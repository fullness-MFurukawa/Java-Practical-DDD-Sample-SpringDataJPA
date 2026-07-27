package jp.co.fullness.ddd.application.category.service;

import java.util.List;

import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.category.CategoryId;

/**
 * 商品カテゴリに関するアプリケーションサービスインターフェイス。
 *
 * <p>Service層は、ユースケース（UseCase）実現のためのドメイン操作をカプセル化し、
 * 複数のユースケースから共通利用される。</p>
 *
 * <p>この層ではトランザクションは管理しない（境界はUseCase側）。
 * また、ドメインロジックは持たず、Repositoryを介してデータ取得を行う。</p>
 */
public interface CategoryService {

    /**
     * すべての商品カテゴリを取得する。
     *
     * @return 商品カテゴリリスト
     */
    List<Category> getCategories();

    /**
     * 商品カテゴリIdで商品カテゴリを取得する。
     *
     * @param categoryId 商品カテゴリId(VO)
     * @return 商品カテゴリ
     * @throws NotFoundException 指定された商品カテゴリIdに該当する商品カテゴリが存在しない場合
     */
    Category getCategoryById(CategoryId categoryId);
}