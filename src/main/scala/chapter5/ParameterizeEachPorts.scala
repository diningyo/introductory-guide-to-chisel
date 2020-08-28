// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * MixedVecを使ったパラメタライズのサンプル用ポート
  * @param dataBits
  */
class ParameterizeEachPortsIO(dataBits: Int) extends Bundle {
  require(dataBits % 8 == 0, "dataBitsは8の倍数である必要があります")
  val strbBits = dataBits / 8
  val strb = UInt(strbBits.W)
  val data = UInt(dataBits.W)

  // パラメーターを行うのでcloneTypeの定義が必要
  override def cloneType: this.type =
    new ParameterizeEachPortsIO(dataBits).asInstanceOf[this.type]
}

/**
  * MixedVecを使ったポートのパラメタライズ
  */
class ParameterizeEachPorts(portParams: Seq[Int]) extends Module {

  val portTypes = portParams.map(new ParameterizeEachPortsIO(_))

  val io = IO(new Bundle {
    val in = Input(MixedVec(portTypes))
    val out = Output(MixedVec(portTypes))
  })

  io.out := io.in
}
