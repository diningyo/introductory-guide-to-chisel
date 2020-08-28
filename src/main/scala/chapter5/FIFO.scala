// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * FIFO リード側 I/O
  */
class FIFORdIO[T <: Data](gen: T) extends Bundle {
  val enable = Input(Bool())
  val empty = Output(Bool())
  val data = Output(gen)

  override def cloneType: this.type =
    new FIFORdIO(gen).asInstanceOf[this.type]
}

/**
  * FIFO ライト側 I/O
  */
class FIFOWrIO[T <: Data](gen: T) extends Bundle {
  val enable = Input(Bool())
  val full = Output(Bool())
  val data = Input(gen)

  override def cloneType: this.type =
    new FIFOWrIO(gen).asInstanceOf[this.type]
}

/**
  * FIFO I/O
  * @param gen 格納したいデータ型のインスタンス
  * @param depth FIFOの段数
  * @param debug trueでデバッグモード
  */
class FIFOIO[T <: Data](gen: T, depth: Int=16, debug: Boolean=false) extends Bundle {

  val depthBits = log2Ceil(depth)

  val wr = new FIFOWrIO(gen)
  val rd = new FIFORdIO(gen)

  val dbg = if (debug) { Some(Output(new Bundle {
    val r_wrptr = Output(UInt(depthBits.W))
    val r_rdptr = Output(UInt(depthBits.W))
    val r_data_ctr = Output(UInt((depthBits + 1).W))
  })) } else {
    None
  }

  override def cloneType: this.type =
    new FIFOIO(gen, depth, debug).asInstanceOf[this.type]
}

/**
  * 単純なFIFO
  * @param gen 格納したいデータ型のインスタンス
  * @param depth FIFOの段数
  * @param debug trueでデバッグモード
  */
class FIFO[T <: Data](gen: T, depth: Int = 16, debug: Boolean = false) extends Module {

  // parameter
  val depthBits = log2Ceil(depth)

  def ptrWrap(ptr: UInt): Bool = ptr === (depth - 1).U

  val io = IO(new FIFOIO(gen, depth, debug))

  val r_fifo = RegInit(VecInit(Seq.fill(depth)(0.U.asTypeOf(gen))))
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