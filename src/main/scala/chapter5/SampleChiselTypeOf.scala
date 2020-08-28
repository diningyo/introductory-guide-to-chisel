// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

class SampleChiselTypeOf1(dataBits: Int) extends Module {

  val io = IO(new Bundle {
    val sel = Input(Bool())
    val in = Input(UInt(dataBits.W))
    val out = Output(UInt(dataBits.W))
  })

  // val a = Reg(UInt(dataBits.W))
  val a = Reg(chiselTypeOf(io.in))

  io.out := 0.U

  when (io.sel) { io.out := io.in }
}
