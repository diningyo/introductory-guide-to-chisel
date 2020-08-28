// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * SampleInputOutput用のBundle継承したクラス1
  */
class SampleInputOutputIO1 extends Bundle {
  val a1 = Input(Bool())
  val b1 = Output(Bool())
}

/**
  * IOとInput/Outputのサンプル
  */
class SampleInputOutput extends Module {
  val io = IO(new Bundle {
    val bool_port = Output(Bool())
    val vec_port = Output(Vec(2, UInt(8.W)))
    val bundle_port1 = new SampleInputOutputIO1
    val bundle_port2 = Output(new SampleInputOutputIO1)
  })

  io := DontCare
}
