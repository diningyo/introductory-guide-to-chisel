// See README.md for license details.

package chapter6.sequencer

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

import chapter6.{SimpleIO, SimpleIOParams}

/**
  * Sequencerのステート
  */
object State extends ChiselEnum {
  val sIdle = Value
  val sRX = Value
  val sTX = Value
}

/**
  * Uartのデータをループバックするシーケンサー
  * @param p SimpleIOParamsのインスタンス
  * @param debug trueでデバッグポートが追加される
  */
class Sequencer(p: SimpleIOParams)
               (implicit val debug: Boolean = false) extends Module {

  import State._
  import chapter6.uart.RegInfo._

  val io = IO(new Bundle {
    val sio = new SimpleIO(p)
    val debug_stm = if (debug) Some(Output(State())) else None
  })

  // ステートマシン
  val r_stm = RegInit(sIdle)

  // sIdleステートの制御
  val w_has_rx_data = Wire(Bool())
  val r_read_interval = RegInit(0.U(4.W))
  val w_read_req = r_read_interval === 0xf.U

  when (r_stm === sIdle) {
    r_read_interval := r_read_interval + 1.U
  } .otherwise {
    r_read_interval := 0.U
  }

  w_has_rx_data := (r_stm === sIdle) && io.sio.rddv && io.sio.rddata(0)

  // sRXステートの制御
  val r_rx_data = Reg(UInt(8.W))
  val r_rx_fifo_req = RegNext(w_has_rx_data, false.B)
  val w_done_rx_data = (r_stm === sRX) && io.sio.rddv

  // sTXステートの制御
  val r_wait_data = RegInit(false.B)
  val r_fifo_full = RegInit(false.B)
  val w_done_tx_data = Wire(Bool())
  val w_tx_state_addr = Mux(r_fifo_full, stat.U, txFifo.U)
  val w_tx_state_rden = r_fifo_full && !r_wait_data && (r_stm === sTX)
  val w_tx_state_wren = !r_fifo_full && !r_wait_data && (r_stm === sTX)

  when (w_done_rx_data) {
    r_rx_data := io.sio.rddata
  }

  // ステート遷移時にr_fifo_fullフラグをセット
  when (w_done_rx_data) {
    r_fifo_full := true.B
  } .elsewhen(r_stm === sTX) {
    when (io.sio.rddv) {
      r_fifo_full := io.sio.rddata(3)
    }
  } .otherwise {
    r_fifo_full := false.B
  }

  // sTXステート内の処理切り替え
  when (r_stm === sTX) {
    when (io.sio.rddv) {
      r_wait_data := false.B
    } .elsewhen(w_done_tx_data) {
      r_wait_data := false.B
    } .elsewhen(!r_wait_data) {
      r_wait_data := true.B
    }
  } .otherwise {
    r_wait_data := false.B
  }

  w_done_tx_data := w_tx_state_wren

  // ステートマシン
  switch (r_stm) {
    is (sIdle) {
      when (w_has_rx_data) {
        r_stm := sRX
      }
    }
    is (sRX) {
      when (w_done_rx_data) {
        r_stm := sTX
      }
    }
    is (sTX) {
      when (w_done_tx_data) {
        r_stm := sIdle
      }
    }
  }

  // IOの接続
  io.sio.wren := w_tx_state_wren
  io.sio.wrdata := r_rx_data
  io.sio.rden := w_read_req || r_rx_fifo_req || w_tx_state_rden
  io.sio.addr := MuxCase(stat.U, Seq(
    (r_stm === sRX) -> rxFifo.U,
    (r_stm === sTX) -> w_tx_state_addr
  ))

  // テスト用にステートマシンの値を出力
  if (debug) {
    io.debug_stm.get := r_stm
  }
}
