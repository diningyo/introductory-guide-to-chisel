// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

class ThroughIO extends Bundle {
  val in = Input(Bool())
  val out = Output(Bool())
}

class Delay extends Module {
  val io = IO(new ThroughIO)

  io.out := RegNext(io.in, false.B)
}

class SampleRequireAsyncReset1 extends Module with RequireAsyncReset {
  val io = IO(new ThroughIO)

  val m_delay =  Module(new Delay)

  io <> m_delay.io
}
