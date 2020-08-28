// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * ChiselのBool型のサンプル
  */
class SampleBool extends Module {
  val io = IO(new Bundle {})

  //val w_a = Bool() Chiselの型のオブジェクト
  val w_b = true.B
  val w_c = false.B

  // printlnを使うとエラボレート時に型の情報が出力される
  println(w_b)
  println(w_c)

  // printfでシミューション実行時の値を出力可能
  //printf("w_a = %d\n", w_a)
  printf("w_b = %d\n", w_b)
  printf("w_c = %d\n", w_c)
}