// See README.md for license details.

package chapter4

import chisel3._
import chisel3.iotesters._

/**
  * Boolを他の型にキャスト
  */
class CastBool extends Module {
  val io = IO(new Bundle {})

  // Bool型を他の型に変換
  val w_bool = false.B
  val w_bool_to_uint = w_bool.asUInt()
  val w_bool_to_sint = w_bool.asSInt()
  val w_bool_to_fp = w_bool.asFixedPoint(1.BP) // 小数点部の指定が必要

  println(w_bool_to_uint)
  println(w_bool_to_sint)
  println(w_bool_to_fp)
}

/**
  * UIntを他の型にキャスト
  */
class CastUInt extends Module {
  val io = IO(new Bundle {})

  // UInt型を他の型に変換
  val w_uint = 0x1.U
  val w_uint_to_bool = w_uint.asBool
  val w_uint_to_bools = w_uint.asBools
  val w_uint_to_sint = w_uint.asSInt()
  val w_uint_to_fp = w_uint.asFixedPoint(1.BP) // 小数点部の指定が必要

  println(w_uint_to_bool)
  println(w_uint_to_bools)
  println(w_uint_to_sint)
  println(w_uint_to_fp)
}

/**
  * SIntを他の型にキャスト
  */
class CastSInt extends Module {
  val io = IO(new Bundle {})

  // SInt型を他の型に変換
  val w_sint = 0x1.U
  val w_sint_to_bool = w_sint.asBool
  val w_sint_to_bools = w_sint.asBools
  val w_sint_to_uint = w_sint.asSInt()
  val w_sint_to_fp = w_sint.asFixedPoint(1.BP) // 小数点部の指定が必要

  println(w_sint_to_bool)
  println(w_sint_to_bools)
  println(w_sint_to_uint)
  println(w_sint_to_fp)
}

/**
  * FixedPointをを他の型にキャスト
  */
class CastFixedPoint extends Module {
  val io = IO(new Bundle {})

  // FixedPoint型を他の型に変換
  val w_fp = -1.5.F(3.W, 1.BP)
  //val fpToBool = fp.asBool
  val w_fp_to_bools = w_fp.asBools
  val w_fp_to_uint = w_fp.asUInt()
  val w_fp_to_sint = w_fp.asFixedPoint(1.BP)

  println(w_fp)
  println(w_fp_to_bools)
  println(w_fp_to_uint)
  println(w_fp_to_sint)
}