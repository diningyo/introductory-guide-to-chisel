// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

class SampleDontTouch(useDontTouch: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(2, UInt(8.W)))
    val out = Output(Vec(2, UInt(8.W)))
  })

  for ((in, out) <- io.in zip io.out) {
    val r1 = if (useDontTouch) dontTouch(WireDefault(in))
             else              WireDefault(in)
    out := r1
  }
}
