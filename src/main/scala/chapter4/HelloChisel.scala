// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * HelloChisel
  *  ChiselでLチカを行うサンプルモジュール
  */
class HelloChisel extends Module {
  val io = IO(new Bundle {
    val out = Output(Bool())
  })

  val r_count = RegInit(0.U(16.W))
  val r_out = RegInit(false.B)

  r_count := r_count + 1.U

  when (r_count === 0xffff.U) {
    r_out := !r_out
  }

  io.out := r_out
}

/**
  * HelloChiselのRTLを生成するメイン関数
  */
object ElaborateHelloChisel extends App {
  Driver.execute(args, () => new HelloChisel)
}
