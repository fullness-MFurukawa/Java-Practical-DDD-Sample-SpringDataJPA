package jp.co.fullness.ddd.domain.model.category;

import java.util.List;
import java.util.Optional;

/**
 * ドメインリポジトリ：{@code CategoryRepository}
 *
 * <p>このリポジトリは、ドメインエンティティ {@link Category} の
 * 永続化と再構築を担当するインターフェイスです。
 * <br>DDDにおけるリポジトリの役割は「ドメイン層からインフラ層を隠蔽し、
 * 集約を永続化するための抽象契約(ポート)」を定義することです。
 *
 * <p>【ユビキタス言語】
 * <ul>
 *   <li>「カテゴリ(Category)」：商品を分類するためのグループ</li>
 *   <li>「カテゴリID(CategoryId)」：カテゴリを一意に識別する値オブジェクト</li>
 * </ul>
 *
 * <p>【設計上の意図】
 * <ul>
 *   <li>ドメイン層は永続化の詳細(SQL, ORMなど)を一切知らない</li>
 *   <li>アプリケーション層はこのインターフェイスを介してドメインを取得する</li>
 * </ul>
 *
 * <p>【責務】
 * <ul>
 *   <li>カテゴリをIDで検索(単一取得)</li>
 *   <li>すべてのカテゴリを一覧で取得</li>
 * </ul>
 *
 * <p>例：
 * <pre>{@code
 * CategoryRepository repo = ...;
 * Category category = repo.findById(CategoryId.fromString("..."))
 *                         .orElseThrow(() -> new NotFoundException("カテゴリが存在しません"));
 * List<Category> all = repo.findAll();
 * }</pre>
 */
public interface CategoryRepository {

    /**
     * 商品カテゴリIDを指定してカテゴリを取得する。
     *
     * <p>この操作はドメインの「カテゴリを参照する」ユースケースで利用されます。
     * <br>主キー検索に対応し、存在しない場合は {@code Optional.empty()} を返します。
     *
     * @param categoryId 商品カテゴリID(値オブジェクト)
     * @return 存在する場合は Category を保持する {@code Optional}、存在しない場合は {@code Optional.empty()}
     */
    Optional<Category> findById(CategoryId categoryId);

    /**
     * 登録されているすべての商品カテゴリを取得する。
     *
     * <p>主に「商品登録画面のプルダウン」や「カテゴリ一覧表示」などの
     * 参照ユースケースで利用されます。
     *
     * @return すべての {@link Category} エンティティのリスト(結果が空の場合は空のリスト)
     */
    List<Category> findAll();
}
