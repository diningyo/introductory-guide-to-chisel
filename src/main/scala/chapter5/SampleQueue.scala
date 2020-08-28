// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * Queue用のIO
  */
class MyQueueIO extends Bundle {
  val a = UInt(8.W)
  val b = Bool()
}

/**
  * ChiselのQueueを使ったサンプル
  */
class SampleQueue extends Module {
  val io = IO(new Bundle {
    val q_in1 = Flipped(DecoupledIO(UInt(8.W)))
    val q_in2 = Flipped(DecoupledIO(new MyQueueIO))
    val q_out1 = DecoupledIO(UInt(8.W))
    val q_out2 = DecoupledIO(new MyQueueIO)
  })

  // クラスをnewでインスタンスする方法
  val m_q1 = Module(new Queue(UInt(8.W), 1))
  m_q1.io.enq <> io.q_in1
  io.q_out1 <> m_q1.io.deq

  // コンパニオンオブジェクトを使う方法
  val m_q2 = Queue(io.q_in2, 1, true, true)
  io.q_out2 <> m_q2
}