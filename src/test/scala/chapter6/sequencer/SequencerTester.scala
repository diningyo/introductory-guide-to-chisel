// See README.md for license details.

package chapter6.sequencer

import chapter6.SimpleIOParams
import chisel3.iotesters._
import test.util.BaseTester

import scala.math.abs
import scala.util.Random

/**
  * Unit tester for TxRxCtrl module.
  * @param c dut module (instance of TxRXCtrl)
  */
class SequencerUnitTester(c: SimDTMSequencer) extends PeekPokeTester(c) {

  import chapter6.uart._

  val memAccLimit = 10
  val timeOutCycle = 1000

  var i = 0

  val sio = c.io.t

  def waitRead(): Unit = {
    while (peek(sio.rden) != 0x1) {
      step(1)
    }
  }

  def retRead(data: Int): Unit = {
    poke(sio.rddv, true)
    poke(sio.rddata, data)
    step(1)
    poke(sio.rddv, false)
  }

  /**
    *
    */
  def idleTest(): Unit = {
    val statData = Seq(0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01)

    for (stat <- statData) {
      expect(c.io.debug_stm, State.sIdle)

      // ステータスレジスタのリード待ち
      waitRead()

      // アイドル時はずっとステータスレジスタをリード
      expect(sio.addr, RegInfo.stat)
      poke(sio.rddv, true)
      // statDataの中身を順にリード値として返却
      poke(sio.rddata, stat)
      step(1)
      poke(sio.rddv, false)
    }

    // 最後のリードでステートが遷移することを確認
    expect(c.io.debug_stm, State.sRX)
  }

  /**
    *
    * @param rxData
    */
  def rxTest(rxData: Int): Unit = {
    expect(c.io.debug_stm, State.sIdle)

    // ステータスレジスタのリード待ち
    waitRead()
    step(1)
    retRead(0x1)

    // ステートの遷移を確認
    expect(c.io.debug_stm, State.sRX)
    waitRead()
    expect(sio.addr, RegInfo.rxFifo)
    step(1)
    retRead(rxData)
    expect(c.io.debug_stm, State.sTX)
  }

  /**
    *
    * @param txData
    * @param statData
    */
  def txTest(txData: Int, statData: Seq[Int]): Unit = {

    rxTest(txData)

    // ステートの遷移を確認
    expect(c.io.debug_stm, State.sTX)

    for (stat <- statData) {
      waitRead()
      expect(sio.addr, RegInfo.stat)
      step(1)
      retRead(stat)
    }

    expect(c.io.t.wren, true)
    expect(c.io.t.wrdata, txData)
    step(1)
    expect(c.io.t.wren, false)

    expect(c.io.debug_stm, State.sIdle)
  }
}

class SequencerTester extends BaseTester {

  val dutName = "chapter6.sequencer.Sequencer"

  behavior of dutName

  val p = SimpleIOParams()
  val limit = 1000
  implicit val debug = true

  it should s"データを受信するまではアイドルステートに留まる. [$dutName-000]" in {
    val outDir = dutName + "-000"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new SimDTMSequencer(p)(limit)) {
      c => new SequencerUnitTester(c) {
        idleTest()
        step(5)
      }
    } should be (true)
  }

  it should s"データの受信ステータスを確認後、rx_fifoをリードしデータを受信する. [$dutName-001]" in {
    val outDir = dutName + "-001"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new SimDTMSequencer(p)(limit)) {
      c => new SequencerUnitTester(c) {
        rxTest(0xa5)
        step(5)
      }
    } should be (true)
  }

  it should s"データを受信後、送信FIFOに受信データをライトする. [$dutName-002]" in {
    val outDir = dutName + "-002"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new SimDTMSequencer(p)(limit)) {
      c => new SequencerUnitTester(c) {
        txTest(0xff, Seq(0x0)) // 送信FIFOに空きあり
        step(5)
      }
    } should be (true)
  }

  it should s"データを受信後に送信FIFOがフルの場合は、空くまでステータスレジスタをポーリングする. [$dutName-003]" in {
    val outDir = dutName + "-003"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new SimDTMSequencer(p)(limit)) {
      c => new SequencerUnitTester(c) {
        txTest(0xff, Seq.fill(100)(0xc) :+ 0x4) // 送信FIFOに空きなし→ありのパターン
        step(5)
      }
    } should be (true)
  }

  it should s"連続で処理をしてもステートが正常に遷移する. [$dutName-004]" in {
    val outDir = dutName + "-004"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    val r = new Random(1)

    Driver.execute(args, () => new SimDTMSequencer(p)(limit * 10)) {
      c => new SequencerUnitTester(c) {

        for (_ <- 0 until 10) {
          val numOfStat = abs(r.nextInt(31)) + 1
          val statData = Range(0, numOfStat).map(_ => r.nextInt() & 0xf7 | 0x8) :+ 0x4
          txTest(r.nextInt() & 0xff, statData) // 送信FIFOに空きなし→ありのパターン
          step(5)
        }
      }
    } should be (true)
  }
}
