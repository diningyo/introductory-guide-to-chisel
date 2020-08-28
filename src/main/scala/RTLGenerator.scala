// See README.md for license details.

import math.pow

import chapter5.{RX, TX}
import chapter6.SimpleIOParams
import chisel3._
import chisel3.iotesters.PeekPokeTester

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
      chisel3.Driver.execute(genArgs, () => new chapter1.FIFO(16))
    // chapter4
    case "chapter4.HelloChisel" =>
      val freq = 100 * pow(10, 6).toInt // 50MHz
      val interval = 500                // 500msec
      Driver.execute(args, () => new chapter4.HelloChisel(freq, interval))
    case "chapter4.SampleRequireAsyncReset2" =>
      val modName = "SampleRequireAsyncReset2"
      chisel3.Driver.execute(genArgs :+ s"-tn=${modName}_sync",
        () => new chapter4.SampleRequireAsyncReset2)
      chisel3.Driver.execute(genArgs :+ s"-tn=${modName}_async",
        () => new chapter4.SampleRequireAsyncReset2 with RequireAsyncReset)
    // chapter5
    case "chapter5.SampleTop" =>
      chisel3.Driver.execute(genArgs, () => new chapter5.SampleTop(4, 8))
    case "chapter5.SampleDontTouch" =>
      val modName = "SampleDontTouch"
      chisel3.Driver.execute(genArgs :+ s"-tn=${modName}_true",
        () => new chapter5.SampleDontTouch(true))
      chisel3.Driver.execute(genArgs :+ s"-tn=${modName}_false",
        () => new chapter5.SampleDontTouch(false))
    case "chapter5.SampleDelayParameterizeTop" =>
      chisel3.Driver.execute(genArgs, () => new chapter5.SampleDelayParameterizeTop(true, false))
    case "chapter5.SampleNDelayParameterizeTop" =>
      chisel3.Driver.execute(genArgs, () => new chapter5.SampleNDelayParameterizeTop(5, 10))
    case "chapter5.ParameterizeEachPorts" =>
      val portParams = Range(8, 25, 8) // 8, 16, 24
      chisel3.Driver.execute(genArgs, () => new chapter5.ParameterizeEachPorts(portParams))
    case "chapter5.IOPortParameterize" =>
      chisel3.Driver.execute(genArgs, () => new chapter5.IOPortParameterize(true, false))
    case "chapter5.IOParameterize" =>
      chisel3.Driver.execute(genArgs, () => new chapter5.IOParameterize(TX))
      chisel3.Driver.execute(genArgs, () => new chapter5.IOParameterize(RX))
    case "chapter5.SampleSuggestName2" =>
      chisel3.Driver.execute(genArgs, () => new chapter5.SampleSuggestName2(Seq("A", "B")))
    case "chapter6.EchoBackTop" =>
      val p = SimpleIOParams()
      chisel3.Driver.execute(genArgs, () => new chapter6.EchoBackTop(p))
    case _ => (new stage.ChiselStage).emitVerilog(GetInstance(args(0)), genArgs)
  }

}
