# Chiselを始めたい人に読んでほしい本のサンプルソースプロジェクト

## このプロジェクトは何？

「Chiselを始めたい人に読んでほしい本」で使用された全サンプルソースを含んだプロジェクトです。
chisel-templateを使用しているので、sbtを使った基本的なプロジェクトの構成に沿っています。
各サンプルソースについては、本書との対応付けと取りやすくするため"src/{main, test}のディレクトリの下に対応するchapterのパッケージディレクトリを作成して、そのディレクトリの下に格納しています。書籍のリストx-yの表記の部分にソースコードのパスが記載してありますので、そちらを確認していただくのが早いと思います。

## Chiselのバージョン

本文に記載したように、執筆時点では安定版として次の２つのバージョンが存在しています。

- Chisel 3.2.4
- Chisel 3.3.0

本リポジトリに存在するブランチとChiselのバージョンの対応は次のようになっています。

| ブランチ名 | Chiselのバージョン |
|----------|------------------|
| master | Chisel 3.2.4 |
| for-chisel-3.3.0 | Chisel 3.3.0 |

## RTLGenerator/SimRunner

簡単に動作確認を行うために`RTLGenerator`と`SimRunner`というオブジェクトを作成してあります。それぞれ以下のような処理を実行することが可能です。

- RTLGenerator : RTLの生成を行うメイン関数を含むオブジェクト
  - クラスパラメーターが存在しないChiselのモジュールなら、全部RTLを生成することが可能（なはず）
  - クラスパラメーターが必要なChiselのモジュールは、オブジェクト内で明示されているもののみ生成可能
- SimRunner : Chiselのテスト機構を使って1cycleのみのシミュレーションを実行するオブジェクト
  - 主に4章で解説したChiselの型のサンプルソース中の`printf`関数の表示を確認するために使用

使い方は以下のようになります。

```scala
runMain RTLGenerator <RTL化したいChiselモジュールのクラスパス>
runMain SimRunner <シミュレーションを実行したいChiselモジュールのクラスパス>
```

ここで`<シミュレーションを実行したいChiselモジュールのクラスパス>`はパッケージ名を含んだクラスパスとなります。
例えばchapter4の`SampleReg`のRTLを生成する場合は以下のようになります。

```scala
runMain Generator chapter4.SampleReg
```

## 本書の誤記の訂正と補足

本書を手に取ってくださった方から頂いたご指摘（誤記など）について記載しました。

確認が足りておらず、ご不便おかけしました。
また、ご指摘いただきありがとうございました！

ここに記載されているもの以外で、なにかお気づきの点があれば、本リポジトリのissueやtwitterでお知らせください。

### リスト3.4の実行コマンド

"リスト3.4:`sbt`を使ったメイン関数の実行"で実行するコマンドに間違いが有りました。

- 本文中の記載

```scala
sbt:scala_chk> runMain main
```

- 正しい記載

```scala
sbt:scala_chk> runMain HelloWorld
```

### リスト4.14のRTL生成のコマンドが失敗する

"リスト4.2のLチカ"のソースコードにはScalaのパッケージ宣言部分が掲載されておらず、そのままコードを写した後に"リスト4.14:Chisel のモジュールから RTL を生成"のコマンドを実行すると、クラスパスが間違った状態になり`runMain`コマンドが失敗します。

- 本文中の記載
  - 次のようにコードの先頭が`import`から始まっています

```scala
import math.pow

import chisel3._
```

- 正しい記載
  - ”リスト4.14”記載のコマンドを動作させるためには、`package chapter4`が必要になります

