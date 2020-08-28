// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._
import chisel3.experimental.{IntParam, DoubleParam, StringParam, RawParam}

/**
  * Paramを使った
  */
class BlackBoxWithParameter() extends BlackBox(
  Map(
    "p_INT_PARAMS" -> IntParam(4),
    "p_DOUBLE_PARAMS" -> DoubleParam(4.0),
    "p_STRING_PARAMS" -> StringParam("String"),
    "p_DELAY_CYCLES" -> RawParam("Raw"),
  )) {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })
}

class BlackBoxWithParameterTop extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  val m_bb = Module(new BlackBoxWithParameter)

  m_bb.io.in := io.in
  io.out := m_bb.io.out
}
