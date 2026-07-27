package jp.co.fullness.ddd.domain.model.stock;

import java.util.Objects;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * ドメインエンティティ：{@code Stock}(商品在庫)
 *
 * <p>このクラスは、商品ごとの在庫数量を表すドメインの実体(Entity)です。
 * 同一性(Identity)を持ち、在庫数という状態(State)を保持し、
 * 在庫増減などの振る舞い(Behavior)を自身の責務として担います。
 *
 * <p>【ドメイン上の意味】
 * <ul>
 *   <li>「倉庫内の在庫数量」を表すビジネス上の概念を具現化</li>
 *   <li>在庫数量の加算・減算といったルールを自ら管理</li>
 *   <li>外部から直接状態を変更させず、不変条件を内部で保持</li>
 * </ul>
 *
 * <p>【設計上の特徴】
 * <ul>
 *   <li>同一性: {@link StockId} により一意に識別される</li>
 *   <li>属性: {@link StockQuantity}(0〜100の範囲を自己検証)</li>
 *   <li>不変条件: IDと数量はnull不可、数量は常に0〜100</li>
 *   <li>状態変更: 業務ルールを満たす場合のみ数量を変更可能</li>
 * </ul>
 *
 * <p>【ユビキタス言語】
 * <ul>
 *   <li>「在庫(Stock)」＝倉庫にある商品の個数</li>
 *   <li>「在庫切れ」＝数量が0</li>
 *   <li>「満杯」＝数量が上限(100)に達している状態</li>
 * </ul>
 *
 * <p>【設計意図】
 * <br>DDDにおけるEntityとして、在庫という概念の一貫性を保ちつつ、
 * ドメインルールを侵さないよう、状態変化の入り口を限定しています。
 *
 * <p>例：
 * <pre>{@code
 * Stock s = Stock.createNew(StockQuantity.of(10));
 * s.increase(5);  // 在庫を追加
 * s.decrease(8);  // 在庫を減らす
 * if (s.isOutOfStock()) {
 *     System.out.println("在庫切れです");
 * }
 * }</pre>
 */
public final class Stock {

    /** 在庫の同一性(不変) */
    private final StockId stockId;

    /** 在庫数(VO) */
    private StockQuantity quantity;

    /**
     * コンストラクタ(private)。
     * <p>不変条件(nullチェック)を集約し、外部からの直接生成を禁止する。
     *
     * @param id       在庫ID
     * @param quantity 在庫数
     */
    private Stock(StockId id, StockQuantity quantity) {
        if (id == null)       throw new DomainException("在庫IDは必須です。");
        if (quantity == null) throw new DomainException("在庫数は必須です。");
        this.stockId = id;
        this.quantity = quantity;
    }

    /**
     * 新しい在庫エンティティを生成する。
     * <p>主に新規登録時に使用する。内部で新しいIDを発行する。
     *
     * @param initialQuantity 初期在庫数(必須)
     * @return 新しい {@code Stock} エンティティ
     * @throws DomainException 初期数量がnullの場合
     */
    public static Stock createNew(StockQuantity initialQuantity) {
        if (initialQuantity == null) {
            throw new DomainException("在庫数は必須です。");
        }
        return new Stock(StockId.createNew(), initialQuantity);
    }

    /**
     * 既存データを復元(リストア)する。
     * <p>リポジトリやテストなど、永続化層から既存エンティティを再構築する際に使用する。
     *
     * @param id       在庫ID(必須)
     * @param quantity 在庫数(必須)
     * @return 再構築された {@code Stock}
     */
    public static Stock restore(StockId id, StockQuantity quantity) {
        return new Stock(id, quantity);
    }

    /**
     * 在庫数を加算する。
     * <p>加算後の値が範囲外の場合、{@link StockQuantity#of(Integer)} が例外を送出する。
     *
     * @param amount 加算量(0以上)
     * @throws DomainException 加算量が負数の場合、または加算後が範囲外の場合
     */
    public void increase(int amount) {
        if (amount < 0) throw new DomainException("在庫の増分は0以上で指定してください。: " + amount);
        int newValue = this.quantity.value() + amount;
        this.quantity = StockQuantity.of(newValue);
    }

    /**
     * 在庫数を減算する。
     * <p>減算後の値が範囲外の場合、{@link StockQuantity#of(Integer)} が例外を送出する。
     *
     * @param amount 減算量(0以上)
     * @throws DomainException 減算量が負数の場合、または減算後が範囲外の場合
     */
    public void decrease(int amount) {
        if (amount < 0) throw new DomainException("在庫の減分は0以上で指定してください。: " + amount);
        int newValue = this.quantity.value() - amount;
        this.quantity = StockQuantity.of(newValue);
    }

    /**
     * 在庫数を直接変更する。
     * <p>テストやシステム再構築など、特殊な用途に使用する。
     *
     * @param newQuantity 新しい在庫数(必須)
     * @throws DomainException 数量がnullの場合
     */
    public void changeQuantity(StockQuantity newQuantity) {
        if (newQuantity == null) throw new DomainException("在庫数は必須です。");
        this.quantity = newQuantity;
    }

    /** @return 在庫ID */
    public StockId getStockId() {
        return stockId;
    }

    /** @return 現在の在庫数 */
    public StockQuantity getQuantity() {
        return quantity;
    }

    /** @return 在庫が0の場合にtrue */
    public boolean isOutOfStock() {
        return quantity.value() == StockQuantity.MIN;
    }

    /** @return 在庫が上限値の場合にtrue */
    public boolean isFullCapacity() {
        return quantity.value() == StockQuantity.MAX;
    }

    /**
     * StockIdによる等価性検証。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock other = (Stock) o;
        return Objects.equals(stockId, other.stockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId);
    }

    /**
     * インスタンスの内容(デバッグ用)。
     */
    @Override
    public String toString() {
        return "Stock{id=" + stockId + ", quantity=" + quantity + "}";
    }
}
