// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * 回路の中身を変更するサンプル
  * @param hasFF 入力を1cycle遅延させるかを選択するフラグ
  */
class SampleDelayParameterize(hasFF: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  val out = if (hasFF) RegNext(io.in, 0.U(8.W)) else WireDefault(io.in)

  io.out := out
}

/**
  * SampleDelayParameterizeをインスタンスするモジュール
  * @param hasFF1
  * @param hasFF2
  */
class SampleDelayParameterizeTop
  (hasFF1: Boolean, hasFF2: Boolean) extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(8.W))
    val out1 = Output(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val out2 = Output(UInt(8.W))
  })

  val m_sbwp1 = Module(new SampleDelayParameterize(hasFF1))
  val m_sbwp2 = Module(new SampleDelayParameterize(hasFF2))

  m_sbwp1.io.in := io.in1
  io.out1 := m_sbwp1.io.out

  m_sbwp2.io.in := io.in2
  io.out2 := m_sbwp2.io.out
}
