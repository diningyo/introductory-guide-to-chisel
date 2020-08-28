// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * MixedVecのサンプル
  */
class SampleMixedVec extends Module {
  val io = IO(new Bundle {
    val in = Input(MixedVec(Seq(Bool(), UInt(2.W), UInt(8.W))))
  })
}

/**
  * MixedVecInitのサンプル
  */
class SampleMixedVecInit extends Module {
  val io = IO(new Bundle {
    val in1 = Input(Bool())
    val in2 = Input(UInt(8.W))
    val out1 = Output(Vec(2, UInt(8.W)))
    val out2 = Output(Vec(2, UInt(8.W)))
  })

  val w_mvec_init_1 = MixedVecInit(Seq(io.in1, io.in2))
  val w_mvec_init_2 = MixedVecInit(io.in1, io.in2)

  io.out1 := w_mvec_init_1
  io.out2 := w_mvec_init_2
}