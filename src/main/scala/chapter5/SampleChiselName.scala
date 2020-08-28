// See README.md for license details.

package chapter5

import chisel3._
import chisel3.internal.naming.chiselName
import chisel3.util._

/**
  * chiselNameのサンプル
  */
// Moduleの宣言の前にアノテーションを付与するだけ
@chiselName
class SampleChiselName extends Module {
  val io = IO(new Bundle {
    val sel = Input(Bool())
    val in = Input(UInt(8.W))
    val out = Output(UInt(9.W))
  })

  io.out := io.in

  // io.sel == 1のときだけ入力を2倍する
  when (io.sel) {
    val w_out = io.in * 2.U
    io.out := w_out
  }
}

object ElaborateSampleChiselName extends App {
  Driver.execute(args, () => new SampleChiselName)
}
