// See README.md for license details.

package chapter4

import math.pow

import chisel3.iotesters._

/**
  * HelloChiselのテストモジュール
  */
class HelloChiselTester extends ChiselFlatSpec {

  behavior of "HelloChisel"

  it should "io.outは指定の周期で変化する" in {

    // テストのために周期を短く設定
    val freq = 50 * pow(10, 3).toInt // 50kHz
    val interval = 500               // 500msec

    Driver.execute(Array(
      "-tn=HelloChisel",
      "-td=test_run_dir/chapter4/HelloChisel",
      "-tgvo=on", "-tbn=verilator"), () => new HelloChisel(freq, interval)) {
      c => new PeekPokeTester(c) {
        reset()

        // リセット直後はLow
        expect(c.io.out, false)

        // その後は25000サイクル周期で変化する
        val expData = Range(0, 16).map(_ % 2)
        for (exp <- expData) {
          var i = 0
          while (i != (25 * pow(10, 3)).toInt) {
            expect(c.io.out, exp)
            step(1)
            i += 1
          }
        }
      }
    } should be (true)
  }
}