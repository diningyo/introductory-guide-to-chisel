// See README.md for license details.

package chapter4

import chisel3._
import chisel3.iotesters._
import chisel3.util._

import chisel3.util.experimental.loadMemoryFromFile
import firrtl.annotations.MemoryLoadFileType

/**
  * Element型を使ったシンプルなメモリのサンプル
  */
class SimpleMem extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(4.W))
    val wren = Input(Bool())
    val wrdata = Input(UInt(16.W))
    val rddata = Output(UInt(16.W))
  })

  val m_mem = Mem(16, UInt(16.W))

  loadMemoryFromFile(m_mem, "src/main/resources/simple_mem_data")

  // ライト
  when (io.wren) {
    // 通常のライトなら以下のどちらでもOK
    m_mem(io.addr) := io.wrdata
    m_mem.write(io.addr, io.wrdata)
  }

  // リード：どちらの記述を使ってもOK
  io.rddata := m_mem(io.addr)
  io.rddata := m_mem.read(io.addr)
}

object TestSimpleMem extends App {

  val dut = "SimpleMem"

  iotesters.Driver.execute(Array(
    s"-tn=$dut", s"-td=test_run_dir/$dut", "-tgvo=on", "-tbn=verilator"
  ), () => new SimpleMem) {
    c => new PeekPokeTester(c) {

      for (addr <- 0 until 0x10) {
        poke(c.io.addr, addr)
        step(1)
      }
      fail
    }
  }
}
