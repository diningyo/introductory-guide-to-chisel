// See README.md for license details.

package chapter6.uart

import chisel3._
import chisel3.util._
import chapter6.{SimpleIO, SimpleIOParams}

import scala.math.{pow, round}

/**
  * UARTのIOクラス
  */
class UartIO extends Bundle {
  val tx = Output(UInt(1.W))
  val rx = Input(UInt(1.W))
}

class UartTopIO(p: SimpleIOParams)
               (implicit debug: Boolean = false)extends Bundle {
  val mbus = Flipped(new SimpleIO(p))
  val uart= new UartIO
  val dbg = if (debug) Some(new CSRDebugIO) else None

  override def cloneType: this.type =
    new UartTopIO(p).asInstanceOf[this.type]
}

/**
  * Uartのトップモジュール
  * @param baudrate ボーレート
  * @param clockFreq クロックの周波数(MHz)
  */
class UartTop(baudrate: Int, clockFreq: Int) extends Module {

  val p = SimpleIOParams()

  val io = IO(new UartTopIO(p))

  val m_reg = Module(new CSR(p))
  val m_ctrl = Module(new TxRxCtrl(baudrate, clockFreq))

  io.uart <> m_ctrl.io.uart

  io.mbus <> m_reg.io.sram
  m_reg.io.r2c <> m_ctrl.io.r2c
}
