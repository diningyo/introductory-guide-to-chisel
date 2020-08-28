// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * FIFO リード側 I/O
  */
class SomeData extends Bundle {
  val data = UInt(8.W)
}

/**
  * FIFO ライト側 I/O
  */
class OtherData extends Bundle {
  val strb = UInt(4.W)
  val data = UInt(32.W)
  val last = Bool()
}

/**
  * 汎用FIFOを使うサンプル
  */
class UseFIFO extends Module {
  val io = IO(new Bundle {
    val wren = Input(Bool())
    val rden = Input(Bool())
    val in_some = Input(new SomeData)
    val in_other = Input(new OtherData)
    val out_some = Output(new SomeData)
    val out_other = Output(new OtherData)
  })

  val m_some_q = Module(new FIFO(chiselTypeOf(io.in_some)))

  m_some_q.io.wr.enable := io.wren
  m_some_q.io.wr.data := io.in_some
  m_some_q.io.rd.enable := io.wren
  io.out_some := m_some_q.io.rd.data

  val m_other_q = Module(new FIFO(chiselTypeOf(io.in_other)))

  m_other_q.io.wr.enable := io.wren
  m_other_q.io.wr.data := io.in_other
  m_other_q.io.rd.enable := io.wren
  io.out_other := m_other_q.io.rd.data
}
