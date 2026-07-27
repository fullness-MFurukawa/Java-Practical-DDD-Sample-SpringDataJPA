package jp.co.fullness.ddd.domain.model.stock;

import java.util.Objects;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * 商品在庫数を表す値オブジェクト。
 * - 不変／自己検証／値で等価
 * - 仕様:
 *   ・必須(null不可)
 *   ・0以上100以下を有効値とする
 */
public final class StockQuantity {

    /** 最小値 */
    static final int MIN = 0;

    /** 最大値 */
    static final int MAX = 100;

    /** 不変な在庫数 */
    private final Integer value;

    /**
     * コンストラクタ。
     * 外部から直接は生成させない(不変保証のため)。
     */
    private StockQuantity(Integer value) {
        this.value = value;
    }

    /**
     * StockQuantity を生成する。
     * <p>検証ルール:
     * <ul>
     *   <li>null不可</li>
     *   <li>0以上100以下</li>
     * </ul>
     *
     * @param raw 入力在庫数
     * @return 検証済みの StockQuantity
     * @throws DomainException 不正値の場合
     */
    public static StockQuantity of(Integer raw) {
        if (raw == null) {
            throw new DomainException("在庫数は必須です。");
        }
        if (raw < MIN || raw > MAX) {
            throw new DomainException(
                "在庫数は " + MIN + " 以上 " + MAX + " 以下で指定してください。: " + raw
            );
        }
        return new StockQuantity(raw);
    }

    /**
     * 在庫数の整数値を返す。
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
        if (!(o instanceof StockQuantity)) return false;
        StockQuantity that = (StockQuantity) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
