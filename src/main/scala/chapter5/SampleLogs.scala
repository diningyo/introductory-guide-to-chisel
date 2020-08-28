// See README.md for license details.

package chapter5

import chisel3._
import chisel3.util._

/**
  * Logxxxの計算結果を確認するためのサンプル
  */
object SampleLogs extends App {
  val a = Log2
  for (i <- 0x1 to 0x10) {
    println(
      f"| $i%2d | " +
      f"${log2Up(i)} | ${log2Up(i + 1)} | " +
      f"${log2Down(i)} | ${log2Down(i + 1)} | " +
      f"${log2Ceil(i)} | ${log2Ceil(i + 1)} | " +
      f"${log2Floor(i)} | ${log2Floor(i + 1)} | " +
      f"${isPow2(i)} |")
  }
}

