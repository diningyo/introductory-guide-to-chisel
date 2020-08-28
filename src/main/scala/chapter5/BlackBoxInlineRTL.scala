// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._
import chisel3.iotesters._

/**
  * BlackBoxInlineを使ったサンプル
  * @param bits ビット幅
  */
class BlackBoxAdd(bits: Int) extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val in1 = Input(UInt(bits.W))
    val in2 = Input(UInt(bits.W))
    val out = Output(UInt((bits + 1).W))
  })

  // 直接RTLを埋め込む
  // """はScalaの複数行コメント
  // s"""のようにすることで、文字列内で$<変数名>で変数を
  // 参照可能
  setInline("BlackBoxAdd.sv",
    s"""
       |module BlackBoxAdd
       |    (
       |      input logic [${bits - 1}:0] in1
       |     ,input logic [${bits - 1}:0] in2
       |     ,output logic [$bits:0] out
       |   );
       |
       |   assign out = in1 + in2;
       |endmodule
       |""".stripMargin
  )
}

/**
  * BlackBoxInlineの確認用トップモジュール
  */
class BlackBoxInlineRTLTop extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val out = Output(UInt(9.W))
  })

  val m_bb_add = Module(new BlackBoxAdd(8))

  m_bb_add.io.in1 := io.in1
  m_bb_add.io.in2 := io.in2
  io.out := m_bb_add.io.out
}

/**
  * BlackBoxInlineRTLの動作確認テスト
  */
object TestBlackBoxInlineRTL extends App {
  val r = iotesters.Driver.execute(Array(
    "--top-name=BlackBoxInlineRTLTop",
    "--target-dir=test_run_dir/BlackBoxInlineRTLTop",
    "--backend-name=verilator"), () => new BlackBoxInlineRTLTop) {
    c => new PeekPokeTester(c) {
      poke(c.io.in1, 1)
      poke(c.io.in2, 1)
      expect(c.io.out, 2)
    }
  }

  assert(r)
}
