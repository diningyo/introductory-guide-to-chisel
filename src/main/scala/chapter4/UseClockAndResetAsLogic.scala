// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Chiselのclock/resetに明示的にアクセスするサンプル
  */
class UseClockAndResetAsLogic extends Module {
  val io = IO(new Bundle {
    val div2_clock = Output(Clock())
    val delay_reset = Output(Bool())
  })

  io.div2_clock := RegNext(clock)
  io.delay_reset := RegNext(reset, false.B)
}
