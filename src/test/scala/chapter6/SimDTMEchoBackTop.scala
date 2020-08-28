// See README.md for license details.

package chapter6

import chapter5.FIFORdIO
import chapter6.uart.{Ctrl, UartTx}
import chisel3._
import chisel3.util._
import test.util._

import scala.math.{pow, round}

/**
  * EchoBackTopモジュールの検証環境のトップモジュール
  * @param p EchoBackTopのパラメータ設定
  * @param baudrate ボーレート
  * @param clockFreq クロックの周波数
  * @param testData テストデータ
  * @param limit シミュレーションの最大サイクル数
  */
class SimDTMEchoBackTop(p: SimpleIOParams,
                        baudrate: Int = 9600,
                        clockFreq: Int = 100)
                       (testData: Seq[Int] = Seq(0xa5, 0x2a), limit: Int)
  extends BaseSimDTM(limit) {
  val io = IO(new Bundle with BaseSimDTMIO {
    val uart_rx = Output(UInt(1.W))
  })

  // UART送信処理
  val durationCount = round(clockFreq * pow(10, 6) / baudrate).toInt

  val m_uart_tx = Module(new Ctrl(UartTx, durationCount))
  val r_data_count = RegInit(0.U(log2Ceil(testData.length + 1).W))
  val reg_io = m_uart_tx.io.reg.asInstanceOf[FIFORdIO[UInt]]
  val w_tdata = VecInit(testData.map(_.U))

  when (reg_io.enable) {
    r_data_count := r_data_count + 1.U
  }

  reg_io.empty := r_data_count === testData.length.U
  reg_io.data := w_tdata(r_data_count)

  // テストモジュールのインスタンス
  val dut = Module(new EchoBackTop(p, baudrate, clockFreq))

  dut.io.rx := m_uart_tx.io.uart
  io.uart_rx := dut.io.tx

  connect(false.B)
}
