// See README.md for license details.

import chisel3.iotesters.PeekPokeTester

/**
  * シミュレーションを実行するRunner
  * chapter4で使ったprintfを表示するのが主な使い方
  */
object SimRunner extends App {

  val genArgs = Array(
    s"-td=test_run_dir"
  )

  args(0) match {
    case "chapter4.SampleLogicalOps" =>
      chisel3.iotesters.Driver.execute(genArgs, () => new chapter4.SampleLogicalOps()) {
        c => new PeekPokeTester(c) {
          poke(c.io.a, true)
          poke(c.io.b, false)
          poke(c.io.c, 0xaa)
          poke(c.io.d, 0x55)
          step(1)
        }
      }
    case c => chisel3.iotesters.Driver.execute(genArgs, GetInstance(c)) {
      c => new PeekPokeTester(c) {
        step(1)
      }
    }
  }
}
