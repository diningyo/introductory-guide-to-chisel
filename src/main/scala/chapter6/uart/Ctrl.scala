// See README.md for license details.

package chapter6.uart

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

import chapter5.{FIFO, FIFOIO, FIFORdIO, FIFOWrIO}

import scala.math.{pow, round}

/**
  * UARTの方向
  */
sealed trait UartDirection
case object UartTx extends UartDirection
case object UartRx extends UartDirection

/**
  * UARTの制御モジュール
  * @param baudrate ボーレート
  * @param clockFreq クロックの周波数(MHz)
  */
class TxRxCtrl(baudrate: Int=9600,
               clockFreq: Int=100) extends Module {
  val io = IO(new Bundle {
    val uart = new UartIO
    val r2c = Flipped(new CSR2CtrlIO())
  })

  val durationCount = round(clockFreq * pow(10, 6) / baudrate).toInt

  val m_tx_ctrl = Module(new Ctrl(UartTx, durationCount))
  val m_rx_ctrl = Module(new Ctrl(UartRx, durationCount))

  io.uart.tx := m_tx_ctrl.io.uart
  m_tx_ctrl.io.reg <> io.r2c.tx

  m_rx_ctrl.io.uart := io.uart.rx
  m_rx_ctrl.io.reg <> io.r2c.rx
}

/**
  * UARTの各方向の制御モジュール
  * @param direction UARTの送受信の方向
  * @param durationCount 1bit分のカウント
  */
class Ctrl(direction: UartDirection, durationCount: Int) extends Module {
  val io = IO(new Bundle {
    val uart = direction match {
      case UartTx => Output(UInt(1.W))
      case UartRx => Input(UInt(1.W))
    }
    val reg = direction match {
      case UartTx => Flipped(new FIFORdIO(UInt(8.W)))
      case UartRx => Flipped(new FIFOWrIO(UInt(8.W)))
    }
  })

  val m_stm = Module(new CtrlStateMachine)
  val r_duration_ctr = RegInit(durationCount.U)
  val r_bit_idx = RegInit(0.U(3.W))

  // 受信方向は受信した信号と半周期ずれたところで
  // データを確定させるため初期値をずらす
  val initDurationCount = direction match {
    case UartTx => 0
    case UartRx => durationCount / 2
  }

  // 動作開始のトリガはTx/Rxで異なるため
  // directionをmatch式で処理
  val w_start_req = direction match {
    case UartTx => !io.reg.asInstanceOf[FIFORdIO[UInt]].empty
    case UartRx => !io.uart
  }

  val w_update_req = r_duration_ctr === (durationCount - 1).U
  val w_fin = m_stm.io.state.stop && w_update_req

  when (m_stm.io.state.idle) {
    when (w_start_req) {
      r_duration_ctr := initDurationCount.U
    } .otherwise {
      r_duration_ctr := 0.U
    }
  } .otherwise {
    when (!w_update_req) {
      r_duration_ctr := r_duration_ctr + 1.U
    } .otherwise {
      r_duration_ctr := 0.U
    }
  }

  when (m_stm.io.state.data) {
    when (w_update_req) {
      r_bit_idx := r_bit_idx + 1.U
    }
  } .otherwise {
    r_bit_idx := 0.U
  }

  direction match {
    case UartTx =>
      val reg = io.reg.asInstanceOf[FIFORdIO[UInt]]

      io.uart := MuxCase(1.U, Seq(
        m_stm.io.state.start -> 0.U,
        m_stm.io.state.data -> reg.data(r_bit_idx)
      ))

      reg.enable := m_stm.io.state.stop && w_update_req

    case UartRx =>
      val reg = io.reg.asInstanceOf[FIFOWrIO[UInt]]
      val r_rx_data = RegInit(0.U)

      when (m_stm.io.state.idle && w_start_req) {
        r_rx_data := 0.U
      } .elsewhen (m_stm.io.state.data) {
        when (w_update_req) {
          r_rx_data := r_rx_data | (io.uart << r_bit_idx)
        }
      }
      reg.enable := w_fin
      reg.data := r_rx_data
  }

  // txStm <-> ctrl
  m_stm.io.start_req := w_start_req
  m_stm.io.data_req := m_stm.io.state.start && w_update_req
  m_stm.io.stop_req := m_stm.io.state.data && w_update_req && (r_bit_idx === 7.U)
  m_stm.io.fin := w_fin
}

/**
  * Uart制御モジュールのステートマシン
  */
class CtrlStateMachine extends Module {

  val io = IO(new Bundle {
    val start_req = Input(Bool())
    val data_req = Input(Bool())
    val stop_req = Input(Bool())
    val fin = Input(Bool())

    // state
    val state = Output(new Bundle {
      val idle = Output(Bool())
      val start = Output(Bool())
      val data = Output(Bool())
      val stop = Output(Bool())
    })
  })

  object CtrlState extends ChiselEnum {
    val sIdle = Value
    val sStart = Value
    val sData = Value
    val sStop = Value
  }

  val r_stm = RegInit(CtrlState.sIdle)

  switch (r_stm) {
    is (CtrlState.sIdle) {
      when (io.start_req) {
        r_stm := CtrlState.sStart
      }
    }

    is (CtrlState.sStart) {
      when (io.data_req) {
        r_stm := CtrlState.sData
      }
    }

    is (CtrlState.sData) {
      when (io.stop_req) {
        r_stm := CtrlState.sStop
      }
    }

    is (CtrlState.sStop) {
      when (io.fin) {
        r_stm := CtrlState.sIdle
      }
    }
  }

  // output
  io.state.idle := r_stm === CtrlState.sIdle
  io.state.start := r_stm === CtrlState.sStart
  io.state.data := r_stm === CtrlState.sData
  io.state.stop := r_stm === CtrlState.sStop
}
