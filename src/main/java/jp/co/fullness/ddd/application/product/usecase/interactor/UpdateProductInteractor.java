package jp.co.fullness.ddd.application.product.usecase.interactor;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jp.co.fullness.ddd.application.annotation.UseCase;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.application.mapper.ProductDTOAssembler;
import jp.co.fullness.ddd.application.product.service.ProductService;
import jp.co.fullness.ddd.application.product.usecase.UpdateProductUsecase;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductPrice;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;
import lombok.RequiredArgsConstructor;

/**
 * <b>ユースケース実装(Interactor)</b>: {@link UpdateProductUsecase}。
 *
 * <p>Service と Assembler を組み合わせ、DTO とドメインの橋渡しを行う。
 * 読み取り系は {@code @Transactional(readOnly = true)}、書き込み系はメソッドで
 * {@code @Transactional} を付与してトランザクション境界を明確化する。</p>
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpdateProductInteractor implements UpdateProductUsecase {

    /** 商品サービス */
    private final ProductService productService;

    /** DomainEntity と DTO の相互変換・組み立て */
    private final ProductDTOAssembler assembler;

    @Override
    public ProductDTO getProduct(String productId) {
        if (!StringUtils.hasText(productId)) {
            throw new InvalidInputException("商品IDは必須です。");
        }
        // ID で取得（存在必須）し、DTO へ変換して返す
        Product product = productService.getProductById(ProductId.fromString(productId));
        return assembler.assembleDto(product);
    }

    /**
     * {@inheritDoc}
     *
     * <h4>処理フロー</h4>
     * <ol>
     *   <li>入力（DTO・商品ID・在庫）をガード。</li>
     *   <li>変更後の値オブジェクト（名称・単価・在庫数）を生成し入力を検証。</li>
     *   <li>変更対象の集約を取得（存在必須）。カテゴリ・在庫を識別子ごと保持した状態で取得する。</li>
     *   <li>同名重複チェック（自分自身を除く。他商品が使用中なら {@code ExistsException}）。</li>
     *   <li>取得した集約にドメインメソッドで変更を適用（rename / reprice / changeStock）。</li>
     *   <li>変更を永続化し、最新状態を再取得してDTOに変換して返す。</li>
     * </ol>
     *
     * <p>カテゴリは変更対象外のため、取得した集約のカテゴリをそのまま保持する。</p>
     */
    @Transactional
    @Override
    public ProductDTO updateProduct(ProductDTO product) {
        // 1. 入力ガード
        if (product == null) {
            throw new InvalidInputException("ProductDTOがnullです。");
        }
        if (!StringUtils.hasText(product.getId())) {
            throw new InvalidInputException("商品IDは必須です。");
        }
        if (product.getStock() == null) {
            throw new InvalidInputException("在庫（StockDTO）は必須です。");
        }

        // 2. 変更後の値オブジェクトを生成（ここで名称・単価・在庫数が検証される）
        ProductName  newName  = ProductName.of(product.getName());
        ProductPrice newPrice = ProductPrice.of(product.getPrice());
        StockQuantity newQty  = StockQuantity.of(product.getStock().getQuantity());

        // 3. 変更対象の集約を取得（存在必須。カテゴリ・在庫を識別子ごと保持している）
        Product current = productService.getProductById(ProductId.fromString(product.getId()));

        // 4. 同名重複チェック（自分自身を除く。他商品が同名を使用中なら ExistsException）
        productService.existsProductExcept(newName, current.getProductId());

        // 5. 取得した集約に変更を適用（在庫は同一性 stock_uuid を保ったまま数量のみ変更）
        current.rename(newName);
        current.reprice(newPrice);
        current.changeStock(newQty);

        // 6. 永続化し、最新状態を再取得して返す
        productService.updateProduct(current);
        Product updated = productService.getProductById(current.getProductId());
        return assembler.assembleDto(updated);
    }
}
