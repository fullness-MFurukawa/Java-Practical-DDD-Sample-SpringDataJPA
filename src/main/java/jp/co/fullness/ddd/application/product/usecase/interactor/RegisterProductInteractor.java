package jp.co.fullness.ddd.application.product.usecase.interactor;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jp.co.fullness.ddd.application.annotation.UseCase;
import jp.co.fullness.ddd.application.category.service.CategoryService;
import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.dto.ProductDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.application.mapper.ProductDTOAssembler;
import jp.co.fullness.ddd.application.product.service.ProductService;
import jp.co.fullness.ddd.application.product.usecase.RegisterProductUsecase;
import jp.co.fullness.ddd.domain.model.category.CategoryId;
import jp.co.fullness.ddd.domain.model.product.Product;

import lombok.RequiredArgsConstructor;

/**
 * <b>ユースケース実装(Interactor)</b>: {@link RegisterProductUsecase}。
 *
 * <p>Service と Assembler/Mapper を組み合わせ、DTO とドメインの橋渡しを行う。
 * 読み取り系は {@code @Transactional(readOnly = true)}、書き込み系はメソッドで
 * {@code @Transactional} を付与してトランザクション境界を明確化する。</p>
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterProductInteractor implements RegisterProductUsecase {

    /** 商品サービス */
    private final ProductService productService;
    /** 商品カテゴリサービス */
    private final CategoryService categoryService;
    /** DomainEntity と DTO の相互変換・組み立て */
    private final ProductDTOAssembler assembler;

    @Override
    public List<CategoryDTO> getCategories() {
        return categoryService.getCategories().stream()
                .map(assembler::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDTO getCategoryById(String categoryId) {
        var category = categoryService.getCategoryById(CategoryId.fromString(categoryId));
        return assembler.toCategoryDto(category);
    }

    @Override
    public void existsProduct(String productName) {
        productService.existsProduct(jp.co.fullness.ddd.domain.model.product.ProductName.of(productName));
    }

    /**
     * {@inheritDoc}
     *
     * <h4>処理フロー</h4>
     * <ol>
     *   <li>カテゴリID等の入力をガード。</li>
     *   <li>カテゴリを取得（存在必須）し、DTOのカテゴリをDB由来の正しい内容で上書き。</li>
     *   <li>DTO → ドメイン集約 {@code Product} を合成（入力検証）。</li>
     *   <li>同名商品の重複チェック（存在すれば {@code ExistsException}）。</li>
     *   <li>登録し、登録結果を再取得してDTOに変換して返す。</li>
     * </ol>
     */
    @Transactional
    @Override
    public ProductDTO addProduct(ProductDTO product) {
        if (product == null) {
            throw new InvalidInputException("ProductDTOがnullです。");
        }
        if (product.getCategory() == null || !StringUtils.hasText(product.getCategory().getId())) {
            throw new InvalidInputException("商品カテゴリIDは必須です。");
        }

        // 商品カテゴリを取得し、DTOのカテゴリをDBの正で上書きする
        var category = categoryService.getCategoryById(CategoryId.fromString(product.getCategory().getId()));
        product.setCategory(assembler.toCategoryDto(category));

        // DTO → ドメイン集約 Product を合成（ここで name/price 等が検証される）
        Product toRegister = assembler.assembleDomain(product);

        // 同名商品の重複チェック（存在すれば ExistsException）
        productService.existsProduct(toRegister.getName());

        // 登録
        productService.addProduct(toRegister);

        // 登録結果（DB上の最新状態）を取得して返す
        var registered = productService.getProductByName(toRegister.getName());
        return assembler.assembleDto(registered);
    }
}