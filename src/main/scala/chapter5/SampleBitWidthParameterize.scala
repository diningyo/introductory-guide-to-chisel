// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * ビット幅のパラメタライズのサンプル
  * @param bits データのビット幅
  */
class SampleBitWidthParameterize(bits: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(bits.W))
    val out = Output(UInt(bits.W))
  })

  io.out := io.in
}

/**
  * SapmleBitWidthParameterizeをインスタンスするトップ階層
  * @param bits1 m_sbwp1のデータのビット幅
  * @param bits2 m_sbwp1のデータのビット幅
  */
class SampleTop(bits1: Int = 7, bits2: Int = 4) extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(bits1.W))
    val out1 = Output(UInt(bits1.W))
    val in2 = Input(UInt(bits2.W))
    val out2 = Output(UInt(bits2.W))
  })

  val m_sbwp1 = Module(new SampleBitWidthParameterize(bits1))
  val m_sbwp2 = Module(new SampleBitWidthParameterize(bits2))

  m_sbwp1.io.in := io.in1
  io.out1 := m_sbwp1.io.out

  m_sbwp2.io.in := io.in2
  io.out2 := m_sbwp2.io.out
}