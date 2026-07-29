package jp.co.fullness.ddd.infrastructure.category;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.mapper.ToDomainMapper;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryId;
import jp.co.fullness.ddd.domain.model.category.CategoryRepository;
import jp.co.fullness.ddd.infrastructure.exception.InternalException;

/**
 * {@link CategoryRepository} の Spring Data JPA による実装。
 *
 * <p>SQL 実行は {@link CategoryJpaRepository} へ、Entity → Category の変換は
 * {@link CategoryEntityMapper} へ委譲する。読み取りのみのため合成用の Assembler は不要。</p>
 *
 * <p>JPA の例外は Spring の {@link org.springframework.dao.DataAccessException} に
 * 翻訳されるため、そこを捕捉する。</p>
 */
@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository repository;
    private final ToDomainMapper<CategoryEntity , Category> mapper;

    public CategoryRepositoryImpl(CategoryJpaRepository categoryJpaRepository,
                                  ToDomainMapper<CategoryEntity , Category> mapper) {
        this.repository = categoryJpaRepository;
        this.mapper = mapper;
    }

    /**
     * 指定された商品カテゴリIdのカテゴリを取得する。
     *
     * @param categoryId 商品カテゴリId（VO）
     * @return 存在する場合は Category を保持する Optional、存在しない場合は空の Optional
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(CategoryId categoryId) {
        if (categoryId == null) {
            throw new DomainException("商品カテゴリIdは必須です。");
        }
        try {
            return repository.findByCategoryUuid(categoryId.value())
                    .map(mapper::toDomain);
        } catch (DomainException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new InternalException("カテゴリ情報の取得中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("カテゴリ情報の取得処理中に予期しないエラーが発生しました。", ex);
        }
    }

    /**
     * すべての商品カテゴリを id 昇順で取得する。
     *
     * @return すべての商品カテゴリのリスト
     */
    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        try {
            return repository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DomainException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new InternalException("カテゴリ一覧の取得中にデータベースエラーが発生しました。", ex);
        } catch (Exception ex) {
            throw new InternalException("カテゴリ一覧の取得処理中に予期しないエラーが発生しました。", ex);
        }
    }
}