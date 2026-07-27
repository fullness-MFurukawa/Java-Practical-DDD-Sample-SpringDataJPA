package jp.co.fullness.ddd.application.category.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryId;
import jp.co.fullness.ddd.domain.model.category.CategoryName;
import jp.co.fullness.ddd.domain.model.category.CategoryRepository;

/**
 * {@link CategoryServiceImpl} の単体テスト（DB 不要 / Mockito）。
 *
 * <p>{@link CategoryRepository} をモック化し、委譲と「未存在 → NotFoundException」変換だけを検証する。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl: カテゴリ取得サービス")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    @DisplayName("getCategories(): リポジトリの findAll をそのまま返す")
    void getCategories() {
        Category c = Category.createNew(CategoryName.of("文房具"));
        when(categoryRepository.findAll()).thenReturn(List.of(c));

        List<Category> result = service.getCategories();

        assertEquals(1, result.size());
        assertSame(c, result.get(0));
    }

    @Test
    @DisplayName("getCategoryById(): 見つかれば Category を返す")
    void getCategoryById_found() {
        Category c = Category.createNew(CategoryName.of("文房具"));
        CategoryId id = c.getCategoryId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(c));

        assertSame(c, service.getCategoryById(id));
    }

    @Test
    @DisplayName("getCategoryById(): 見つからなければ NotFoundException")
    void getCategoryById_notFound() {
        CategoryId id = CategoryId.createNew();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getCategoryById(id));
    }
}
