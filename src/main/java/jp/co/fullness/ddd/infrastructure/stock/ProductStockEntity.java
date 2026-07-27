package jp.co.fullness.ddd.infrastructure.stock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jp.co.fullness.ddd.infrastructure.product.ProductEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * product_stock テーブルにマッピングする JPA エンティティ（永続化モデル）。
 *
 * <p>1商品1在庫。外部キー {@code product_id} は<b>このテーブル側</b>にあるため、
 * OneToOne の<b>所有側</b>はこのエンティティになる（Product 側は {@code mappedBy}）。
 * ドメインへの変換は {@code StockEntityMapper}（MapStruct）が担う。</p>
 */
@Getter
@Setter
@Entity
@Table(name = "product_stock")
public class ProductStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 識別Id（stock_uuid, VARCHAR(36)） */
    @Column(name = "stock_uuid", nullable = false, unique = true, length = 36)
    private String stockUuid;

    /** 在庫数 */
    @Column(name = "stock", nullable = false)
    private Integer stock;

    /** 所有側：product_stock.product_id（1商品1在庫を UNIQUE で担保） */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private ProductEntity product;
}