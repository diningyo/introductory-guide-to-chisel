// See README.md for license details.

package chapter6.uart

import chapter6.SimpleIOParams

import scala.math.{floor, random}
import chisel3.iotesters._
import test.util.BaseTester

/**
  * CSRモジュールのテスト制御クラス
  * @param c CSR
  */
class CSRUnitTester(c: CSR) extends PeekPokeTester(c) {

  /**
    * アイドル
    * @param cycle アイドルのサイクル数
    */
  def idle(cycle: Int = 1): Unit = {
    poke(c.io.sram.wren, false)
    poke(c.io.sram.rden, false)
    step(cycle)
  }

  /**
    * レジスタライト
    * @param addr レジスタのアドレス
    * @param data ライトデータ
    */
  def hwrite(addr: Int, data: Int): Unit = {
    poke(c.io.sram.addr, addr)
    poke(c.io.sram.wren, true)
    poke(c.io.sram.wrdata, data)
    step(1)
  }

  /**
    * レジスタリード
    * @param addr レジスタのアドレス
    * @param exp リードの期待値
    */
  def hread(addr: Int, exp: Int): Unit = {
    poke(c.io.sram.addr, addr)
    poke(c.io.sram.rden, true)
    step(1)
    expect(c.io.sram.rddv, true)
    expect(c.io.sram.rddata, exp)
  }

  /**
    * 受信FIFOへのライト
    * @param data register write data
    */
  def uwrite(data: Int): Unit = {
    poke(c.io.r2c.rx.enable, true)
    poke(c.io.r2c.rx.data, data)
    step(1)
    poke(c.io.r2c.rx.enable, false)
    step(1)
  }

  /**
    * 送信FIFOのenable発行
    */
  def txfifoAck(): Unit = {
    poke(c.io.r2c.tx.enable, true)
    step(1)
  }
}

/**
  * CSRのテストクラス
  */
class CSRTester extends BaseTester {

  val dutName = "CSR"

  behavior of dutName

  val sp = SimpleIOParams()

  it should "送信FIFOにライトできる" in {

    val outDir = dutName + "-txfifo"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new CSR(sp)(true)) {
      c => new CSRUnitTester(c) {
        val txData = Range(0, 10).map(_ => floor(random * 256).toInt)

        idle()
        for (d <- txData) {
          hwrite(RegInfo.txFifo, d)
          expect(c.io.dbg.get.tx_fifo, d)
        }
      }
    } should be (true)
  }

  it should "送信FIFOにライトするとtx_emptyビットが0に遷移する" in {

    val outDir = dutName + "-txfifo-txempty"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new CSR(sp)(true)) {
      c => new CSRUnitTester(c) {
        val txData = 0xff

        idle()
        expect(c.io.r2c.tx.empty, true)
        hwrite(RegInfo.txFifo, txData)
        expect(c.io.r2c.tx.empty, false)
      }
    } should be (true)
  }

  it should "be able to read Stat register from Host" in {
    val outDir = dutName + "-stat-txempty"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new CSR(sp)(true)) {
      c => new CSRUnitTester(c) {
        val txData = 0xff

        idle()
        expect(c.io.r2c.tx.empty, true)
        hwrite(RegInfo.txFifo, txData)
        expect(c.io.r2c.tx.empty, false)
      }
    } should be (true)  }

  // Ctrlレジスタは使用していないのでテスト実行対象から除外
  // その場合は次のように"ignore"を指定する
  ignore should "be able to read Ctrl register from Host" in {
  }

  // ここからは受信FIFOのテストなので"behaviour"を変更している
  behavior of "RxFifo"

  it should "UARTの制御ブロック側から受信FIFOにライトできる" in {
    val outDir = dutName + "-rxfifo-write"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new CSR(sp)(true)) {
      c => new CSRUnitTester(c) {
        val txData = Range(0, 10).map(_ => floor(random * 256).toInt)

        idle()
        for (d <- txData) {
          uwrite(d)
          expect(c.io.dbg.get.rx_fifo, txData(0))
        }
      }
    } should be (true)
  }

  it should "ホスト側からは受信FIFOをリードできる" in {

    val outDir = dutName + "-rxfifo-read"
    val args = getArgs(Map(
      "--top-name" -> dutName,
      "--target-dir" -> s"test_run_dir/$outDir"
    ))

    Driver.execute(args, () => new CSR(sp)(true)) {
      c => new CSRUnitTester(c) {
        val txData = Range(0, 10).map(_ => floor(random * 256).toInt)

        idle()
        for (d <- txData) {
          hwrite(RegInfo.txFifo, d)
          expect(c.io.dbg.get.tx_fifo, d)
        }
      }
    } should be (true)
  }
}
