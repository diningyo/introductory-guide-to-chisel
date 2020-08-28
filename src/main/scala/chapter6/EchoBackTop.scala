// See README.md for license details.

package chapter6

import chisel3._
import chisel3.util._

import chapter6.uart._
import chapter6.sequencer._

/**
  * EchoBackTop
  * @param p SimpleIOのパラメータ
  * @param baudrate ボーレート
  * @param clockFreq クロック周波数(MHz)
  */
class EchoBackTop(p: SimpleIOParams, baudrate: Int = 9600, clockFreq: Int = 100) extends Module {
  val io = IO(new UartIO)

  io.tx := io.rx

  val m_seq = Module(new Sequencer(p))
  val m_uart = Module(new UartTop(baudrate, clockFreq))

  m_uart.io.mbus <> m_seq.io.sio
  io <> m_uart.io.uart
}