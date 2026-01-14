# BoxKeyFrameAnimation

本リポジトリは、Java Swing を用いたキーフレームアニメーションシステムのプロジェクトです。
物理演算とキーフレーム補間を組み合わせた、インタラクティブなアニメーションツールです。


<img width="1919" height="1029" alt="image" src="https://github.com/user-attachments/assets/5d9e1062-11ea-4929-848c-6040cd93f353" />


## 🎯 プロジェクト概要 (Overview)

このプロジェクトは、以下の機能を提供します：

*   **キーフレームアニメーション**: 時間軸上に配置したキーフレーム間を補間してスムーズなアニメーションを実現
*   **物理演算**: 重力、弾性、摩擦などの物理パラメータをリアルタイムで調整可能
*   **ビジュアルタイムライン**: キーフレームの追加・削除・移動を直感的に操作できるUIパネル
*   **パラメータスライダー**: 速度、加速度、摩擦係数などをリアルタイムで変更

### 主要クラス構成

*   `PinBall.java` - アプリケーションのエントリーポイント
*   `AnimationPanel.java` - 描画とフレーム更新を担当
*   `Box.java` - アニメーション対象の物体
*   `KeyFrameTimeline.java` - キーフレーム管理とタイムライン制御
*   `TimelinePanel.java` - タイムラインのUI表示
*   `ParameterSlider.java` - 物理パラメータ調整用スライダー

---

## 📖 フォルダ構成 (Folder Structure)

```
BoxKeyFrameAnimation/
├── src/
│   ├── report/              # メインソースコード
│   │   ├── PinBall.java     # エントリーポイント
│   │   ├── AnimationPanel.java
│   │   ├── Box.java
│   │   ├── Ball.java
│   │   ├── KeyFrame.java
│   │   ├── KeyFrameData.java
│   │   ├── KeyFrameTimeline.java
│   │   ├── TimelinePanel.java
│   │   ├── ParameterSlider.java
│   │   ├── AnimationConfig.java
│   │   └── UIStyles.java
│   └── resources/           # リソースファイル（フォント、画像等）
├── bin/                     # コンパイル済みクラスファイル（Git管理外）
├── lib/                     # 外部ライブラリ（必要に応じて）
└── .gitignore               # Git除外設定
```

---

## 🚀 セットアップ手順 (Getting Started)

### 1. リポジトリのクローン

```bash
git clone [Repository URL]
cd BoxKeyFrameAnimation
```

### 2. プロジェクトのコンパイル

```bash
# srcディレクトリからコンパイル
javac -d bin -sourcepath src src/report/*.java
```

### 3. アプリケーションの実行

```bash
# binディレクトリのクラスファイルを実行
java -cp bin report.PinBall
```

---

## 🎮 使い方 (Usage)

### 基本操作

*   **再生/停止**: タイムラインパネルの再生ボタンでアニメーションを制御
*   **キーフレーム追加**: タイムライン上の任意の位置をクリック
*   **キーフレーム編集**: キーフレームマーカーをドラッグして時間位置を変更
*   **パラメータ調整**: 右側のスライダーで物理パラメータをリアルタイム変更

### パラメータ

*   **重力 (Gravity)**: 物体に働く下向きの加速度
*   **弾性係数 (Elasticity)**: 壁や床との衝突時の跳ね返り係数
*   **摩擦係数 (Friction)**: 速度減衰の度合い
*   **初速度 (Velocity)**: アニメーション開始時の速度ベクトル

---
