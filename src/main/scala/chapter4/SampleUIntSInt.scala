// See README.md for license details.

package chapter4

import chisel3._
import chisel3.iotesters._
import chisel3.util._

/**
  * UInt/SIntのサンプル
  */
class SampleUIntSInt extends Module {
  val io = IO(new Bundle {})

  val w_uint_a = Wire(UInt(8.W))
  val w_uint_b = 0xff.U
  val w_uint_c = "b1111".U(4.W)

  println(w_uint_a)
  println(w_uint_b)
  println(s" - bit width = ${w_uint_b.getWidth}")
  println(w_uint_c)

  w_uint_a := "hff".U


  printf("w_uint_a = %d\n", w_uint_a)
  printf("w_uint_b = %d\n", w_uint_b)
  printf("w_uint_c = %d\n", w_uint_c)

  val w_sint_a = Wire(SInt(8.W))
  val w_sint_b = 0xff.S // データ8bit + 符号ビットで9bitの信号になる
  val w_sint_c = -1.S(4.W)

  println(s" - bit width = ${w_uint_c.getWidth}")
  println(w_sint_a)
  println(w_sint_b)
  println(w_sint_c)

  w_sint_a := 0xff.S

  printf("w_sint_a = %d\n", w_sint_a)
  printf("w_sint_b = %d\n", w_sint_b)
  printf("w_sint_c = %d\n", w_sint_c)
}
