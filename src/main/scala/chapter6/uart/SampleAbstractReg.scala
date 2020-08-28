// See README.md for license details.

package chapter6.uart

import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util._

object RegAddress {
  val a = 0
  val b = 1
  val c = 2

  val addrSeq = Seq(a, b, c)
}

class RegA extends UartReg {
  val bit7_1 = UInt(7.W)
  val bit0 = Bool()

  def write(data: UInt): Unit = {
    bit7_1 := data(7, 1)
    bit0 := data(0)
  }

  def read(): UInt = Cat(bit7_1, bit0)
}

class RegB extends RegA
class RegC extends RegA

// RegAと同様にRegB / RegCを実装

class SampleAbstractReg extends Module {

  import RegAddress._

  val io = IO(new Bundle {
    val addr = Input(UInt(2.W))
    val wren = Input(Bool())
    val wrdata = Input(UInt(8.W))
    val rden = Input(Bool())
    val rddv = Output(Bool())
    val rddata = Output(UInt(8.W))
  })

  val r_a = Reg(new RegA)
  val r_b = Reg(new RegB)
  val r_c = Reg(new RegC)

  val regSeq = Seq(r_a, r_b, r_c)

  // addrSeqとregSeqをzipでまとめてループ処理
  addrSeq.zip(regSeq).foreach {
    case (addr, reg) =>
      // ライトの条件を満たしたら、各レジスタのwriteを呼び出す
      when (io.wren && (io.addr === addr.U)) {
        reg.write(io.wrdata)
      }
  }

  // ライトと同様にzipでループ処理して
  // MuxCaseに渡すSeqを生成する
  val readDataSeq: Seq[(Bool, (Bool, UInt))] = addrSeq.zip(regSeq).map {
    case (addr, reg) =>
      val w_rd_sel = (io.addr === addr.U)
      (w_rd_sel, (w_rd_sel -> reg.read()))
  }

  io.rddv := io.rden && readDataSeq.map(_._1).reduce(_ || _)
  io.rddata := MuxCase(0.U, readDataSeq.map(_._2))
}

object Elaborate extends App {
  (new ChiselStage).emitVerilog(new SampleAbstractReg, Array(""))
}
