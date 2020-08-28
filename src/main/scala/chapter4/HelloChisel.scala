// See README.md for license details.

package chapter4

import math.pow
import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util._

/**
  * HelloChisel
  *  ChiselでLチカを行うサンプルモジュール
  *
  * @param targetFreq 周波数(Hz)
  * @param blinkInterval 点滅周期(msec)
  */
class HelloChisel(targetFreq: Int, blinkInterval: Int) extends Module {
  val io = IO(new Bundle {
    val out = Output(Bool())
  })

  // 定数の計算
  val msecCounts = targetFreq / 1000 // 1msecに必要なカウント

  // カウント用のレジスタ
  val r_count_msec = RegInit(0.U(log2Ceil(msecCounts).W))
  val w_msec_update = r_count_msec === (msecCounts - 1).U
  val r_count_interval = RegInit(0.U(log2Ceil(blinkInterval).W))
  val w_interval_update = r_count_interval === (blinkInterval - 1).U
  val r_out = RegInit(false.B)

  // クロック毎に0x1を加算
  r_count_msec := r_count_msec + 1.U

  // w_msec_updateが0x1の時
  when (w_msec_update) {
    r_count_msec := 0.U
    r_count_interval := r_count_interval + 1.U

    // w_interval_updateが0x1の時
    when (w_interval_update) {
      r_count_interval := 0.U
      r_out := !r_out
    }
  }

  // 出力にr_outを接続
  io.out := r_out
}

/**
  * HelloChiselのRTLを生成するメイン関数
  */
object ElaborateHelloChisel extends App {

  val freq = 100 * pow(10, 6).toInt // 50MHz
  val interval = 500                // 500msec

  // HelloChiselモジュールのクラスパラメーターを渡して実行
  (new ChiselStage).emitVerilog(new HelloChisel(freq, interval), args)
}
