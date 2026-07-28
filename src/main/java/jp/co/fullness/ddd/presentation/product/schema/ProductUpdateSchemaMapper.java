package jp.co.fullness.ddd.presentation.product.schema;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;

/**
 * プレゼンテーション層の {@link ProductUpdateSchema} を、アプリケーション層の
 * {@link ProductDTO} へ変換する腐敗防止層（ACL）アダプタ。
 *
 * <p>商品IDはURIのパスで受け取るため、ここでは設定せず（{@code ignore}）、
 * コントローラでパスの {@code {id}} を補完する。カテゴリは変更対象外のため設定しない（null）。
 * 在庫はネストDTOとして在庫数のみを組み立てる（在庫IDは更新時に不要）。</p>
 */
@Mapper(
    componentModel = "spring",
    imports = { StockDTO.class }
)
public interface ProductUpdateSchemaMapper {

    /**
     * 商品変更スキーマを {@link ProductDTO} に変換する。
     *
     * @param schema 商品変更リクエストスキーマ
     * @return アプリケーション層の商品DTO（id は未設定＝コントローラで補完、category は null、stock はネスト）
     */
    // id はパスから補完するため無視。category は変更対象外のため無視。name / price は同名で自動マッピング。
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "stock",
             expression = "java(new StockDTO(null, schema.stockQuantity()))")
    ProductDTO toDto(ProductUpdateSchema schema);
}