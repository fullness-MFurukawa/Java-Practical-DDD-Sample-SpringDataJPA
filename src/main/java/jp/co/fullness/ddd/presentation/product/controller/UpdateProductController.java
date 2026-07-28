package jp.co.fullness.ddd.presentation.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.product.usecase.UpdateProductUsecase;
import jp.co.fullness.ddd.presentation.product.schema.ProductUpdateSchema;
import jp.co.fullness.ddd.presentation.product.schema.ProductUpdateSchemaMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * 【プレゼンテーション層：UpdateProductController】
 * ============================================================================
 * 🎯 役割
 * - ユースケース「商品を変更する」を実現するエンドポイント群を提供する。
 * - クライアントからの HTTP リクエストを受け取り、アプリケーション層の Usecase に委譲する。
 * - 入出力データの整形（Schema ⇔ DTO）および入力値の基本的な検証を担う。
 *
 * 🧩 設計方針
 * - Controller は「変換と委譲」に徹し、ビジネスロジックは一切持たない。
 * - 商品IDはURIのパス {id} で受け取り、ボディには含めない（URIがリソースを一意に指す）。
 * - 変更対象は名称・単価・在庫数のみ。カテゴリは変更対象外。
 * - トランザクション境界は Usecase 層（UpdateProductUsecase）にある。
 *
 * 📦 主なエンドポイント
 * - GET /api/products/{id} : 変更対象の取得（編集画面の初期表示用）
 * - PUT /api/products/{id} : 商品変更
 *
 * 🛡️ 例外ハンドリング（ApiExceptionHandler にて統一処理）
 * - 該当なし : NotFoundException → 404
 * - 重複     : ExistsException → 409（他商品が同名を使用中）
 * - 入力不備 : InvalidInputException / DomainException → 400
 * ============================================================================
 */
@Tag(name = "UpdateProducts", description = "商品変更")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class UpdateProductController {

    /** ユースケース:[商品を変更する] */
    private final UpdateProductUsecase usecase;

    /** 商品変更スキーマ → ProductDTO の変換 */
    private final ProductUpdateSchemaMapper mapper;

    /**
     * 変更対象の商品を取得する（編集画面の初期表示用）。
     *
     * @param productId 商品Id（UUID）
     * @return 商品DTO（カテゴリ・在庫を含む）
     */
    @Operation(summary = "商品取得（変更用）",
        description = "商品Id(UUID)を指定して、変更対象の商品情報を取得します。")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取得成功",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "該当商品が存在しない")
    })
    @GetMapping(path = "/{id}", produces = "application/json")
    public ProductDTO getProduct(
        @Parameter(description = "商品Id(UUID)", example = "83fbc81d-2498-4da6-b8c2-54878d3b67ff", required = true)
        @PathVariable("id") String productId
    ) {
        return usecase.getProduct(productId);
    }

    /**
     * 商品を変更する。
     * <p>取得・変更適用・重複チェック（自分自身を除く）・更新・再取得は
     * Usecase（updateProduct）内で1トランザクションとして完結する。</p>
     *
     * @param productId 変更対象の商品Id（URIパス）
     * @param req       商品変更リクエスト
     * @return 変更後の商品DTO（200 OK）
     */
    @Operation(
        summary = "商品変更",
        description = "商品の名称・単価・在庫数を変更します。成功時は200を返します。",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductUpdateSchema.class)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "変更成功",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "入力不正"),
        @ApiResponse(responseCode = "404", description = "商品が存在しない"),
        @ApiResponse(responseCode = "409", description = "同名商品が既に存在する（他商品が使用中）"),
        @ApiResponse(responseCode = "500", description = "サーバ内部エラー")
    })
    @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProductDTO> update(
        @Parameter(description = "商品Id(UUID)", example = "83fbc81d-2498-4da6-b8c2-54878d3b67ff", required = true)
        @PathVariable("id") String productId,
        @Valid @RequestBody ProductUpdateSchema req
    ) {
        // ProductUpdateSchema → ProductDTO（id はパスから補完する）
        ProductDTO dto = mapper.toDto(req);
        dto.setId(productId);
        // 変更（取得・重複チェック・更新・再取得は updateProduct 内で完結する）
        ProductDTO updated = usecase.updateProduct(dto);
        return ResponseEntity.ok(updated);
    }
}