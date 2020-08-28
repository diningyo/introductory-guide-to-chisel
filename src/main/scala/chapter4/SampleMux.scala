// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Muxのサンプル
  */
class SampleMux extends Module {
  val io = IO(new Bundle {
    val sel = Input(Bool())
    val in1 = Input(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  io.out := Mux(io.sel, io.in1, io.in2)
}

/**
  * MuxCaseのサンプル
  */
class SampleMuxCase extends Module {
  val io = IO(new Bundle {
    val sel = Input(UInt(2.W))
    val in1 = Input(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val in3 = Input(UInt(8.W))
    val in4 = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  io.out := MuxCase(io.in1, Seq(
    (io.sel === 1.U) -> io.in2,
    (io.sel === 2.U) -> io.in3,
    (io.sel === 3.U) -> io.in4))
}
