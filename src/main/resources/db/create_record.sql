-- =============================================================
--  ドメイン駆動設計実践 Java編 — サンプルDB定義 (PostgreSQL)
--  データベース名: restapi_exercise
--  テーブル: product_category / product / product_stock
--  ※テキストのテーブル定義書・概念ドメインモデルに準拠
-- =============================================================

-- =============================================================
-- 2. サンプルデータ
--    ※Idは自動生成に任せ、外部キーは名前で解決して投入します。
-- =============================================================

-- 2-1. カテゴリ
INSERT INTO product_category (category_uuid, name)
SELECT gen_random_uuid()::text, name
FROM (VALUES
    ('文房具'),
    ('雑貨'),
    ('食品'),
    ('飲料'),
    ('書籍')
) AS v(name);

-- 2-2. 商品（カテゴリ名で category_id を解決）
INSERT INTO product (product_uuid, name, price, category_id)
SELECT gen_random_uuid()::text, v.name, v.price, c.id
FROM (VALUES
    ('油性ボールペン',       120,  '文房具'),
    ('水性マーカー',         180,  '文房具'),
    ('蛍光ペン',             150,  '文房具'),
    ('A5ノート',             240,  '文房具'),
    ('修正テープ',           320,  '文房具'),
    ('陶器マグカップ',       880,  '雑貨'),
    ('トートバッグ',        1500,  '雑貨'),
    ('折りたたみ傘',        2200,  '雑貨'),
    ('ミックスナッツ',       650,  '食品'),
    ('ドリップコーヒー',     980,  '食品'),
    ('緑茶ペットボトル',     140,  '飲料'),
    ('炭酸水',               100,  '飲料'),
    ('技術書入門',          3200,  '書籍'),
    ('文庫本',               780,  '書籍')
) AS v(name, price, cat)
JOIN product_category c ON c.name = v.cat;

-- 2-3. 商品在庫（商品名で product_id を解決。一部は在庫0）
INSERT INTO product_stock (stock_uuid, stock, product_id)
SELECT gen_random_uuid()::text, v.stock, p.id
FROM (VALUES
    ('油性ボールペン',    80),
    ('水性マーカー',      45),
    ('蛍光ペン',          60),
    ('A5ノート',          30),
    ('修正テープ',         0),   -- 在庫切れ
    ('陶器マグカップ',    25),
    ('トートバッグ',      12),
    ('折りたたみ傘',       8),
    ('ミックスナッツ',    40),
    ('ドリップコーヒー',  50),
    ('緑茶ペットボトル',  90),
    ('炭酸水',            75),
    ('技術書入門',         5),
    ('文庫本',            33)
) AS v(pname, stock)
JOIN product p ON p.name = v.pname;

-- =============================================================
-- 3. 確認用クエリ（任意）
-- =============================================================
-- SELECT c.name AS category, p.name AS product, p.price, s.stock
-- FROM product p
-- JOIN product_category c ON c.id = p.category_id
-- LEFT JOIN product_stock s ON s.product_id = p.id
-- ORDER BY c.id, p.id;