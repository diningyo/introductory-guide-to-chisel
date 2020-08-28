// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * 非同期リセットのサンプル
  */
class SampleAsyncReset extends Module {
  val io = IO(new Bundle {
    val out = Output(Bool())
  })

  // asAsyncResetでresetをAsyncReset型に変更
  val w_async_reset = reset.asAsyncReset()

  // 非同期リセットをwithResetで指定
  val r_out = withReset(w_async_reset) { RegInit(false.B) }

  r_out := 1.U

  io.out := r_out
}
