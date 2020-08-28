// See README.md for license details.

package chapter5

import chisel3._

/**
  * 既存のRTLのChiselモジュールラッパークラス
  */
class SimpleBlackBox extends BlackBox {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val reset_n = Input(Bool())
    val in = Input(Bool())
    val out = Output(Bool())
  })
}

/**
  * BlackBoxをインスタンスするトップ階層
  */
class SimpleBlackBoxTop extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  val m_sbb = Module(new SimpleBlackBox)

  m_sbb.io.clock := clock
  // resetはReset型のため、Boolへの変換が必要
  m_sbb.io.reset_n := !reset.asBool
  m_sbb.io.in := io.in
  io.out := m_sbb.io.out
}

/**
  * SimpleBlackBoxTopのRTL生成
  */
object ElaborateSimpleBlackBoxTop extends App {
  Driver.execute(Array(
    "-td=rtl"
  ), () => new SimpleBlackBoxTop)
}