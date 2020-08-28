// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

/**
  * Switch ~ Isのサンプル
  */
class SampleSwitch extends Module {
  val io = IO(new Bundle {
    val start = Input(Bool())
    val stop = Input(Bool())
    val out_state = Output(UInt(2.W))
  })

  // ChiselのEnum
  // こちらはUIntの値を持つ
  val sIdle :: sRun :: Nil = Enum(2)

  // Chisel3.2.0で導入されたChiselEnum（要import）
  // objectになるため、スコープを別にできる
  // また、こちらは任意の値を割り振れる
  object State extends ChiselEnum {
    val sIdle = Value
    val sRun = Value(2.U)
  }

  val r_state = RegInit(State.sIdle)

  switch (r_state) {
    is (State.sIdle) {
      r_state := State.sRun
      io.out_state := 0.U
    }
    is (State.sRun) {
      when (io.stop) {
        r_state := State.sIdle
      }
    }
  }

  // 型が違うのでUIntへの変換が必要
  io.out_state := r_state.asUInt()
}