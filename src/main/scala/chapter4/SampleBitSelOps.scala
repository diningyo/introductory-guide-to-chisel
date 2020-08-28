// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Chiselのビット選択処理のサンプル
  */
class SampleBitSelOps extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(8.W))
  })

  val a = io.a

  val w_single_bit = a(7)      // 単一のビット選択
  val w_multi_bits = a(7, 4)   // 連続するビットの選択
  val w_head_bits = a.head(4)  // MSBから4bit
  val w_tail_bits = a.tail(4)  // LSBから4bit

  printf("singleBit : %d'h%x\n", w_single_bit.getWidth.U, w_single_bit)
  printf("multiBits : %d'h%x\n", w_multi_bits.getWidth.U, w_multi_bits)
  printf("headBits  : %d'h%x\n", w_head_bits.getWidth.U, w_head_bits)
  printf("tailBits  : %d'h%x\n", w_tail_bits.getWidth.U, w_tail_bits)
}