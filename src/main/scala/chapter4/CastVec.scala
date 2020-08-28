// See README.md for license details.

package chapter4

import chisel3._

/**
  * UIntをVecに変換するサンプル
  */
class CastUIntToVec extends Module {
  val io = IO(new Bundle {})

  val w_uint = 0xa5.U

  // w_uintを要素数2のUIntのVecにキャスト
  val w_uint_to_vec = WireDefault(w_uint.asTypeOf(Vec(2, UInt(4.W))))

  printf("w_uint = %x\n", w_uint)
  for ((vec, i) <- w_uint_to_vec.zipWithIndex) {
    printf("w_uint_to_vec(%d) : %x\n", i.U, vec)
  }
}

/**
  * BundleからVecへのキャストのサンプル
  */
class SampleCastBundleToVec extends Module {
  val io = IO(new Bundle {})

  val sb = Wire(new SomeBundle)

  sb.a := true.B
  sb.b := 5.U

  val sbToVec = WireDefault(sb.asTypeOf(Vec(7, Bool())))

  for ((vec, i) <- sbToVec.zipWithIndex) {
    printf("%d : %x\n", i.U, vec)
  }
}
