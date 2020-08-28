// See README.md for license details.

package chapter6.uart

import chisel3._
import chisel3.util._

import chapter5.{FIFO, FIFORdIO, FIFOWrIO}
import chapter6.{SimpleIO, SimpleIOParams}

/**
  * CSRのレジスタアドレス
  */
object RegInfo {
  val rxFifo = 0x0 // RX FIFO
  val txFifo = 0x4 // TX FIFO
  val stat = 0x8   // Status
  val ctrl = 0xc   // Control
}

/**
  * CSRのレジスタ用クラス
  */
abstract class UartReg extends Bundle {
  def write(data: UInt)
  def read(): UInt
}

/**
  * ステータス・レジスタ
  */
class StatReg extends UartReg with IgnoreSeqInBundle {
  val txfifo_full = UInt(1.W)
  val txfifo_empty = UInt(1.W)
  val rxfifo_full = UInt(1.W)
  val rxfifo_valid = UInt(1.W)

  // ビット配置
  val bitOrder = Seq(
    txfifo_full, txfifo_empty,
    rxfifo_full, rxfifo_valid)

  //
  def write(v: UInt): Unit = {
    bitOrder.zip(v.asBools()).foreach{ case(r, b) => r := b }
  }

  def read(): UInt = Cat(bitOrder)
}

/**
  * デバッグ用のBundle
  */
class CSRDebugIO extends Bundle {
  val rx_fifo = Output(UInt(8.W))
  val tx_fifo = Output(UInt(8.W))
  val stat = Output(UInt(8.W))
}

/**
  * CSRとCtrl間のI/F
  */
class CSR2CtrlIO extends Bundle {
  val tx = new FIFORdIO(UInt(8.W))
  val rx = new FIFOWrIO(UInt(8.W))
}

/**
  * CSR部のIOクラス
  * @param p CSRブロックのパラメータ
  * @param debug Trueでデバッグ用のIOが有効になる
  */
class CSRIO(p: SimpleIOParams)(implicit debug: Boolean = false) extends Bundle {
  val sram = Flipped(new SimpleIO(p))
  val r2c = new CSR2CtrlIO()
  val dbg = if (debug) Some(new CSRDebugIO) else None

  override def cloneType: this.type =
    new CSRIO(p).asInstanceOf[this.type ]
}

/**
  * レジスタブロック
  * @param sp IOの設定
  * @param debug デバッグ出力のON/OFF
  */
class CSR(sp: SimpleIOParams)(implicit debug: Boolean = false) extends Module {

  val io = IO(new CSRIO(sp))

  // FIFOの段数
  val fifoDepth = 16

  val m_rx_fifo = Module(new FIFO(UInt(8.W), fifoDepth))
  val m_tx_fifo = Module(new FIFO(UInt(8.W), fifoDepth))
  val w_stat = WireInit(0.U.asTypeOf(new StatReg))

  // レジスタのアクセス制御信号
  val w_rdsel_rxfifo = (io.sram.addr === RegInfo.rxFifo.U) && io.sram.rden
  val w_rdsel_txfifo = (io.sram.addr === RegInfo.txFifo.U) && io.sram.wren
  val w_rdsel_stat = (io.sram.addr === RegInfo.stat.U) && io.sram.rden

  // statusレジスタの接続
  w_stat.txfifo_empty := m_tx_fifo.io.rd.empty
  w_stat.txfifo_full := m_tx_fifo.io.wr.full
  w_stat.rxfifo_full := m_rx_fifo.io.wr.full
  w_stat.rxfifo_valid := !m_rx_fifo.io.rd.empty

  // リードの制御
  io.sram.rddv := RegNext(w_rdsel_rxfifo || w_rdsel_stat, false.B)
  io.sram.rddata := RegNext(MuxCase(0.U, Seq(
    w_rdsel_rxfifo -> m_rx_fifo.io.rd.data,
    w_rdsel_stat -> w_stat.read())), 0.U)

  // Ctrl部との接続
  io.r2c.tx <> m_tx_fifo.io.rd
  m_tx_fifo.io.wr.enable := w_rdsel_txfifo
  m_tx_fifo.io.wr.data := io.sram.wrdata

  io.r2c.rx <> m_rx_fifo.io.wr
  m_rx_fifo.io.rd.enable := w_rdsel_rxfifo

  // debug
  if (debug) {
    val dbg = io.dbg.get
    dbg.rx_fifo := m_rx_fifo.io.rd.data
    dbg.tx_fifo := m_tx_fifo.io.wr.data
    dbg.stat := w_stat.read()
  }
}
