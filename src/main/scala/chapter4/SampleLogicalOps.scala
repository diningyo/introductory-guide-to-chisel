// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Chiselの論理演算のサンプル
  */
class SampleLogicalOps extends Module {
  val io = IO(new Bundle {
    val a = Input(Bool())
    val b = Input(Bool())
    val c = Input(UInt(8.W))
    val d = Input(UInt(8.W))
  })

  val a = io.a
  val b = io.b
  val c = io.c
  val d = io.d

  val w_and = a && b
  val w_or = a || b
  val w_not = !a
  val w_xor = a ^ b
  //val bitwise_and = c && d // UIntなのでError
  //val bitwise_or = c || d  // UIntなのでError
  val w_bitwise_and = c & d
  val w_bitwise_or = c | d
  val w_bitwise_not = ~c
  val w_bitwise_xor = c ^ d
  val w_reduction_and = c.andR
  val w_reduction_or = c.orR
  val w_reduction_xor = c.xorR

  printf("w_and           : %d'b%b\n", w_and.getWidth.U, w_and)
  printf("w_or            : %d'b%b\n", w_or.getWidth.U, w_or)
  printf("w_not           : %d'b%b\n", w_not.getWidth.U, w_not)
  printf("w_xor           : %d'b%b\n", w_xor.getWidth.U, w_xor)
  printf("w_bitwise_and   : %d'b%b\n", w_bitwise_and.getWidth.U, w_bitwise_and)
  printf("w_bitwise_or    : %d'b%b\n", w_bitwise_or.getWidth.U, w_bitwise_or)
  printf("w_bitwise_not   : %d'b%b\n", w_bitwise_not.getWidth.U, w_bitwise_not)
  printf("w_bitwise_xor   : %d'b%b\n", w_bitwise_xor.getWidth.U, w_bitwise_xor)
  printf("w_reduction_and : %d'b%b\n", w_reduction_and.getWidth.U, w_reduction_and)
  printf("w_reduction_or  : %d'b%b\n", w_reduction_or.getWidth.U, w_reduction_or)
  printf("w_reduction_xor : %d'b%b\n", w_reduction_xor.getWidth.U, w_reduction_xor)
}
