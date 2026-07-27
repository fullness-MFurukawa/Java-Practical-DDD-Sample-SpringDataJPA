package jp.co.fullness.ddd.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品情報を表すDTO（Data Transfer Object）。
 *
 * <p>本クラスは {@code Product} エンティティに対応するデータ構造であり、
 * アプリケーション層とプレゼンテーション層の間でデータを受け渡すために使用する。</p>
 *
 * <p>DTOはドメイン層の内部構造（エンティティ、値オブジェクト）を隠蔽し、
 * API仕様やUIの要求に合わせた構造を提供する。</p>
 */
@Schema(name = "Product", description = "商品DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    /** 商品ID(UUID形式)。エンティティの識別子に対応。 */
    @Schema(description = "商品ID(UUID)", example = "83fbc81d-2498-4da6-b8c2-54878d3b67ff")
    private String id;

    /** 商品名。{@code ProductName} 値オブジェクトに対応。 */
    @Schema(description = "商品名", example = "蛍光ペン(赤)")
    private String name;

    /** 商品単価（円）。{@code ProductPrice} 値オブジェクトに対応。 */
    @Schema(description = "商品単価（円）", example = "130")
    private Integer price;

    /** 商品カテゴリ。{@code CategoryDTO} をネストして表現。 */
    @Schema(description = "商品カテゴリ情報")
    private CategoryDTO category;

    /** 商品在庫情報。{@code StockDTO} をネストして表現。 */
    @Schema(description = "商品在庫情報")
    private StockDTO stock;
}