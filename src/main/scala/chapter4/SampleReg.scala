// See README.md for license details.

package chapter4

import chisel3._
import chisel3.util._

/**
 * Regを使ったサンプル
 * 処理自体は入力信号"in_***"を１サイクル遅延させるだけの処理
 */
class SampleReg extends Module {
  val io = IO(new Bundle {
    val in_bool = Input(Bool())
    val in_uint = Input(UInt(8.W))
    val in_sint = Input(SInt(8.W))
    val out_bool = Output(Bool())
    val out_uint = Output(UInt(8.W))
    val out_sint = Output(SInt(8.W))
  })

  val r_in_bool = Reg(Bool())    // Bool型のレジスタ
  val r_in_uint = Reg(UInt(8.W)) // UInt型のレジスタ
  val r_in_sint = Reg(SInt(8.W)) // SInt型のレジスタ

  r_in_bool := io.in_bool
  r_in_uint := io.in_uint
  r_in_sint := io.in_sint

  io.out_bool := r_in_bool
  io.out_uint := r_in_uint
  io.out_sint := r_in_sint
}

/**
 * RegInitを使ったサンプル
 * 処理自体は入力信号"in_***"を１サイクル遅延させるだけの処理
 */
class SampleRegInit extends Module {
  val io = IO(new Bundle {
    val in_bool = Input(Bool())
    val in_uint = Input(UInt(8.W))
    val in_sint = Input(SInt(8.W))
    val out_bool = Output(Bool())
    val out_uint = Output(UInt(8.W))
    val out_sint = Output(SInt(8.W))
  })

  val r_in_bool = RegInit(false.B)   // Bool型のレジスタ
  val r_in_uint = RegInit(100.U)     // "1000"で初期化
  val r_in_sint = RegInit(-1.S(8.W)) // "-1"で初期化

  r_in_bool := io.in_bool
  r_in_uint := io.in_uint
  r_in_sint := io.in_sint

  io.out_bool := r_in_bool
  io.out_uint := r_in_uint
  io.out_sint := r_in_sint
}

/**
 * RegNextを使ったサンプル
 * 処理自体は入力信号"in_***"を１サイクル遅延させるだけの処理
 */
class SampleRegNext extends Module {
  val io = IO(new Bundle {
    val in_bool = Input(Bool())
    val in_uint = Input(UInt(8.W))
    val in_sint = Input(SInt(8.W))
    val out_bool = Output(Bool())
    val out_uint = Output(UInt(8.W))
    val out_sint = Output(SInt(8.W))
  })

  // Chiselの他のハードウェア要素を直接引数に取れる
  io.out_bool := RegNext(io.in_bool)
  io.out_uint := RegNext(io.in_uint, 0.U)
  io.out_sint := RegNext(io.in_sint, -1.S)
}
