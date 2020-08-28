// See README.md for license details.

package chapter4

import chisel3._

/**
  * キャスト動作確認用Bundle1
  */
class SomeBundle extends Bundle {
  val a = UInt(4.W)
  val b = UInt(3.W)
}

/**
  * キャスト動作確認用Bundle2
  */
class OtherBundle extends Bundle {
  val a = UInt(4.W)
  val b = Vec(3, UInt(1.W))
}

/**
  * UIntをBundleにキャストするサンプル
  */
class SampleCastUIntToBundle extends Module {
  val io = IO(new Bundle {})

  val w_sb = WireDefault(0xa5.U.asTypeOf(new SomeBundle))
  val w_ob = WireDefault(0xa5.U.asTypeOf(new OtherBundle))

  printf("a.a = %d\n", w_sb.a)
  printf("a.b = %d\n", w_sb.b)

  printf("a.a = %d\n", w_ob.a)
  printf("a.b(0) = %d\n", w_ob.b(0)) // 1
  printf("a.b(1) = %d\n", w_ob.b(1)) // 0
  printf("a.b(2) = %d\n", w_ob.b(2)) // 1
}

/**
  * Bundleを別のBundleにキャストするサンプル
  */
class SampleCastBundleToBundle extends Module {
  val io = IO(new Bundle {})

  val w_sb = Wire(new SomeBundle)

  w_sb.a := 0xa.U
  w_sb.b := 0x5.U

  val w_ob = WireDefault(w_sb.asTypeOf(new OtherBundle))

  printf("w_sb.a = %x\n", w_sb.a)
  printf("w_sb.b = %x\n", w_sb.b)

  printf("w_ob.a = %x\n", w_ob.a)
  printf("w_ob.b(0) = %d\n", w_ob.b(0)) // 1
  printf("w_ob.b(1) = %d\n", w_ob.b(1)) // 0
  printf("w_ob.b(2) = %d\n", w_ob.b(2)) // 1
}