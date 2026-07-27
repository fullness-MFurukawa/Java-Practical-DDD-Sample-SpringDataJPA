package jp.co.fullness.ddd.presentation.product.schema;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.dto.StockDTO;

/**
 * プレゼンテーション層の {@link ProductCreateSchema} を、アプリケーション層の
 * {@link ProductDTO} へ変換する腐敗防止層（ACL）アダプタ。
 *
 * <p>新規登録なので商品IDは設定しない。カテゴリ・在庫はネストDTOとして組み立てる。
 * カテゴリ名は登録処理（ユースケースの addProduct）側でDBの正しい値に解決・上書きされるため、
 * ここでは null とする。</p>
 */
@Mapper(
    componentModel = "spring",
    imports = { CategoryDTO.class, StockDTO.class }
)
public interface ProductCreateSchemaMapper {

    /**
     * 商品登録スキーマを {@link ProductDTO} に変換する。
     *
     * @param schema 商品登録リクエストスキーマ
     * @return アプリケーション層の商品DTO（id は未設定、category/stock はネスト）
     */
    // id は新規採番のため無視。name / price は同名で自動マッピングされる。
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category",
             expression = "java(new CategoryDTO(schema.categoryId(), null))")
    @Mapping(target = "stock",
             expression = "java(new StockDTO(null, schema.stockQuantity()))")
    ProductDTO toDto(ProductCreateSchema schema);
}
