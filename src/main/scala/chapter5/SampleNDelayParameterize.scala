// See README.md for license details.

package chapter5

import chisel3._

/**
  * 回路の中身を変更するサンプル
  * @param delayCyc 何サイクル遅延させるかを選択するパラメータ
  */
class SampleNDelayParameterize1(delayCyc: Int = 1) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  // レジスタをN個作ってSeqに格納
  // r_delaysには回路として動的にアクセスしないので
  // SeqでOK
  val r_delays = Seq.fill(delayCyc)(RegInit(0.U(8.W)))

  // Seqに格納したレジスタで信号を遅延させる
  for ((r, i) <- r_delays.zipWithIndex) {
    if (i == 0) {
      r := io.in
    } else {
      r := r_delays(i - 1)
    }
  }

  // 遅延サイクルが0以外の場合はSeqの最後のレジスタを出力に接続
  val w_out = if (delayCyc == 0) io.in else r_delays.last

  io.out := w_out
}

/**
  * 回路の中身を変更するサンプル（高階関数使用版）
  * @param delayCyc 何サイクル遅延させるかを選択するパラメータ
  */
class SampleNDelayParameterize2(delayCyc: Int = 1) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  // レジスタをN個作ってSeqに格納
  // r_delaysには回路として動的にアクセスしないので
  // SeqでOK
  val r_delays = Seq.fill(delayCyc)(RegInit(0.U(8.W)))

  // zipとforeachでレジスタをずらしながら接続
  (io.in +: r_delays).zip(r_delays).foreach {
    case (src, dst) => dst := src
  }

  // 遅延サイクルが0以外の場合はSeqの最後のレジスタを出力に接続
  val w_out = if (delayCyc == 0) io.in else r_delays.last

  io.out := w_out
}

/**
  * SampleDelayParameterizeをインスタンスするモジュール
  * @param delayCyc1 何サイクル遅延させるかを選択するパラメータ
  * @param delayCyc2 何サイクル遅延させるかを選択するパラメータ
  */
class SampleNDelayParameterizeTop
  (delayCyc1: Int, delayCyc2: Int) extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(8.W))
    val out1 = Output(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val out2 = Output(UInt(8.W))
  })

  // forループ版
  val m_sbwp1 = Module(new SampleNDelayParameterize1(delayCyc1))

  // こちらは高階関数版
  val m_sbwp2 = Module(new SampleNDelayParameterize2(delayCyc2))

  m_sbwp1.io.in := io.in1
  io.out1 := m_sbwp1.io.out

  m_sbwp2.io.in := io.in2
  io.out2 := m_sbwp2.io.out
}
