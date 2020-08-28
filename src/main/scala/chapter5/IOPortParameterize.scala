// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * IOポートのパラメタライズ
  * @param hasOptInput opt_inを持つかどうか
  * @param hasOptOutput opt_outを持つかどうか
  */
class IOPortParameterize(hasOptInput: Boolean, hasOptOutput: Boolean) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
    // 以下がパラメタライズ対象のポート
    val opt_in = if (hasOptInput) Some(Input(Bool())) else None
    val opt_out = if (hasOptOutput) Some(Output(Bool())) else None
  })

  io.out := io.in + io.opt_in.getOrElse(0.U) // opt_inポートの値を加算

  // opt_outに接続
  if (hasOptOutput) {
    io.opt_out.get := io.in
  }
}