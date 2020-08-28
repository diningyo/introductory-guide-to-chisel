// See README.md for license details.

package test.util

import chisel3._

/**
  * BaseSimDTMで追加するIOポート
  */
trait BaseSimDTMIO extends Bundle {
  val timeout = Output(Bool())
  val finish = Output(Bool())
}

/**
  * WDT入りのシミュレーション環境のトップモジュール
  * abortEnをtrueにするとlimitに指定した時間が経過するとWDTモジュール
  * のアサートでシミュレーションが強制終了する。
  * @param limit シミュレーションの最大サイクル数
  * @param abortEn タイムアウト時にシミュレーションを強制終了するフラグ
  */
abstract class BaseSimDTM(limit: Int, abortEn: Boolean = true)
  extends Module {
  // ioをBaseSimDTMIOで宣言しておく
  val io: BaseSimDTMIO
  val wdt = Module(new WDT(limit, abortEn))

  def connect(finish: Bool): Unit = {
    io.finish := finish
    io.timeout := wdt.io.timeout
  }
}
