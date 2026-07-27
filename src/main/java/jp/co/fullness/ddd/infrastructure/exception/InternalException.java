package jp.co.fullness.ddd.infrastructure.exception;

/**
 * システム内部で発生する「技術的な異常状態」を表す例外クラス。
 * <p>
 * 主に以下のような、アプリケーション外部からの操作では回避できない
 * 技術的エラーを通知するために使用します。
 * </p>
 *
 * <ul>
 *   <li>データベースサーバの停止・接続障害</li>
 *   <li>外部API通信の失敗やタイムアウト</li>
 *   <li>ファイル入出力エラー、設定ファイルの不整合</li>
 * </ul>
 *
 * <p>
 * これらはドメインルールの違反ではなく、インフラストラクチャ層の
 * 「技術的な問題」に分類されます。<br>
 * ドメイン層では {@code DomainException}、インフラ層では本クラスを使い分けることで、
 * 業務上のエラーと技術的エラーを明確に区別できます。
 * </p>
 *
 * <p>【使用例】</p>
 * <pre>{@code
 * try {
 *     // 各ORMのデータアクセス処理
 * } catch (RuntimeException ex) {
 *     throw new InternalException("DBアクセス中にエラーが発生しました。", ex);
 * }
 * }</pre>
 */
public class InternalException extends RuntimeException {

    /** シリアライズ用バージョンID */
    private static final long serialVersionUID = 1L;

    /**
     * 指定したメッセージで技術的例外を生成します。
     *
     * @param message エラーの内容を示すメッセージ
     */
    public InternalException(String message) {
        super(message);
    }

    /**
     * 指定したメッセージと原因例外で技術的例外を生成します。
     *
     * @param message エラーの内容を示すメッセージ
     * @param cause   原因となった例外(例: DataAccessException, PersistenceException, IOException など)
     */
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