```scala
package chapter4 // package宣言が必要

import math.pow

import chisel3._
import chisel3.util._
```

 - [src/main/scala/chapter4/HelloChisel.scala#L1](https://github.com/diningyo/introductory-guide-to-chisel/blob/master/src/main/scala/chapter4/HelloChisel.scala#L1)

### リスト4.2のLチカのコードがビルドエラーになる

"リスト4.2のLチカ"のソースコードに間違いがあり、本文中のコードをそのまま写すと次のようなビルドエラーが発生します。

```scala
[info] running chapter4.ElaborateHelloChisel
[info] [0.001] Elaborating design...
[error] java.lang.NullPointerException
```

これは`w_msec_update`に接続される論理に、初期化が済んでいないレジスタ用の変数`r_count_msec`を使っていることが原因です。
そのため、次の訂正後のコードのように`val r_count_msec`の宣言を`w_msec_udpate`の前に移動する必要があります。

- 本文中の記載

```scala
  // r_count_msecの宣言場所がw_msec_updateの後にある
  // msec カウントのパルス
  val w_msec_update = Wire(Bool())
  w_msec_update := r_count_msec === (msecCounts - 1).U
  // msec カウント用のレジスタ
  val r_count_msec = RegInit(0.U(log2Ceil(msecCounts).W))
  val r_count_interval = RegInit(0.U(log2Ceil(blinkInterval).W))
```

- 正しい記載

```scala

  // r_count_msecの宣言場所はw_msec_updateより前になければならない
  // msec カウントのパルス
  val r_count_msec = RegInit(0.U(log2Ceil(msecCounts).W))
  val w_msec_update = Wire(Bool())
  w_msec_update := r_count_msec === (msecCounts - 1).U
  // msec カウント用のレジスタ
  val r_count_interval = RegInit(0.U(log2Ceil(blinkInterval).W))
```

修正後の正しいコードについては、本リポジトリの次のコードを参照ください。

 - [src/main/scala/chapter4/HelloChisel.scala#L26](https://github.com/diningyo/introductory-guide-to-chisel/blob/master/src/main/scala/chapter4/HelloChisel.scala#L26)


### 4.4.3 組み合わせ論理回路 - Wire / WireDefault (p.102)

本文中の次の説明の「Wireを作ることができます」は誤った説明でした。

> "Chiselのハードウェア"を`val`に格納しても、`Wire`を作ることができます。この方法で作成した場合は、元の"Chiselのハードウェア"の属性を引き継ぐことに注意してください。初期値として値を入れておきたい場合は、`WireDefault`を使用してください。

正しくは「左辺で定義した変数に、右辺の論理が束縛された」だけとなります。ふるまいとしては組み合わせ論理相当の動きとなりますが、定義された変数には`Wire`の属性が付与されていいないので、再代入を行おうとするとエラーになります（次のサンプルを参照下さい）。どちらかと言うと、エイリアスといった方が正確です。


```scala
class Sample extends Module {
  val io = IO(new Bundle {
    val in_0 = Input(UInt(2.W))
    val in_1 = Input(UInt(2.W))
    val sel = Input(Bool())
    val out_wire_add = Output(UInt(8.W))
    val out_reg_add = Output(UInt(8.W))
  })

  val w_wire_add = io.in_0 + io.in_1

  // Wireであれば、定義後に`:=`で再代入が可能だが
  // `w_wire_add`はWireではないので、エラーになる。
  w_wire_add := io.in_0

  val r_reg_0 = RegInit(io.in_0)
  val r_reg_1 = RegInit(io.in_1)

  // 右辺がレジスタの論理でも同様のあつかい
  val w_reg_add = r_reg_0 + r_reg_1

  // 次のコードはエラーになる
  w_reg_add := r_reg_0 + r_reg_1

  io.out_wire_add := w_wire_add
  io.out_reg_add := w_reg_add
}
```

上記のコードをエラボレートすると、次のようなエラーメッセージが出力されます。

```scala
[error] chisel3.internal.ChiselException: Cannot reassign to read-only UInt(OpResult in Sample)
[error]         ...
[error]         at Sample.<init>(Sample.scala:27)
[error]         at Elaborate$.$anonfun$rtl$1(Sample.scala:36)
[error]         ... (Stack trace trimmed to user code only, rerun with --full-stacktrace if you wish to see the full stack trace)
```

上記を回避するには`w_wire_add`を`Wire`として宣言した後、`:=`で論理を接続するか、`WireDefault`を使うようにして下さい。

- 回避方法

```scala
// Wireとして宣言
val w_wire_add = Wire(UInt(8.W))
w_wire_add := io.in_0 + io.in_1

// WireDefaultで宣言と同時に論理を接続
val w_wire_add = WireDefault(io.in_0 + io.in_1)
```


## サンプルコードの取り扱いについて

このサンプルコードを含んだプロジェクト一式についてはMIT Licenseにしてます。
今回は「Chiselを広めたい！」がモチベーションになっているので、Chiselを他の人に勧める
際に「このサンプル使えるやん！」と思ってもらえるのならぜひ使ってください！

### ライセンス

MIT License

Copyright (c) 2019-2020 diningyo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
