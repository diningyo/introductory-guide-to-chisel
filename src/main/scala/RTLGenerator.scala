// See README.md for license details.

import math.pow

import chapter5.{RX, TX}
import chapter6.SimpleIOParams
import chisel3._

/**
  * Stringでもらったクラスのパスからインスタンスを生成する
  */
object GetInstance {
  /**
    * apply
    * @param modPath インスタンスを生成したいChiselモジュールのクラスパス
    * @return modPathで指定したChiselモジュールのインスタンス
    * @note このapplyで作れるのはクラスパラメータを持たないクラスのみ
    */
  def apply(modPath: String): Module = {
    Class.forName(modPath)
      .getConstructor()
      .newInstance() match {
      case m: Module => m
      case _ => throw new ClassNotFoundException("The class must be inherited chisel3.Module")
    }
  }
}

/**
  * RTLを生成するジェネレータ
  * runMainのプログラム引数でRTLを生成したいChiselモジュールの
  * パスを渡すと、その情報を元にクラスのインスタンスを
  * chisel3.Driver.executeに渡してRTLを生成する。
  */
object RTLGenerator extends App {

  val genArgs = Array(
    s"-td=rtl"
  )

  args(0) match {
    // chapter1
    case "chapter1.FIFO" =>
      (new stage.ChiselStage).emitVerilog(new chapter1.FIFO(16), genArgs)
    // chapter4
    case "chapter4.HelloChisel" =>
      val freq = 100 * pow(10, 6).toInt // 50MHz
      val interval = 500                // 500msec
      (new stage.ChiselStage).emitVerilog(new chapter4.HelloChisel(freq, interval), genArgs)
    case "chapter4.SampleRequireAsyncReset2" =>
      val modName = "SampleRequireAsyncReset2"
      (new stage.ChiselStage).emitVerilog(
        new chapter4.SampleRequireAsyncReset2, genArgs :+ s"-tn=${modName}_sync")
      (new stage.ChiselStage).emitVerilog(
        new chapter4.SampleRequireAsyncReset2 with RequireAsyncReset, genArgs :+ s"-tn=${modName}_async")
    // chapter5
    case "chapter5.SampleTop" =>
      (new stage.ChiselStage).emitVerilog(new chapter5.SampleTop(4, 8), genArgs)
    case "chapter5.SampleDontTouch" =>
      val modName = "SampleDontTouch"
      (new stage.ChiselStage).emitVerilog(
        new chapter5.SampleDontTouch(true), genArgs :+ s"-tn=${modName}_true")
      (new stage.ChiselStage).emitVerilog(
        new chapter5.SampleDontTouch(false), genArgs :+ s"-tn=${modName}_false")
    case "chapter5.SampleDelayParameterizeTop" =>
      (new stage.ChiselStage).emitVerilog(new chapter5.SampleDelayParameterizeTop(true, false), genArgs)
    case "chapter5.SampleNDelayParameterizeTop" =>
      (new stage.ChiselStage).emitVerilog(new chapter5.SampleNDelayParameterizeTop(5, 10), genArgs)
    case "chapter5.ParameterizeEachPorts" =>
      val portParams = Range(8, 25, 8) // 8, 16, 24
      (new stage.ChiselStage).emitVerilog(new chapter5.ParameterizeEachPorts(portParams), genArgs)
    case "chapter5.IOPortParameterize" =>
      (new stage.ChiselStage).emitVerilog(new chapter5.IOPortParameterize(true, false), genArgs)
    case "chapter5.IOParameterize" =>
      (new stage.ChiselStage).emitVerilog(new chapter5.IOParameterize(TX), genArgs)
      (new stage.ChiselStage).emitVerilog(new chapter5.IOParameterize(RX), genArgs)
    case "chapter5.SampleSuggestName2" =>
      (new stage.ChiselStage).emitVerilog(new chapter5.SampleSuggestName2(Seq("A", "B")), genArgs)
    case "chapter6.EchoBackTop" =>
      val p = SimpleIOParams()
      (new stage.ChiselStage).emitVerilog(new chapter6.EchoBackTop(p), genArgs)
    case _ => (new stage.ChiselStage).emitVerilog(GetInstance(args(0)), genArgs)
  }
}
