// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._
import chisel3.experimental.FixedPoint

/**
  * FixedPointのサンプル
  */
class SampleFixedPoint extends Module {
  val io = IO(new Bundle {})

  val w_fp_a = Wire(FixedPoint(2.W, 1.BP))
  val w_fp_b = 0.125.F(12.W, 3.BP)
  val w_fp_c = -1.5.F(12.W, 10.BP)

  w_fp_a := 0.5.F(2.W, 1.BP)

  // printlnを使うとエラボレート時に型の情報が出力される
  println(w_fp_a)
  println(w_fp_b)
  println(w_fp_c)

  // printf("fpA : %f\n", fpA) // エラーになる
}
