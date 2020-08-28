// See README.md for license details.

package chapter5

import chisel3._
import chisel3.iotesters._
import chisel3.util._

/**
  * Verilog-HDL/SystemVerilogのChiselラッパー
  */
class BlackBoxDelay extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val clk = Input(Clock())
    val rst_n = Input(Bool())
    val in = Input(UInt(8.W))
    val out = Output(UInt(9.W))
  })

  addResource("/BlackBoxDelay.sv")
}

class BlackBoxWithDefine extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(9.W))
  })

  val m_bb_delay = Module(new BlackBoxDelay)

  m_bb_delay.io.clk := clock
  m_bb_delay.io.rst_n := !reset.asBool()
  m_bb_delay.io.in := io.in
  io.out := m_bb_delay.io.out
}


object TestBlackBoxWithDefine extends App {
  iotesters.Driver.execute(Array(
    "-tbn=verilator",
    "-tmvf=+define+DELAY=1"
  ), () => new BlackBoxWithDefine) {
    c => new PeekPokeTester(c) {
      poke(c.io.in, 1)
      expect(c.io.out, 0)
      step(1)
      expect(c.io.out, 1)
    }
  }
}