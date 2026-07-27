package jp.co.fullness.ddd.domain.model.product;

import java.util.Objects;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * 商品名を表す値オブジェクト。
 * - 不変／自己検証／値で等価
 * - 仕様:
 *   ・必須(null/空/空白のみ不可)
 *   ・最大30文字
 *   ・前後の空白はトリムされる
 */
public final class ProductName {

    /** 商品名の最大長 */
    private static final int MAX_LENGTH = 30;

    /** 不変な値(トリム済み) */
    private final String value;

    /**
     * コンストラクタ。
     * 外部から直接は生成させない(不変保証のため)。
     */
    private ProductName(String value) {
        this.value = value;
    }

    /**
     * ProductName を生成する。
     * <p>検証ルール:
     * <ul>
     *   <li>必須(null不可)</li>
     *   <li>空文字または空白のみは不可</li>
     *   <li>最大30文字</li>
     *   <li>前後空白はトリムされる</li>
     * </ul>
     *
     * @param raw 入力文字列
     * @return 検証済みの ProductName
     * @throws DomainException 不正値の場合
     */
    public static ProductName of(String raw) {
        if (raw == null) {
            throw new DomainException("商品名は必須です。");
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new DomainException("商品名は空にできません。");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new DomainException(
                "商品名は" + MAX_LENGTH + "文字以内で指定してください。: " + trimmed
            );
        }
        return new ProductName(trimmed);
    }

    /**
     * 値を返す。
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
        if (!(o instanceof ProductName)) return false;
        ProductName that = (ProductName) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}