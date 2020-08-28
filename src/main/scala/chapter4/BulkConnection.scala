// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
  * バルクコネクション確認用のクラス
  */
class RegIO extends Bundle {
  val addr = Input(UInt(8.W))
  val wren = Input(Bool())
  val rden = Input(Bool())
  val wrdata = Input(UInt(8.W))
  val rddata = Output(UInt(8.W))
}

/**
  * サンプル用のレジスタブロック
  */
class RegMod extends Module {
  // IO部分はRegIOをインスタンスする
  val io = IO(new RegIO)

  val r_reg = RegInit(0.U(8.W))

  when (io.wren) {
    r_reg := io.wrdata
  }

  io.rddata := Mux(io.rden, r_reg, 0.U)
}

/**
  * バルクコネクション確認用のクラス
  */
// モジュールを接続する階層
class SampleBulkConnection extends Module {
  val io = IO(new RegIO)

  val reg = Module(new RegMod)

  // "<>" だけで接続完了
  reg.io <> io
}

/**
  * Flippedを使ったバルクコネクション確認用のクラス
  */
class SampleFlippedBulkConnection extends Module {
  val io = IO(new Bundle {
    val in = new RegIO
    // Flippedに渡すとポートの方向が反転する
    val out = Flipped(new RegIO)
  })

  io.out <> io.in
}

/**
  * 一部の信号の再接続を行うサンプル
  */
class PartialReConnection extends Module {
  val io = IO(new Bundle {
    val in = Flipped(new RegIO)
    val out = new RegIO
  })

  // 一回<>全ての端子を接続する
  io.out <> io.in

  // リードデータはレジスタ出力にする
  io.out.rddata := RegNext(io.in.rddata)
}