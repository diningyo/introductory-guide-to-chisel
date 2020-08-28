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

例えばchapter4の`SampleReg`のRTLを生成する場合は以下のようになります。

```scala
runMain Generator chapter4.SampleReg
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
