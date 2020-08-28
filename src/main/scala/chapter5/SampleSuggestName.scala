// See README.md for license details.

package chapter5

import chisel3._
import chisel3.stage.ChiselStage
import chisel3.util._

/**
  * suggestNameのサンプル
  */
class SampleSuggestName extends Module {
  val io = IO(new Bundle {
    val sel = Input(Bool())
    val in = Input(UInt(8.W))
    val out = Output(UInt(9.W))
  })

  io.out := io.in

  when (io.sel) {
    val w_out = io.in * 2.U
    w_out.suggestName("w_out")
    io.out := w_out
  }
}

/**
  * suggestNameのサンプルその２
  * @param portNames 付与したいサフィックス
  */
class SampleSuggestName2(portNames: Seq[String]) extends Module {
  val io = IO(new Bundle {
    val vec_in = Vec(2, Input(UInt(8.W)))
    val vec_out = Vec(2, Output(UInt(8.W)))
  })

  for ((in, out, name) <- (io.vec_in, io.vec_out, portNames).zipped) {
    val r_delay = RegNext(in)
    r_delay.suggestName(s"r_delay_$name")
    out := r_delay
  }
}

object ElaborateSampleSuggestName extends App {
  (new ChiselStage).emitVerilog(new SampleSuggestName, args)
}

object ElaborateSampleSuggestName2 extends App {
  (new ChiselStage).emitVerilog(new SampleSuggestName2(Seq("A", "B")), args)
}
