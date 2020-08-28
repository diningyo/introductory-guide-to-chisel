// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._
import chisel3.iotesters._

/**
  * Verilog-HDL/SystemVerilogのChiselラッパー
  */
class BlackBoxSub extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val in1 = Input(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val out = Output(UInt(9.W))
  })

  addResource("/BlackBoxSub.sv")
}

class BlackBoxWithRTLTop extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(8.W))
    val in2 = Input(UInt(8.W))
    val out = Output(UInt(9.W))
  })

  val m_bb_add = Module(new BlackBoxSub)

  m_bb_add.io.in1 := io.in1
  m_bb_add.io.in2 := io.in2
  io.out := m_bb_add.io.out
}


object TestBlackBoxWithRTLTop extends App {
  iotesters.Driver.execute(args, () => new BlackBoxWithRTLTop) {
    c => new PeekPokeTester(c) {
      poke(c.io.in1, 1)
      poke(c.io.in2, 1)
      expect(c.io.out, 2)
    }
  }
}