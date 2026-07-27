package jp.co.fullness.ddd.domain.model.category;

import java.util.Objects;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * カテゴリ名の値オブジェクト。
 * - 不変／自己検証／値で等価
 * - 仕様:
 *   ・必須(null/空/空白のみ不可)
 *   ・最大20文字
 *   ・前後空白はトリム
 */
public final class CategoryName {

    /** カテゴリ名の最大長 */
    private static final int MAX_LENGTH = 20;

    private final String value;

    /**
     * コンストラクタ。
     * 外部から直接は生成させない(不変保証のため)。
     */
    private CategoryName(String value) {
        this.value = value;
    }

    /**
     * CategoryName を生成する。
     * 検証: 必須・最大長・トリムを適用。
     *
     * @param raw 入力文字列
     * @return 検証済みの CategoryName
     * @throws DomainException 不正値の場合
     */
    public static CategoryName of(String raw) {
        if (raw == null) {
            throw new DomainException("カテゴリ名は必須です。");
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new DomainException("カテゴリ名は空にできません。");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new DomainException("カテゴリ名は" + MAX_LENGTH + "文字以内で指定してください。: " + trimmed);
        }
        return new CategoryName(trimmed);
    }

    /**
     * 保持している値を返す。
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
     * 値で等価判定。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryName)) return false;
        CategoryName that = (CategoryName) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
