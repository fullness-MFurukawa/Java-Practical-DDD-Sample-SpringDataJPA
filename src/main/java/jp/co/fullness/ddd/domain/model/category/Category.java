package jp.co.fullness.ddd.domain.model.category;

import jp.co.fullness.ddd.domain.exception.DomainException;

/**
 * 商品カテゴリを表すエンティティ。
 * - 同一性: CategoryId(値で等価)
 * - 属性: CategoryName(不変/自己検証済みのVO)
 */
public final class Category {

    /** 商品カテゴリId */
    private final CategoryId categoryId;

    /** 商品カテゴリ名 */
    private CategoryName name;

    /**
     * コンストラクタ。
     *
     * @param id   商品カテゴリId
     * @param name 商品カテゴリ名
     */
    private Category(CategoryId id, CategoryName name) {
        // CategoryIdがnullならドメインルール違反として例外をスロー
        if (id == null) {
            throw new DomainException("カテゴリIDは必須です。");
        }
        // CategoryNameがnullならドメインルール違反として例外をスロー
        if (name == null) {
            throw new DomainException("カテゴリ名は必須です。");
        }
        this.categoryId = id;
        this.name = name;
    }

    /**
     * 生成: 新規作成。
     *
     * @param name 商品カテゴリ名
     * @return 商品カテゴリエンティティ
     */
    public static Category createNew(CategoryName name) {
        return new Category(CategoryId.createNew(), name);
    }

    /**
     * 生成: 識別子を指定して再構築(リストア)。
     * 既存データの復元やテストの明示的なID指定に利用する。
     *
     * @param id   商品カテゴリId
     * @param name 商品カテゴリ名
     * @return 商品カテゴリエンティティ
     */
    public static Category restore(CategoryId id, CategoryName name) {
        return new Category(id, name);
    }

    /**
     * 名称を変更する。
     * - nullは許可しない(ドメインルール違反としてDomainExceptionをスロー)
     * - 値の検証は CategoryName 側に委譲されるため、
     *   不正な文字列や長さ超過もVO生成時にDomainExceptionとなる
     *
     * @param newName 新しい商品カテゴリ名
     */
    public void rename(CategoryName newName) {
        if (newName == null) {
            throw new DomainException("カテゴリ名は必須です。");
        }
        this.name = newName;
    }

    /** 商品カテゴリIdを返す。 */
    public CategoryId getCategoryId() {
        return categoryId;
    }

    /** 商品カテゴリ名を返す。 */
    public CategoryName getName() {
        return name;
    }

    /**
     * 同一性(CategoryId)による等価判定。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category other = (Category) o;
        return categoryId.equals(other.categoryId);
    }

    @Override
    public int hashCode() {
        return categoryId.hashCode();
    }

    /**
     * インスタンスの内容(デバッグ用)。
     */
    @Override
    public String toString() {
        return "Category{id=" + categoryId + ", name=" + name + "}";
    }
}