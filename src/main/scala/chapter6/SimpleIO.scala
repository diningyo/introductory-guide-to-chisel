// See README.md for license details.

package chapter6

import chisel3._
import chisel3.util._

/**
  * SimpleIOクラスのパラメータ用クラス
  * @param addrBits アドレスのビット幅
  * @param dataBits データのビット幅
  */
case class SimpleIOParams
  (
   addrBits: Int = 4,
   dataBits: Int = 8
  )

/**
  * SimpleIO
  * @param p IOパラメータ
  */
class SimpleIO(p: SimpleIOParams) extends Bundle {
  val addr = Output(UInt(p.addrBits.W))
  val wren = Output(Bool())
  val rden = Output(Bool())
  val wrdata = Output(UInt(p.dataBits.W))
  val rddv = Input(Bool())
  val rddata = Input(UInt(p.dataBits.W))

  override def cloneType: this.type =
    new SimpleIO(p).asInstanceOf[this.type]
}
