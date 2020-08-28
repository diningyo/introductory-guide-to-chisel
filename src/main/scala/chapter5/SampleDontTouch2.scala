// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

class SampleDontTouch2 extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  val w_out = io.in + 1.U

  // dontTouchはただのオブジェクトなので
  // 変数宣言時以外にも使用可能
  dontTouch(w_out)

  io.out := w_out
}
