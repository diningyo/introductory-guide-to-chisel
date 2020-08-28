// See README.md for license details.

package chapter1

import chisel3.iotesters._
import test.util.BaseTester

import scala.math.{floor, random}

/**
  * FIFOの単体テストクラス
  * @param c FIFOモジュールのインスタンス
  */
class FIFOUnitTester(c: FIFO) extends PeekPokeTester(c) {

  /**
    * アイドル
    */
  def idle(): Unit = {
    poke(c.io.rd.enable, false)
    poke(c.io.wr.enable, false)
    step(1)
  }

  /**
    * FIFOにデータを書き込む
    * @param data データ
    */
  def push(data: BigInt): Unit = {
    poke(c.io.wr.enable, true)
    poke(c.io.wr.data, data)
    step(1)
  }

  /**
    * FIFOのデータを読みだし、期待値と比較
    * @param exp 期待値
    */
  def pop(exp: BigInt): Unit = {
    expect(c.io.rd.data, exp)
    poke(c.io.rd.enable, true)
    step(1)
  }

  /**
    * プッシュとポップを同時に行う
    * @param data 設定するデータ
    * @param exp 期待値
    */
  def pushAndPop(data: BigInt, exp: BigInt): Unit = {
    expect(c.io.rd.data, exp)
    poke(c.io.rd.enable, true)
    poke(c.io.wr.enable, true)
    poke(c.io.wr.data, data)
    step(1)
  }
}

/**
  * FIFOのテストクラス
  */
class FIFOTester extends ChiselFlatSpec {
  val dutName = "chapter1.FIFO"
  val dataBits = 8
  val depth = 16

  it should "ホストがpushを実行すると、FIFOにデータが書き込まれる [FIFO-000]" in {
    val outDir = dutName + "-fifo-push"
    val args = Array(
      "--top-name", dutName,
      "--target-dir", s"test_run_dir/$outDir",
      "-tgvo=on"
    )

    Driver.execute(args, () => new FIFO(dataBits, depth, true)) {
      c => new FIFOUnitTester(c) {
        val setData = Range(0, 16).map(_ => floor(random * 256).toInt)

        expect(c.io.rd.empty, true)
        for ((data, idx) <- setData.zipWithIndex) {
          push(data)
          expect(c.io.rd.empty, false)
          expect(c.io.rd.data, setData(0))
        }
        idle()
      }
    } should be (true)
  }

  it should "ホストがpopを実行すると、FIFOからデータが読み出される [FIFO-001]" in {
    val outDir = dutName + "-fifo-pop"
    val args = Array(
      "--top-name", dutName,
      "--target-dir", s"test_run_dir/$outDir",
      "-tgvo=on"
    )

    Driver.execute(args, () => new FIFO(dataBits, depth, true)) {
      c => new FIFOUnitTester(c) {
        val setData = Range(0, 16).map(_ => floor(random * 256).toInt)

        // data set
        for (data <- setData) {
          expect(c.io.wr.full, false)
          push(data)
        }
        expect(c.io.wr.full, true)
        idle()

        // pop
        for ((data, idx) <- setData.zipWithIndex) {
          pop(data)
        }
        idle()
      }
    } should be (true)
  }

  it should "pushとpopが同時に起きた場合、FIFOのデータ数は維持される [FIFO-002]" in {
    val outDir = dutName + "-fifo-push-and-pop"
    val args = Array(
      "--top-name", dutName,
      "--target-dir", s"test_run_dir/$outDir",
      "-tgvo=on"
    )

    Driver.execute(args, () => new FIFO(dataBits, depth, true)) {
      c => new FIFOUnitTester(c) {
        val txData = Range(0, 128).map(_ => floor(random * 256).toInt)
        expect(c.io.dbg.get.r_data_ctr, 0x0)
        push(txData(0))

        for ((data, exp) <- txData.tail.zip(txData)) {
          expect(c.io.dbg.get.r_data_ctr, 0x1)
          pushAndPop(data, exp)
        }
      }
    } should be (true)
  }

  it should "FIFOの段数を超えるデータが設定されると" +
    "ポインタはオーバーラップする [FIFO-003]" in {
    val outDir = dutName + "-fifo-overwrap"
    val args = Array(
      "--top-name", dutName,
      "--target-dir", s"test_run_dir/$outDir",
      "-tgvo=on"
    )

    Driver.execute(args, () => new FIFO(dataBits, depth, true)) {
      c => new FIFOUnitTester(c) {
        val txData = Range(0, 17).map(_ => floor(random * 256).toInt)

        for ((data, ptr) <- txData.zipWithIndex) {
          val expPtr = if (ptr == depth) 0 else ptr
          expect(c.io.dbg.get.r_wrptr, expPtr)
          push(data)
        }
      }
    } should be (true)
  }
}
