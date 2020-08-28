// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * desiredNameのサンプル
  * @param n 加算する数
  */
class AddN(n : Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  /**
    * desiredName
    * @return 元の名前に"equalTo"n""を追加
    */
  //override def desiredName: String = super.desiredName + f"equalTo$n%02d"

  io.out := io.in + n.U
}

/**
  * desiredNameを使ったモジュールをインスタンスする階層
  */
class SampleDesiredName extends Module {

  val nSeq = Seq(1, 7)

  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(MixedVec(nSeq.map(i => UInt(i.W))))
  })

  io.out.zip(nSeq).foreach {
    case (out, n) =>
      val m_mod = Module(new AddN(n))
      m_mod.io.in := io.in
      out := m_mod.io.out
  }
}
