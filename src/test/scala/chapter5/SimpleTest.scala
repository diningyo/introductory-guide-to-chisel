// See README.md for license details.

package chapter5

import chisel3._
import chisel3.iotesters._

/**
  * 入力a/bをANDしてcに出力
  */
class SampleAnd extends Module {
  val io = IO(new Bundle {
    val a = Input(Bool())
    val b = Input(Bool())
    val c = Output(Bool())
  })

  io.c := io.a && io.b
}

/**
  * SampleAndをテスト
  */
object TestSampleAnd extends App {

  // Chiselのテストを行う際にはiotesters.Driverを使う
  val result = iotesters.Driver.execute(args, () => new SampleAnd) {
    c => new PeekPokeTester(c) {
      // 0 / 0 -> 0
      poke(c.io.a, false)
      poke(c.io.b, false)
      expect(c.io.c, false)
      step(1)

      // 0 / 1 -> 0
      poke(c.io.a, false)
      poke(c.io.b, true)
      expect(c.io.c, false)
      step(1)

      // 1 / 0 -> 0
      poke(c.io.a, true)
      poke(c.io.b, false)
      expect(c.io.c, false)
      step(1)

      // 1 / 1 -> 1
      poke(c.io.a, true)
      poke(c.io.b, true)
      expect(c.io.c, true)
      step(1)
    }
  }

  assert(result, "Test Fail.")
}
