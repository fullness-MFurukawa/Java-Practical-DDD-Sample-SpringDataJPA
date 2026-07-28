-- =============================================================
--  ドメイン駆動設計実践 Java編 — サンプルDB定義 (PostgreSQL)
--  データベース名: restapi_exercise
--  テーブル: product_category / product / product_stock
--  ※テキストのテーブル定義書・概念ドメインモデルに準拠
-- =============================================================
 
-- -------------------------------------------------------------
-- 0. データベース作成
--    ※この2行は psql のメタコマンドを含むため、psql で実行してください。
--      例) psql -U postgres -f restapi_exercise.sql
--    既に存在する場合は「DROP DATABASE」の行を有効化して作り直せます。
-- -------------------------------------------------------------
-- DROP DATABASE IF EXISTS restapi_exercise;
CREATE DATABASE restapi_exercise
    WITH ENCODING = 'UTF8'
         TEMPLATE = template0
         LC_COLLATE = 'C'
         LC_CTYPE   = 'C';
 
\connect restapi_exercise
 
-- gen_random_uuid() を使うための拡張（PostgreSQL 13 未満でも動くように）
CREATE EXTENSION IF NOT EXISTS pgcrypto;