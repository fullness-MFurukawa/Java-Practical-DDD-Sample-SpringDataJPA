package jp.co.fullness.ddd.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品在庫情報を表すDTO。
 *
 * <p>ドメインの {@code Stock} エンティティに対応し、在庫の識別子と数量を保持する。
 * {@link ProductDTO} からネストして参照される。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {

    /** 在庫ID(UUID形式)。{@code StockId} 値オブジェクトに対応。 */
    private String id;

    /** 在庫数量。{@code StockQuantity} 値オブジェクトに対応。 */
    private Integer quantity;
}