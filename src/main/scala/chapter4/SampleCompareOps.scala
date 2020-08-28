// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Chiselの比較演算のサンプル
  */
class SampleCompareOps extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(8.W))
    val b = Input(UInt(8.W))
  })

  val a = io.a
  val b = io.b

  val w_equal = a === b
  val w_not_equal = a =/= b
  val w_less_than = a < b
  val w_greater_than = a > b
  val w_less_than_equal = a <= b
  val w_greater_than_equal = a >= b

  println(w_equal)
  println(w_not_equal)
  println(w_less_than)
  println(w_greater_than)
  println(w_less_than_equal)
  println(w_greater_than_equal)

  printf("w_equal              : %b\n", w_equal)
  printf("w_not_equal          : %b\n", w_not_equal)
  printf("w_less_than          : %b\n", w_less_than)
  printf("w_greater_than       : %b\n", w_greater_than)
  printf("w_less_than_equal    : %b\n", w_less_than_equal)
  printf("w_greater_than_equal : %b\n", w_greater_than_equal)
}
