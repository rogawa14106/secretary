# 要件

- WebUIで予定を登録
- 予定の一時間前にLineでリマインド

# 概要

## リソース

- DBサーバ(Model)(予定情報管理)
- Webサーバ(View)(UI)
- APサーバ(Controller)(予定登録処理)
- 定期実行サーバ(cloneでLineAPIを叩いてリマインド処理)

## 実装

### 予定登録フロー

1. Webサーバで入力受付
1. APサーバへ情報送信
1. APサーバからDBサーバへ登録

### リマインドフロー

1. cloneでDB検索を定期実行(AM7:00)
1. 今日中(AM8:00~次の日のAM7:00)に予定がある場合はLineAPIを叩く

# 詳細

## Webサーバ処理

- UIから予定入力受付、APサーバへ渡す
- 予定一覧を表示

## APサーバ処理

- /v1/schedules GET 予定情報取得
- /v1/schedules POST 予定情報登録
- /v1/schedules PATCH 予定情報修正
- /v1/schedules DELETE 予定情報削除

## DB

### 予定テーブル(schedules)

- id(一意\*) integer unique
- title(予定タイトル\*) text not null
- datetime(予定の日時\*) timestamptz not null
- user(予定を登録した人\*) text not null
- description(予定の説明) text
- updateTime(予定の更新日\*) timestamptz not null

## リマインド処理

- cloneで1hごとに判定処理
- 予定の1h前にLine送信

# 詳細

## Web

webUI表示
ユーザから入力受取
Applicationへ以下データを渡す

## Application

webから入力受取
Serviceへリクエストを渡す

## Service

Applicationからリクエスト受取
整形
datetime:Date型に変換
updateTime:現在時刻を入れる
Repositoryへリクエストを渡す

## Repository

Serviceからリクエスト受取
DB接続

# Test

- GET
  curl -X GET http://localhost:8080/api/v1/schedules -H "Content-Type:Application/json"
- GET
  curl -X GET http://localhost:8080/api/v1/schedules/1 -H "Content-Type:Application/json"
- POST
  curl -X POST http://localhost:8080/api/v1/schedules -H "Content-Type:Application/json" -d "{\"title\":\"test\", \"datetime\":\"2025/03/07-12:30\", \"owner\": \"rogawa\", \"description\":\"test schedule\"}"
- PATCH
  curl -X PATCH http://localhost:8080/api/v1/schedules/1 -H "Content-Type:Application/json" -d "{\"title\":\"test2\"}"
- DELETE
  curl -X DELETE http://localhost:8080/api/v1/schedules/2

# maven コマンド週

mvn clean
mvn clean install
mvn clean package
mvn eclipse:eclipse
