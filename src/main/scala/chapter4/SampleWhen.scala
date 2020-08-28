// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Whenのサンプル
  */
class SampleWhen extends Module {
  val io = IO(new Bundle {
    val cond1 =  Input(Bool())
    val someVal2 = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  when (io.cond1) {
    io.out := 0.U
  } .elsewhen (io.someVal2 === 1.U) {
    io.out := 1.U
  } .otherwise {
    io.out := 0xff.U
  }
}