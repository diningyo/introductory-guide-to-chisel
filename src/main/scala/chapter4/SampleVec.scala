// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Vecのサンプル
  */
class SampleVec extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(2, UInt(8.W)))
    val out = Output(UInt(8.W))
  })
  io.out := io.in(0)
}

/**
  * VecInitのサンプル
  */
class SampleVecInit extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val out1 = Output(Vec(2, UInt(8.W)))
    val out2 = Output(Vec(2, UInt(8.W)))
  })

  val w_vec_init_1 = VecInit(Seq(io.in1, io.in2))
  val w_vec_init_2 = VecInit(io.in1, io.in2)

  io.out1 := w_vec_init_1
  io.out2 := w_vec_init_2
}