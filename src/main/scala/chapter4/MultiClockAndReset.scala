// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * 複数のクロックとリセットを使うサンプル
  */
class MultiClockAndReset extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out1 = Output(Bool())
    val out2 = Output(Bool())
    val out3_1 = Output(Bool())
    val out3_2 = Output(Bool())
  })

  // resetを遅延
  val r_reset = RegNext(reset)

  // r_resetを使ってリセット
  val r_some_1 = withReset(r_reset) {
    RegInit(false.B)
  }

  io.out1 := r_some_1

  // クロック
  val r_div_clock = RegNext(clock)

  val r_some_2 = withClock(r_div_clock) {
    RegNext(io.in)
  }

  io.out2 := r_some_2

  // 複数のレジスタをまとめて別のドメインにする場合は
  // withXのブロック式で囲んでもOK
  // ただしスコープの関係で出力用のWireが必要
  val w_out3_1 = Wire(Bool())
  val w_out3_2 = Wire(Bool())

  // 引数は(clock, reset)の順番
  withClockAndReset(r_div_clock, r_reset) {
    val r_some3_1 = RegNext(io.in, false.B)
    val r_some3_2 = RegNext(io.in, true.B)

    w_out3_1 := r_some3_1
    w_out3_2 := r_some3_2
  }

  io.out3_1 := w_out3_1
  io.out3_2 := w_out3_2
}
