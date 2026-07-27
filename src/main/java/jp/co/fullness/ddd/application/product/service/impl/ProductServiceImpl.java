package jp.co.fullness.ddd.application.product.service.impl;

import org.springframework.stereotype.Service;

import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.application.product.service.ProductService;
import jp.co.fullness.ddd.domain.model.product.Product;
import jp.co.fullness.ddd.domain.model.product.ProductId;
import jp.co.fullness.ddd.domain.model.product.ProductName;
import jp.co.fullness.ddd.domain.model.product.ProductRepository;

import lombok.RequiredArgsConstructor;

/**
 * {@link ProductService} の実装クラス。
 *
 * <p>Repository を介してドメインモデルを操作し、アプリケーション層の例外をスローする。
 * ユースケースから呼び出され、ドメイン層を抽象化したファサードとして振る舞う。
 * トランザクションはユースケース層で管理する。</p>
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public void existsProduct(ProductName productName) {
        if (repository.existsByName(productName)) {
            throw new ExistsException(
                    String.format("商品名:[%s]は既に登録済みです。", productName.value()));
        }
    }

    @Override
    public Product getProductById(ProductId productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("商品Id:[%s]の商品は存在しません。", productId.value())));
    }

    @Override
    public Product getProductByName(ProductName productName) {
        return repository.findByName(productName)
                .orElseThrow(() -> new NotFoundException(
                        String.format("商品名:[%s]の商品は存在しません。", productName.value())));
    }

    @Override
    public void addProduct(Product product) {
        repository.create(product);
    }
}