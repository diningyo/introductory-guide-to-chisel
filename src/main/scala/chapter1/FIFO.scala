// See README.md for license details.

package chapter1

import chisel3._
import chisel3.util._

/**
  * FIFO リード側 I/O
  */
class FIFORdIO(bits: Int) extends Bundle {
  val enable = Input(Bool())
  val empty = Output(Bool())
  val data = Output(UInt(bits.W))
}

/**
  * FIFO ライト側 I/O
  */
class FIFOWrIO(bits: Int) extends Bundle {
  val enable = Input(Bool())
  val full = Output(Bool())
  val data = Input(UInt(bits.W))
}

/**
  * FIFO I/O
  * @param bits データのビット幅
  * @param depth FIFOの段数
  * @param debug trueでデバッグモード
  */
class FIFOIO(bits: Int, depth: Int=16, debug: Boolean=false) extends Bundle {

  val depthBits = log2Ceil(depth)

  val wr = new FIFOWrIO(bits)
  val rd = new FIFORdIO(bits)

  val dbg = if (debug) { Some(Output(new Bundle {
    val r_wrptr = Output(UInt(depthBits.W))
    val r_rdptr = Output(UInt(depthBits.W))
    val r_data_ctr = Output(UInt((depthBits + 1).W))
  })) } else {
    None
  }

  override def cloneType: this.type =
    new FIFOIO(bits, depth, debug).asInstanceOf[this.type]
}

/**
  * 単純なFIFO
  * @param dataBits データのビット幅
  * @param depth FIFOの段数
  * @param debug trueでデバッグモード
  */
class FIFO(dataBits: Int = 8, depth: Int = 16, debug: Boolean = false) extends Module {

  // parameter
  val depthBits = log2Ceil(depth)

  def ptrWrap(ptr: UInt): Bool = ptr === (depth - 1).U

  val io = IO(new FIFOIO(dataBits, depth, debug))

  val r_fifo = RegInit(VecInit(Seq.fill(depth)(0.U(dataBits.W))))
  val r_rdptr = RegInit(0.U(depthBits.W))
  val r_wrptr = RegInit(0.U(depthBits.W))
  val r_data_ctr = RegInit(0.U((depthBits + 1).W))

  // リードポインタ
  when(io.rd.enable) {
    r_rdptr := Mux(ptrWrap(r_rdptr), 0.U, r_rdptr + 1.U)
  }

  // ライトポインタ
  when(io.wr.enable) {
    r_fifo(r_wrptr) := io.wr.data
    r_wrptr := Mux(ptrWrap(r_wrptr), 0.U, r_wrptr + 1.U)
  }

  // データカウント
  when (io.wr.enable && io.rd.enable) {
    r_data_ctr := r_data_ctr
  } .otherwise {
    when (io.wr.enable) {
      r_data_ctr := r_data_ctr + 1.U
    }
    when (io.rd.enable) {
      r_data_ctr := r_data_ctr - 1.U
    }
  }

  // IOとの接続
  io.wr.full := r_data_ctr === depth.U
  io.rd.empty := r_data_ctr === 0.U
  io.rd.data := r_fifo(r_rdptr)

  // テスト用のデバッグ端子の接続
  if (debug) {
    io.dbg.get.r_wrptr := r_wrptr
    io.dbg.get.r_rdptr := r_rdptr
    io.dbg.get.r_data_ctr := r_data_ctr
  }
}