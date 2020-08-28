// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Chiselの四則演算のサンプル
  */
class SampleArithmeticOps extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(8.W))
    val b = Input(UInt(8.W))
  })

  val a = io.a
  val b = io.b

  val w_add_init = a + b
  val w_sub_init = a - b
  val w_add_init2 = a +& b
  val w_sub_init2 = a -& b
  val w_mul_init = a * b
  val w_div_init = a / b
  val w_mod_init = a % b

  val w_add_init3 = Wire(UInt(8.W))
  val w_sub_init3 = Wire(UInt(8.W))

  w_add_init3 := a +& b
  w_sub_init3 := a -& b

  println(s"w_add_init Width = ${w_add_init.getWidth}")
  println(s"w_add_init2 Width = ${w_add_init2.getWidth}")
  println(s"w_add_init3 Width = ${w_add_init3.getWidth}")
  println(s"w_sub_init Width = ${w_add_init.getWidth}")
  println(s"w_sub_init2 Width = ${w_add_init2.getWidth}")
  println(s"w_sub_init3 Width = ${w_add_init3.getWidth}")
  println(s"w_mul_init Width = ${w_mul_init.getWidth}")
  println(s"w_div_init2 Width = ${w_div_init.getWidth}")
  println(s"w_mod_init Width = ${w_mod_init.getWidth}")

  printf("add_init : %d\n", w_add_init)
  printf("sub_init : %d\n", w_sub_init)
  printf("mul_init : %d\n", w_mul_init)
  printf("div_init : %d\n", w_div_init)
  printf("mod_init : %d\n", w_mod_init)
}