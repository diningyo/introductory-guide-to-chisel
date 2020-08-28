// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

sealed trait Direction
case object TX extends Direction
case object RX extends Direction

class TXIF() extends Bundle {
  val tx = DecoupledIO(UInt(8.W))
}

class RXIF() extends Bundle {
  val rx = DecoupledIO(UInt(4.W))
  val info = Output(Bool())
}

class IOParameterize(dir: Direction) extends Module {
  val io = if (dir == TX) IO(new TXIF) else IO(new RXIF)

  // DontCareで全ての出力端子を0にクリップできる
  io := DontCare

  io match {
    case i: TXIF => i.tx.bits := 0.U
    case i: RXIF =>
      i.rx.bits := 1.U
      i.info := true.B
  }
}

object El extends App {
  Driver.execute(args :+ "-tn=TX", () => new IOParameterize(TX))
  Driver.execute(args :+ "-tn=RX", () => new IOParameterize(RX))
}
