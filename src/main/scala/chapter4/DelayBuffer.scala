// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
 * 1cycle遅らせるだけのモジュール
 */
class DelayBuffer extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  io.out := RegNext(io.in, false.B)
}
