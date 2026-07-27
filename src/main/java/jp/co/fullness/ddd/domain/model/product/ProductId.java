package jp.co.fullness.ddd.domain.model.product;

import java.util.Objects;
import java.util.UUID;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * 商品を一意に識別するための値オブジェクト。
 *
 * <p>このクラスは「Product(商品)」の永続的な識別子を表し、
 * 値の正当性を内部で保証し、誤ったID文字列がシステム内に流通しないようにする。
 *
 * <p>仕様・制約:
 * <ul>
 *   <li>内部表現は UUID(canonical形式: 小文字・ハイフン付き、36文字固定)</li>
 *   <li>外部から直接生成することはできない(不変性を維持)</li>
 *   <li>{@link #createNew()} により新しいUUIDを発行可能</li>
 *   <li>{@link #fromString(String)} により既存のUUID文字列を復元可能</li>
 *   <li>等価性は値(UUID文字列)の一致によって判定される</li>
 * </ul>
 *
 * <p>【設計意図】
 * <br>商品IDは単なる文字列ではなく、「生成・検証・正規化」の責務を持つドメイン型とすることで、
 * システム全体で「正しい形式のIDしか存在しない」状態を保証する。
 * これにより、DBキーやAPI入力値の不正混入を防ぎ、ドメイン整合性を維持する。
 *
 * <p>例：
 * <pre>{@code
 * ProductId id1 = ProductId.createNew();             // 新規発行
 * ProductId id2 = ProductId.fromString("...");       // 既存UUID文字列から復元
 * boolean same = id1.equals(id2);                    // 値で等価比較
 * }</pre>
 *
 * @see java.util.UUID
 */
public final class ProductId {

    /** canonicalなUUID文字列(不変) */
    private final String value;

    /**
     * コンストラクタ。
     * 外部から直接は生成させない(不変保証のため)。
     */
    private ProductId(String value) {
        this.value = value;
    }

    /**
     * 新しいUUIDを発行してProductIdを生成する。
     * UUID#toString は canonical(小文字・ハイフン付き)で返る。
     *
     * @return 新規生成された ProductId
     */
    public static ProductId createNew() {
        String canonical = UUID.randomUUID().toString();
        return new ProductId(canonical);
    }

    /**
     * 既存のUUID文字列からProductIdを復元する。
     * 入力は大文字/小文字を問わず受け付けるが、内部ではcanonical(小文字)に正規化する。
     *
     * @param raw UUID文字列(ハイフン付き36文字)
     * @return 検証済みのProductId
     * @throws DomainException UUID形式でない場合
     */
    public static ProductId fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("ProductIdは必須です。");
        }
        String s = raw.trim();
        // UUIDの厳密検証(8-4-4-4-12形式)
        if (!s.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new DomainException("ProductIdはUUID形式で指定してください。: " + raw);
        }
        // UUIDとして正規化(常に小文字・ハイフン付き)
        String canonical = UUID.fromString(s).toString();
        return new ProductId(canonical);
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
        if (!(o instanceof ProductId)) return false;
        ProductId that = (ProductId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
