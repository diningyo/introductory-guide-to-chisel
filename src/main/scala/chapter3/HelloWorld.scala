// See README.md for license details.

package chapter3

/**
 * 普通のメイン関数
 */
object HelloWorld {
  def main(args: Array[String]): Unit = println("Hello, World!!")
}

/**
  * Appトレイトを使ったメイン関数
  */
object HelloWorldWithApp extends App {
  println("Hello, World!!")
}
