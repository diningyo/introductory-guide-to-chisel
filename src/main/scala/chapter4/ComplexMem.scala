// See README.md for license details.

package chapter4

import chisel3._
import chisel3.iotesters._
import chisel3.util._

import chisel3.util.experimental.loadMemoryFromFile

/**
 * ComplexMem用のデータ型
 */
class MemData extends Bundle {
  val a = UInt(16.W)
  val b = SInt(16.W)
  val c = Bool()

}

/**
 * Bundleを使ったメモリ
 */
class ComplexMem extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(4.W))
    val wren = Input(Bool())
    val wrdata = Input(new MemData)
    val rddata = Output(new MemData)
  })

  val m_mem = Mem(16, new MemData)

  loadMemoryFromFile(m_mem, "src/main/resources/mem_data")

  when (io.wren) {
    m_mem.write(io.addr, io.wrdata)
  }

  io.rddata := m_mem(io.addr)
}

object TestComplexMem extends App {

  val dut = "ComplexMem"

  iotesters.Driver.execute(Array(
    s"-tn=$dut", s"-td=test_run_dir/$dut", "-tgvo=on", "-tbn=verilator"
    //s"-tn=$dut", s"-td=test_run_dir/$dut", "-tgvo=on"//, "-tbn=firrtl"
  ), () => new ComplexMem) {
    c => new PeekPokeTester(c) {

      for (addr <- 0 until 0x10) {
        poke(c.io.addr, addr)
        step(1)
      }

      poke(c.io.addr, 2)
      poke(c.io.wren, true)
      poke(c.io.wrdata.a, 0x80)
      poke(c.io.wrdata.b, -1)
      poke(c.io.wrdata.c, false)
      step(1)
      poke(c.io.wren, false)
      step(2)
    }
  }
}
