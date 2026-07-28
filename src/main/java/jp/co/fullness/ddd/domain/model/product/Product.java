package jp.co.fullness.ddd.domain.model.product;

import java.util.Objects;

import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.domain.model.category.Category;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * 商品を表すエンティティ(集約ルート)。
 * - 同一性: ProductId(値オブジェクト)
 * - 属性: ProductName / ProductPrice(いずれも不変・自己検証VO)
 * - 集約: Category / Stock(Entity)を保持する
 */
public final class Product {

    /** 商品の同一性(不変) */
    private final ProductId productId;

    /** 商品名(VO) */
    private ProductName name;

    /** 商品単価(VO) */
    private ProductPrice price;

    /** 商品カテゴリ(Entity) */
    private Category category;

    /** 商品在庫(Entity) */
    private Stock stock;

    /**
     * コンストラクタ(不変条件の検証を集約)。
     *
     * @param id       商品Id
     * @param name     商品名
     * @param price    商品単価
     * @param category 商品カテゴリ(stockと同時にnull/非nullであること)
     * @param stock    商品在庫(categoryと同時にnull/非nullであること)
     */
    private Product(
            ProductId id, ProductName name,
            ProductPrice price, Category category, Stock stock) {
        if (id == null)    throw new DomainException("商品IDは必須です。");
        if (name == null)  throw new DomainException("商品名は必須です。");
        if (price == null) throw new DomainException("商品単価は必須です。");

        // 完全性チェック：Category と Stock は「両方null」か「両方非null」だけを許可
        boolean onlyOneProvided = (category == null) ^ (stock == null);
        if (onlyOneProvided) {
            throw new DomainException(
                "Productの再構築に失敗：CategoryとStockは両方指定するか、両方nullにしてください。");
        }

        this.productId = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    /**
     * 新規作成。
     *
     * @param name     商品名
     * @param price    商品単価
     * @param category 商品カテゴリ
     * @param quantity 初期在庫数
     * @return 商品エンティティ
     */
    public static Product createNew(
            ProductName name, ProductPrice price, Category category, StockQuantity quantity) {
        return new Product(ProductId.createNew(), name, price, category, Stock.createNew(quantity));
    }

    /**
     * 生成: 識別子を指定して再構築(リストア)。
     * 既存データの復元やテストの明示的なID指定に利用する。
     */
    public static Product restore(
            ProductId id, ProductName name,
            ProductPrice price, Category category, Stock stock) {
        return new Product(id, name, price, category, stock);
    }

    /**
     * 骨格だけで再構築するファクトリ(Assemblerで後から合成)。
     */
    public static Product restoreSkeleton(ProductId id, ProductName name, ProductPrice price) {
        return new Product(id, name, price, /*category*/ null, /*stock*/ null);
    }

    /**
     * カテゴリを設定する。
     *
     * @param category 商品カテゴリ
     */
    public void attachCategory(Category category) {
        if (category == null) throw new DomainException("商品カテゴリは必須です。");
        this.category = category;
    }

    /**
     * 在庫を設定する。
     *
     * @param stock 商品在庫
     */
    public void attachStock(Stock stock) {
        if (stock == null) throw new DomainException("在庫は必須です。");
        this.stock = stock;
    }

    /**
     * 商品名を変更する。
     *
     * <p>「商品を変更する」ユースケースで、既存商品の名称を新しい値に差し替えます。
     * <br>商品名の妥当性(必須・最大文字数・前後トリムなど)は値オブジェクト
     * {@link ProductName} が生成時に自己検証するため、本メソッドは非nullのみを保証します。
     * <br>なお「同名商品の重複不可(自分自身を除く)」はドメイン単体では判定できないため、
     * ユースケース層で {@code ProductRepository#findByName} を用いて検証します。
     *
     * @param newName 新しい商品名(値オブジェクト、非null)
     * @throws DomainException {@code newName} が {@code null} の場合
     */
    public void rename(ProductName newName) {
        if (newName == null) throw new DomainException("商品名は必須です。");
        this.name = newName;
    }

    /**
     * 単価を変更する。
     *
     * <p>「商品を変更する」ユースケースで、既存商品の単価を新しい値に差し替えます。
     * <br>単価の範囲(50〜10,000)などの妥当性は値オブジェクト {@link ProductPrice} が
     * 生成時に自己検証するため、本メソッドは非nullのみを保証します。
     *
     * @param newPrice 新しい商品単価(値オブジェクト、非null)
     * @throws DomainException {@code newPrice} が {@code null} の場合
     */
    public void reprice(ProductPrice newPrice) {
        if (newPrice == null) throw new DomainException("商品単価は必須です。");
        this.price = newPrice;
    }

    /**
     * 在庫数を変更する。
     *
     * <p>「商品を変更する」ユースケースで、集約が保持する在庫({@link Stock})の数量を変更します。
     * <br>数量の範囲(0〜100)などの妥当性は値オブジェクト {@link StockQuantity} が生成時に
     * 自己検証します。既存の {@link Stock} の同一性(StockId)は保持したまま数量だけを書き換えるため、
     * 更新時に在庫行のIDは変わりません。
     *
     * @param newQty 新しい在庫数(値オブジェクト、非null)
     * @throws DomainException 在庫が未設定(未 {@code attachStock})の場合
     */
    public void changeStock(StockQuantity newQty) {
        ensureStockAttached();
        this.stock.changeQuantity(newQty);
    }

    private void ensureStockAttached() {
        if (this.stock == null) {
            throw new DomainException("在庫が未設定です。先に attachStock(...) を呼び出してください。");
        }
    }

    /** @return 商品Id */
    public ProductId getProductId() { return productId; }

    /** @return 商品名 */
    public ProductName getName() { return name; }

    /** @return 商品単価 */
    public ProductPrice getPrice() { return price; }

    /** @return 商品カテゴリ */
    public Category getCategory() { return category; }

    /** @return 現在の在庫数 */
    public StockQuantity currentStock() {
        ensureStockAttached();
        return stock.getQuantity();
    }

    /** @return 商品在庫(Entity) */
    public Stock getStock() { return stock; }

    /**
     * 同一性(ProductId)による等価判定。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    /**
     * インスタンスの内容(デバッグ用)。
     */
    @Override
    public String toString() {
        return "Product{id=" + productId + ", name=" + name + ", price=" + price
                + ", category=" + category + ", stock=" + stock + "}";
    }
}