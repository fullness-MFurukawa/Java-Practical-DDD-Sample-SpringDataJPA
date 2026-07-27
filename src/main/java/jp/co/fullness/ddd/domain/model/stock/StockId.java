package jp.co.fullness.ddd.domain.model.stock;

import java.util.Objects;
import java.util.UUID;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * 商品在庫を一意に識別する値オブジェクト。
 * - 不変／自己検証／値で等価
 * - 内部表現はUUIDのcanonical文字列(小文字・ハイフン付き、36文字)
 */
public final class StockId {

    /** canonicalなUUID文字列(不変) */
    private final String value;

    /**
     * コンストラクタ。
     * 外部から直接は生成させない(不変保証のため)。
     */
    private StockId(String value) {
        this.value = value;
    }

    /**
     * 新しいUUIDを発行してStockIdを生成する。
     * UUIDはcanonical(小文字・ハイフン付き)で返る。
     */
    public static StockId createNew() {
        String canonical = UUID.randomUUID().toString();
        return new StockId(canonical);
    }

    /**
     * 既存の UUID 文字列から StockId を復元する。
     * 大小問わず受け付け、内部ではcanonical(小文字)に正規化する。
     *
     * @param raw UUID 文字列(ハイフン付き36文字)
     * @throws DomainException UUID 形式でない場合
     */
    public static StockId fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("StockId は必須です。");
        }
        String s = raw.trim();
        // 8-4-4-4-12の厳密マッチ(半角ハイフン U+002D、16進のみ)
        if (!s.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new DomainException("StockId は UUID 形式で指定してください。: " + raw);
        }
        // UUIDとして正規化(常に小文字・ハイフン付き)
        String canonical = UUID.fromString(s).toString();
        return new StockId(canonical);
    }

    /**
     * 正規化済みのUUID文字列を返す(常に小文字・ハイフン付き、36文字)。
     */
    public String value() {
        return value;
    }

    /**
     * 文字列表現(保持している値をそのまま返す)。
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * 値で等価判定(同じUUID文字列なら等価)。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockId)) return false;
        StockId that = (StockId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
