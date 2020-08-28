// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * Bundleのサンプル
  */
class SampleBundleIO1 extends Bundle {
  val addr = Input(UInt(8.W))
  val wren = Input(Bool())
  val rden = Input(Bool())
  val wrdata = Input(UInt(8.W))
  val rddata = Output(UInt(8.W))
}

/**
  * Bundleのサンプルを使ったモジュール
  */
class SampleBundle1 extends Module {
  val io = IO(new SampleBundleIO1)

  io.rddata := 100.U
}

/**
  * クラスパラメーターつきのBundleのサンプル
  */
class SampleBundleIO2(dataBits: Int) extends Bundle {
  val addr = Input(UInt(dataBits.W))
  val wren = Input(Bool())
  val rden = Input(Bool())
  val wrdata = Input(UInt(dataBits.W))
  val rddata = Output(UInt(dataBits.W))
}

/**
  * SampleBundleIO2を使ったモジュール
  */
class SampleBundle2 extends Module {
  val io = IO(new SampleBundleIO2(8))

  io.rddata := 100.U
}