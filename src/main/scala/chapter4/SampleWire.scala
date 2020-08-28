// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Wire/WireDefaultのサンプル
  */
class SampleWire extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val in3 = Input(UInt(8.W))
    val out1 = Output(UInt(8.W))
    val out2 = Output(UInt(8.W))
    val out3 = Output(UInt(8.W))
  })

  val w_wire1 = Wire(UInt(8.W))
  w_wire1 := io.in1

  val w_wire2 = io.in2
  val w_wire3 = WireDefault(io.in3)

  //w_wire2 := 1.U
  w_wire3 := 1.U

  io.out1 := w_wire1
  io.out2 := w_wire2
  io.out3 := w_wire3
}