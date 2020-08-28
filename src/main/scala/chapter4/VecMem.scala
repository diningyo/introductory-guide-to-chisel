// See README.md for license details.

package chapter4

import chisel3._
import chisel3.iotesters._
import chisel3.util._

import chisel3.util.experimental.loadMemoryFromFile

/**
  * Vecを使ったMemのサンプル
  */
class VecMem extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(4.W))
    val wren = Input(Bool())
    val wrstrb = Input(UInt(2.W))
    val wrdata = Input(Vec(2, UInt(8.W)))
    val rddata = Output(UInt(16.W))
  })

  val m_mem = Mem(16, Vec(2, UInt(8.W)))

  loadMemoryFromFile(m_mem, "src/main/resources/vec_mem_data")

  // ライト
  when (io.wren) {
    // 通常のライトもOK：この場合はmask=1'b1で固定
    //m_mem.write(io.addr, io.wrdata)
    // マスク付きのライトを使用
    // マスクデータはSeq[Bool]なのでUIntのasBoolsを使って変換
    m_mem.write(io.addr, io.wrdata, io.wrstrb.asBools())
  }

  // リード：データがVec(2, UInt(8.W)で出力される
  io.rddata := Cat(m_mem.read(io.addr))
}

object TestVecMem extends App {

  val dut = "VecMem"

  iotesters.Driver.execute(Array(
    s"-tn=$dut", s"-td=test_run_dir/$dut", "-tgvo=on"//, "-tbn=verilator"
  ), () => new VecMem) {
    c => new PeekPokeTester(c) {

      for (addr <- 0 until 0x10) {
        poke(c.io.addr, addr)
        step(1)
      }
      fail
    }
  }
}
