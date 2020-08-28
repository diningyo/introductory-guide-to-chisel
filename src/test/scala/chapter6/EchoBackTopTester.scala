// See README.md for license details.

package chapter6

import chisel3.iotesters._
import test.util.BaseTester

import scala.math.{abs, pow, round}
import scala.util.Random

/**
  * EchoBackTopのテストモデル
  * @param c SimCTMEchoBackTopのインスタンス
  */
class EchoBackerUnitTester(c: SimDTMEchoBackTop,
                           baudrate: Int, clockFreq: Int)
  extends PeekPokeTester(c) {

  val memAccLimit = 10
  val timeOutCycle = 1000
  val duration = round(clockFreq * pow(10, 6) / baudrate).toInt

  var i = 0

  val rx = c.io.uart_rx

  /**
    * Uart data receive
    * @param exp expect value
    */
  def receive(exp: Int): Unit = {

    // detect start
    while (peek(rx) == 0x1) {
      step(1)
    }

    // shift half period
    for (_ <- Range(0, duration / 2)) {
      step(1)
    }

    expect(rx, false, "detect bit must be low")

    for (idx <- Range(0, 8)) {
      val expTxBit = (exp >> idx) & 0x1
      for (_ <- Range(0, duration)) {
        step(1)
      }
      expect(rx, expTxBit, s"don't match exp value bit($idx) : exp = $expTxBit")
    }

    // stop bits
    for (_ <- Range(0, duration)) {
      step(1)
    }

    // check stop bit value
    expect(rx, true, s"stop bit must be high")
  }
}

/**
  * EchoBackTopのテストクラス
  */
class EchoBackTopTester extends BaseTester {

  val dutName = "chapter6.EchoBackTop"

  behavior of dutName

  val p = SimpleIOParams()
  val limit = 1000000
  implicit val debug = true

  it should "UARTに送ったデータがループバックして受信できる。" +
    s" [$dutName-000]" in {
    val outDir = dutName + "-000"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    val test = Seq(0xa5, 0x2a, 0x35)
    val baudrate = 960000
    val clockFreq = 100

    info(s"baudtate    = $baudrate Hz")
    info(s"clock freq. = $clockFreq MHz")

    Driver.execute(args,
      () => new SimDTMEchoBackTop(p, baudrate, clockFreq)(test, limit = limit)) {
      c => new EchoBackerUnitTester(c, baudrate, clockFreq) {
        for (d <- test) {
          receive(d)
        }
        step(100)
      }
    } should be (true)
  }

  it should "UARTに送ったデータがループバックして受信できる。" +
    s" [$dutName-001]" in {
    val outDir = dutName + "-000"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    val test = Seq(0xa5, 0x2a, 0x35)

    // 周波数が1MHzの場合設定値上は666666がMaxだが
    // 実際には500000で動くことになる(四捨五入でduration=2になるから）
    // 理論上のbaurateの上限は周波数の1/2
    val baudrate: Int = 500000
    val clockFreq: Int = 1

    info(s"baudtate    = $baudrate Hz")
    info(s"clock freq. = $clockFreq MHz")

    Driver.execute(args,
      () => new SimDTMEchoBackTop(p, baudrate, clockFreq)(test, limit = limit)) {
      c => new EchoBackerUnitTester(c, baudrate, clockFreq) {
        for (d <- test) {
          receive(d)
        }
        step(100)
      }
    } should be (true)
  }
}
