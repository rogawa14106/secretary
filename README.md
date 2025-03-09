# 要件

- WebUIで予定を登録、閲覧、編集、削除
- WebUIで登録済み一覧確認
- 登録した予定をLineでリマインド

# 概要

## リソース

- DBサーバ(予定情報管理)
- Webサーバ(View)
- APサーバ(Controller)(予定登録処理)
- 定期実行サーバ(cloneでLineAPIを叩いてリマインド処理)

## 処理流れ

### 予定登録

1. WebUIで入力受付
1. APサーバで入力処理
1. DBサーバへ登録

### リマインド

1. cloneでDB検索を定期実行(AM7:00)
1. 今日中(AM8:00~次の日のAM7:00)に予定がある場合はLineAPIを叩く

# 詳細

## UI層(View)

- ユーザの入力受付、AP層へ渡す
- 予定一覧表示

## AP層(Controller, Service)

- /v1/schedules GET 予定情報取得
- /v1/schedules POST 予定情報登録
- /v1/schedules PATCH 予定情報修正
- /v1/schedules DELETE 予定情報削除

## DB層(Repository, Model)

postgresql

### 予定テーブル(schedules)

- id(一意\*) integer unique
- title(予定タイトル\*) text not null
- isAllDay(終日予定かどうか) boolean not null
- datetime(予定の日時\*) timestamptz not null
- endDatetime(予定の終了日時) timestamptz not null 追加
- owner(予定を登録した人\*) text not null
- description(予定の説明) text
- updateTime(予定の更新日\*) timestamptz not null

```psql
create datebase secretary;
create table schedules (
    id serial primary key,
    title text not null,
    is_all_day boolean not null,
    datetime timestamptz not null,
    end_datetime timestamptz not null,
    owner text not null,
    description text,
    update_time timestamptz not null
);
```

## リマインド処理

- cloneで1hごとに判定処理

# API Test

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

# maven コマンド集

mvn clean
mvn clean install
mvn clean package
mvn eclipse:eclipse
