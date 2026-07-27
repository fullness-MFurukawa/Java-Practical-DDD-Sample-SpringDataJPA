package jp.co.fullness.ddd.domain.model.category;

import java.util.Objects;
import java.util.UUID;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * カテゴリを一意に識別する値オブジェクト。
 * - 不変／自己検証／値で等価
 * - 内部表現はUUIDのcanonical文字列(小文字・ハイフン付き、36文字)
 */
public final class CategoryId {

    /** canonical な UUID 文字列 */
    private final String value;

    /**
     * コンストラクタ。
     * 外部から直接は生成させない(不変保証のため)。
     */
    private CategoryId(String value) {
        this.value = value;
    }

    /**
     * 新しいUUIDを発行してCategoryIdを生成する。
     */
    public static CategoryId createNew() {
        // UUID#toString は canonical(小文字・ハイフン付き)で返る
        String canonical = UUID.randomUUID().toString();
        return new CategoryId(canonical);
    }

    /**
     * 既存のUUID文字列からCategoryIdを復元する。
     * 入力は大文字/小文字を問わず受け付けるが、内部ではcanonical(小文字)に正規化する。
     *
     * @param raw UUID 文字列(ハイフン付き36文字)
     * @throws DomainException UUID 形式でない場合
     */
    public static CategoryId fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("CategoryId は必須です。");
        }
        String s = raw.trim();
        // 8-4-4-4-12の厳密マッチ(半角ハイフンU+002D、16進のみ)
        if (!s.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new DomainException("CategoryIdはUUID形式で指定してください。: " + raw);
        }
        // UUIDとして正規化(小文字・ハイフン付き)
        String canonical = UUID.fromString(s).toString();
        return new CategoryId(canonical);
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
        if (!(o instanceof CategoryId)) return false;
        CategoryId that = (CategoryId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
