// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * DecoupledIOを使ったサンプル
  */
class SampleDecoupledIO extends Module {
  val io = IO(new Bundle {
    val out = DecoupledIO(UInt(8.W))    // UIntのオブジェクトを渡す
    val out2 = DecoupledIO(new Bundle { // Bundleのオブジェクトを渡す
      val a = Bool()
      val b = UInt(8.W)
    })
  })

  // 信号的にはout/out2の下にvalid/ready/bitsが生成される
  io.out.valid := true.B && io.out.ready
  io.out.bits := 0xff.U
  io.out2.valid := false.B
  io.out2.bits.a := true.B
  io.out2.bits.b := 0x80.U
}
