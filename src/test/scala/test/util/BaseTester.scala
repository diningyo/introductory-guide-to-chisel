// See README.md for license details.

package test.util

import chisel3.iotesters._
import org.scalatest.{BeforeAndAfterAllConfigMap, ConfigMap}

/**
  * テストクラスのベース
  * BeforeAndAfterAllConfigMapをミックスインすることで
  * testOnlyコマンド実行時にシミュレーションのオプション設定を行う
  * 機能を追加している。
  */
abstract class BaseTester extends ChiselFlatSpec
  with BeforeAndAfterAllConfigMap  {

  // testOnlyの引数未指定時の設定
  val defaultArgs = scala.collection.mutable.Map(
    "--generate-vcd-output" -> "on",
    "--backend-name" -> "verilator",
    "--is-verbose" -> false
  )

  /**
    * BeforeAndAfterAllConfigMapで定義されるbeforeAllメソッドを
    * オーバーライドして、実行時の引数を取得する
    * @param configMap ScalaTest ConfigMap
    */
  override def beforeAll(configMap: ConfigMap): Unit = {
    if (configMap.get("--backend-name").isDefined) {
      defaultArgs("--backend-name") =
        configMap.get("--backend-name").fold("")(_.toString)
    }
    if (configMap.get("--generate-vcd-output").isDefined) {
      defaultArgs("--generate-vcd-output") =
        configMap.get("--generate-vcd-output").fold("")(_.toString)
    }
    if (configMap.get("--is-verbose").isDefined) {
      defaultArgs("--is-verbose") = true
    }
  }

  /**
    * デフォルトの引数をiotesters.Driver.executeに渡せるように加工する
    * @param optArgs テストごとの引数設定（テストの実行ディレクトリの設定など）
    * @return 引数をArray[String]に変換したもの
    */
  def getArgs(optArgs: Map[String, Any]): Array[String] = {
    val argsMap = defaultArgs ++ optArgs
    argsMap.map {
      case (key: String, value: String) => s"$key=$value"
      case (key: String, value: Boolean) => if (value) key else ""
      case (_, _) => ""
    }.toArray
  }

  /**
    * テスト対象モジュールの名称を取得
    * 派生クラス側で実装が必須
    * @return テスト対象モジュールの名称
    */
  def dutName: String
}
