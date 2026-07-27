package jp.co.fullness.ddd.domain.exception;

/**
 * ドメイン層における「不変条件(Invariant)」や「ビジネスルール」違反を表現するための例外クラス。
 *
 * <p>この例外は、アプリケーションの内部的なロジックエラーやシステム障害を示すものではなく、
 * 「ドメイン上の意味的な不整合(ルール違反)」を明示的に表現するために使用する。
 *
 * <p>【利用目的】
 * <ul>
 *   <li>値オブジェクトの生成時に不正な値が指定された場合</li>
 *   <li>エンティティの状態遷移がビジネスルールに違反する場合</li>
 *   <li>ドメインサービスでลูルール検証に失敗した場合</li>
 * </ul>
 *
 * <p>【設計上の意図】
 * <br>このクラスを導入することで、ドメイン層における
 * 「検証エラー」と「技術的例外(例：DB接続失敗)」を明確に区別できるようにする。
 * これにより、アプリケーション層やUI層ではドメインルール違反を適切にハンドリングし、
 * ユーザーに理解しやすいメッセージを返すことが可能となる。
 *
 * <p>例：
 * <pre>{@code
 * // 値オブジェクト生成時のルール違反
 * ProductName name = ProductName.fromString(" "); // DomainException: "商品名は必須です。"
 * }</pre>
 */
public class DomainException extends RuntimeException {

    /** シリアライズ用バージョンID */
    private static final long serialVersionUID = 1L;

    /**
     * ドメイン例外をメッセージ付きで生成する。
     *
     * @param message ドメインルール違反の説明メッセージ
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * ドメイン例外をメッセージと原因例外付きで生成する。
     *
     * @param message ドメインルール違反の説明メッセージ
     * @param cause   原因となった例外(任意)
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
