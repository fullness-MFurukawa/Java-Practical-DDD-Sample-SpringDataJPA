package jp.co.fullness.ddd.presentation.product.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 📦 商品変更リクエスト受信用スキーマ（入力DTO）。
 *
 * <p>プレゼンテーション層で使用する入力専用のデータ転送オブジェクト。
 * 「商品を変更する」ユースケースで、変更対象の名称・単価・在庫数を受け取り、
 * Jakarta Bean Validation で入力値の妥当性を境界で検証する。
 * ドメイン層の知識（値オブジェクト等）は露出しない。</p>
 *
 * <p>商品IDはURIのパス {@code /api/products/{id}} で受け取るため、本スキーマには含めない。
 * カテゴリは変更対象外のため受け取らない。</p>
 *
 * <p>Controller で {@code @Valid} により自動検証され、バリデーションエラーは
 * {@link jp.co.fullness.ddd.presentation.exception.ApiExceptionHandler} が処理する。
 * 受け取った値は {@link ProductUpdateSchemaMapper} でアプリケーション層の
 * {@code ProductDTO} に変換される。</p>
 */
@Schema(name = "ProductUpdateSchema", description = "商品変更リクエスト")
public record ProductUpdateSchema(

    @Schema(description = "商品名", example = "筆ペン（極細）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品名は必須です")
    @Size(max = 30, message = "商品名は30文字以内で指定してください")
    String name,

    @Schema(description = "商品単価（円）: 50〜10000", example = "350", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "単価は必須です")
    @Min(value = 50, message = "単価は50以上で指定してください")
    @Max(value = 10000, message = "単価は10000以下で指定してください")
    Integer price,

    @Schema(description = "在庫数: 0〜100", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "在庫数は必須です")
    @Min(value = 0, message = "在庫数は0以上で指定してください")
    @Max(value = 100, message = "在庫数は100以下で指定してください")
    Integer stockQuantity
) {}