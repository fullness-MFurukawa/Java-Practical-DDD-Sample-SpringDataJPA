package jp.co.fullness.ddd.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品カテゴリ情報を表すDTO。
 *
 * <p>ドメインの {@code Category} エンティティに対応し、
 * 商品カテゴリの識別子および名称を保持する。</p>
 *
 * <p>このDTOは他のDTO（例：{@link ProductDTO}）から参照されることが多く、
 * 集約構造のミラーとして設計されている。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    /** カテゴリID(UUID形式)。{@code CategoryId} 値オブジェクトに対応。 */
    private String id;

    /** カテゴリ名。{@code CategoryName} 値オブジェクトに対応。 */
    private String name;
}