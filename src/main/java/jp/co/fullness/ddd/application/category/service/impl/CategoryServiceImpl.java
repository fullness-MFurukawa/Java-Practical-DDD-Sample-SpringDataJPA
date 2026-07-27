package jp.co.fullness.ddd.application.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.fullness.ddd.application.category.service.CategoryService;
import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryId;
import jp.co.fullness.ddd.domain.model.category.CategoryRepository;

import lombok.RequiredArgsConstructor;

/**
 * {@link CategoryService} の実装クラス。
 *
 * <p>Repository を介して永続層からドメインエンティティを取得し、
 * 必要に応じてアプリケーション例外に変換する。ドメインの整合性検証や
 * トランザクションは行わない（ユースケース層で管理される）。</p>
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(CategoryId categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("商品カテゴリId:[%s]の商品カテゴリは存在しません。", categoryId.value())));
    }
}