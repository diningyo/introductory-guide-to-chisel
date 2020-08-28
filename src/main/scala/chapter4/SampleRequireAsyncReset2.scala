// See README.md for license details.

package chapter4

import chisel3._
import chisel3.stage.ChiselStage

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
  (new ChiselStage).emitVerilog(
    new chapter4.SampleRequireAsyncReset2, genArgs :+ s"-tn=${modName}_sync")
  (new ChiselStage).emitVerilog(
    new chapter4.SampleRequireAsyncReset2 with RequireAsyncReset, genArgs :+ s"-tn=${modName}_async")
}