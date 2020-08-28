// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * ChiselのArbiterを使ったサンプル
  */
class SampleArbiter extends Module {
  val io = IO(new Bundle {
    val in = Flipped(Vec(4, DecoupledIO(UInt(8.W))))
    val out = DecoupledIO(UInt(8.W))
  })

  val m_arb = Module(new Arbiter(chiselTypeOf(io.in(0).bits), 4))

  m_arb.io.in <> io.in
  io.out <> m_arb.io.out
}
