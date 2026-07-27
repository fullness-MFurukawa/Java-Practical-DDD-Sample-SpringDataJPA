package jp.co.fullness.ddd.infrastructure.product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jp.co.fullness.ddd.infrastructure.category.ProductCategoryEntity;
import jp.co.fullness.ddd.infrastructure.stock.ProductStockEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * product テーブルにマッピングする JPA エンティティ（永続化モデル）。
 *
 * <p>集約ルート Product に対応する。カテゴリは別集約なので参照のみ（cascade しない）、
 * 在庫は集約内部なので {@code cascade = ALL} で一括保存する。
 * ドメインへの変換は {@code ProductEntityMapper}（MapStruct）と {@code ProductAssembler} が担う。</p>
 */
@Getter
@Setter
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 識別Id（product_uuid, VARCHAR(36)） */
    @Column(name = "product_uuid", nullable = false, unique = true, length = 36)
    private String productUuid;

    /** 商品名 */
    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    /** 単価 */
    @Column(name = "price", nullable = false)
    private Integer price;

    /** 多商品 → 1カテゴリ。別集約なので参照のみ（cascade しない）。 */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategoryEntity category;

    /**
     * 1商品 → 1在庫。集約内部なので {@code cascade = ALL} で一括保存・削除する。
     * 外部キーは在庫側にあるため所有側は {@link ProductStockEntity}（ここは {@code mappedBy}）。
     * 素の setter は生成せず、両方向を同時に設定する {@link #assignStock} を使う。
     */
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Setter(AccessLevel.NONE)
    private ProductStockEntity stock;

    /**
     * 在庫を紐づける。双方向 OneToOne の所有側（{@code product_stock.product_id}）も
     * 同時に設定することで、cascade 保存時に外部キーが正しく書き込まれる。
     *
     * @param stock 紐づける在庫エンティティ
     */
    public void assignStock(ProductStockEntity stock) {
        this.stock = stock;
        if (stock != null) {
            stock.setProduct(this);
        }
    }
}