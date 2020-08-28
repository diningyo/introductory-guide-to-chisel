// See README.md for license details.

package chapter4

import chisel3.iotesters._

/**
  * HelloChiselのテストモジュール
  */
class HelloChiselTester extends ChiselFlatSpec {

  behavior of "HelloChisel"

  it should "io.outは65536サイクル周期で変化する" in {
    Driver.execute(Array(
      "-tn=HelloChisel",
      "-td=test_run_dir/chapter4/HelloChisel",
      "-tgvo=on", "-tbn=verilator"), () => new HelloChisel) {
      c => new PeekPokeTester(c) {
        reset()

        // リセット直後はLow
        expect(c.io.out, false)

        // その後は65536周期で変化する
        val expData = Range(0, 16).map(_ % 2)
        for (exp <- expData) {
          var i = 0
          while (i != 0x10000) {
            expect(c.io.out, exp)
            step(1)
            i += 1
          }
        }
      }
    } should be (true)
  }
}