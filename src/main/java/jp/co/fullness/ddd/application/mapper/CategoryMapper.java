package jp.co.fullness.ddd.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import jp.co.fullness.ddd.application.dto.CategoryDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.mapper.DomainBiMapper;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryId;
import jp.co.fullness.ddd.domain.model.category.CategoryName;

/**
 * {@link Category} エンティティと {@link CategoryDTO} の相互変換を行うアプリケーション層Mapper。
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends DomainBiMapper<CategoryDTO, Category> {

    /**
     * CategoryDTO から Category エンティティを再構築する。
     * ID未指定なら {@link Category#createNew(CategoryName)}、指定済みなら
     * {@link Category#restore(CategoryId, CategoryName)} を呼ぶ。
     *
     * @param dto CategoryDTO
     * @return Category エンティティ
     * @throws InvalidInputException DTOの必須項目が欠落している場合
     */
    @Override
    default Category toDomain(CategoryDTO dto) {
        if (dto == null) {
            throw new InvalidInputException("CategoryDTOがnullです。");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new InvalidInputException("商品カテゴリ名は必須です。");
        }
        if (!StringUtils.hasText(dto.getId())) {
            return Category.createNew(CategoryName.of(dto.getName()));
        }
        return Category.restore(CategoryId.fromString(dto.getId()), CategoryName.of(dto.getName()));
    }

    /**
     * Category エンティティを CategoryDTO に変換する。
     *
     * @param domain Category エンティティ
     * @return CategoryDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    @Override
    default CategoryDTO fromDomain(Category domain) {
        if (domain == null) {
            throw new InvalidInputException("Categoryがnullです。");
        }
        return new CategoryDTO(domain.getCategoryId().value(), domain.getName().value());
    }
}