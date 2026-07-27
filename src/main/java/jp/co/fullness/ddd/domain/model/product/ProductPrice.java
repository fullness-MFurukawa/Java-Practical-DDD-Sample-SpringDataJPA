package jp.co.fullness.ddd.domain.model.product;

import java.util.Objects;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * 商品単価を表す値オブジェクト。
 * - 不変／自己検証／値で等価
 * - 仕様:
 *   ・必須(null不可)
 *   ・50以上10000以下を有効値とする
 */
public final class ProductPrice {

    /** 最小値 */
    private static final int MIN_PRICE = 50;

    /** 最大値 */
    private static final int MAX_PRICE = 10000;

    /** 不変な単価 */
    private final Integer value;

    /**
     * コンストラクタ。
     * 外部から直接は生成させない(不変保証のため)。
     */
    private ProductPrice(Integer value) {
        this.value = value;
    }

    /**
     * ProductPriceを生成する。
     * <p>検証ルール:
     * <ul>
     *   <li>必須(null不可)</li>
     *   <li>50以上10000以下でなければならない</li>
     * </ul>
     *
     * @param raw 入力単価
     * @return 検証済みの ProductPrice
     * @throws DomainException 不正値の場合
     */
    public static ProductPrice of(Integer raw) {
        if (raw == null) {
            throw new DomainException("商品単価は必須です。");
        }
        if (raw < MIN_PRICE || raw > MAX_PRICE) {
            throw new DomainException(
                "商品単価は " + MIN_PRICE + " 以上 " + MAX_PRICE + " 以下で指定してください。: " + raw
            );
        }
        return new ProductPrice(raw);
    }

    /**
     * 単価の整数値を返す。
     */
    public Integer value() {
        return value;
    }

    /**
     * 文字列表現(保持している値を文字列として返す)。
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * 値で等価判定。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPrice)) return false;
        ProductPrice that = (ProductPrice) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}