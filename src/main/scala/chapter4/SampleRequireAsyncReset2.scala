// See README.md for license details.

package chapter4

import chisel3._

/**
  * 通常のモジュール
  */
class SampleRequireAsyncReset2 extends Module {
  val io = IO(new ThroughIO)

  val m_delay =  Module(new Delay)

  io <> m_delay.io
}

/**
  * モジュールのインスタンス時にRequireAsyncResetを指定
  */
object ElaborateSampleRequireAsyncReset2 extends App {

  val genArgs = Array(
    s"-td=rtl"
  )

  val modName = "SampleDefaultResetType"
  chisel3.Driver.execute(genArgs :+ s"-tn=${modName}_sync", () => new chapter4.SampleRequireAsyncReset2)
  chisel3.Driver.execute(genArgs :+ s"-tn=${modName}_async",
    () => new chapter4.SampleRequireAsyncReset2 with RequireAsyncReset)
}