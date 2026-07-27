package jp.co.fullness.ddd.presentation.product.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.product.usecase.SearchProductByNameUsecase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * 【プレゼンテーション層：SearchProductByNameController】
 * ============================================================================
 * 🎯 役割
 * - ユースケース「商品名で検索する」を実現するエンドポイントを提供する。
 * - クライアントから商品名を受け取り、アプリケーション層の Usecase に委譲する。
 * - HTTP レイヤの入力検証（@NotBlank）を担う。
 *
 * 🧩 設計方針
 * - Controller はビジネスロジックを持たない「薄い層」。
 * - トランザクション境界は Usecase 側（SearchProductByNameUsecase）にある。
 * - ProductDTO を返し、ドメイン内部構造（Entity / ValueObject）は秘匿する。
 *
 * 📦 エンドポイント
 * - GET /api/products/search?name=XXX  : 商品名で商品を検索
 *
 * 🛡️ 例外ハンドリング（ApiExceptionHandler にて統一処理）
 * - NotFoundException → 404
 * - InvalidInputException / DomainException → 400
 * ============================================================================
 */
@Tag(name = "SearchProducts", description = "商品検索(名前で検索)")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class SearchProductByNameController {

    /** ユースケース:[商品を名前で検索する] */
    private final SearchProductByNameUsecase usecase;

    /**
     * 商品名を指定して商品情報を取得する。
     * <p>例: GET /api/products/search?name=蛍光ペン(赤)</p>
     *
     * @param name 商品名（必須・空白のみ不可）
     * @return 該当する商品のDTO
     */
    @Operation(
        summary = "商品名で検索",
        description = "商品名を指定して商品情報(ProductDTO)を取得します。"
    )
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "該当商品が存在しない場合")
    @ApiResponse(responseCode = "400", description = "入力パラメータが不正な場合")
    @GetMapping("/search")
    public ProductDTO searchByName(
        @Parameter(description = "商品名(必須・空白のみ不可)", required = true, example = "蛍光ペン(赤)")
        @RequestParam("name") @NotBlank(message = "商品名は必須です") String name) {
        return usecase.search(name);
    }
}
