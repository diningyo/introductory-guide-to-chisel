// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Chiselの連接のサンプル
  */
class SampleCatOps extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(8.W))
    val b = Input(UInt(8.W))
    val c = Input(UInt(8.W))
    val d = Input(UInt(8.W))
  })

  val a = io.a
  val b = io.b
  val c = io.c
  val d = io.d

  val w_cat0 = a ## b ## c ## d
  val w_cat1 = Cat(a, b, c, c)
  val w_cat2 = Cat(Seq(a, b, c, d))
  //val cat2Error = Cat(a, Seq(b, c, d)) // 型の混在はNG
  val w_vec3 = WireInit(VecInit(Seq(a, b, c, d)))
  val w_cat3 = Cat(w_vec3)
  val w_vec4 = WireInit(VecInit(Seq(b, c, d)))
  //val cat4Error = Cat(a, vec4) // こちらもUIntとVec[UInt]で型が混在するのでNG

  printf("w_cat0 : %d'h%x\n", w_cat0.getWidth.U, w_cat0)
  printf("w_cat1 : %d'h%x\n", w_cat1.getWidth.U, w_cat1)
  printf("w_cat2 : %d'h%x\n", w_cat2.getWidth.U, w_cat2)
  printf("w_cat3 : %d'h%x\n", w_cat3.getWidth.U, w_cat3)
}