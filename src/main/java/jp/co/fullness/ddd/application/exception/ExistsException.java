package jp.co.fullness.ddd.application.exception;

/**
 * {@code ExistsException} は、アプリケーション層において
 * 「指定されたデータがすでに存在する」ことを示す実行時例外です。
 *
 * <p>主に登録処理（例：新規商品の追加、カテゴリの作成など）で、
 * 一意制約に違反するデータが検出された場合にスローされます。
 *
 * <p>この例外はビジネス上の重複状態を表すため、
 * 技術的なエラー（例：SQL例外）とは区別して扱います。
 *
 * <p>層の責務：
 * <ul>
 *   <li>発生層：アプリケーション層（Service、Usecase）</li>
 *   <li>捕捉層：ControllerまたはExceptionHandler（HTTP 409 Conflictに変換）</li>
 * </ul>
 */
public class ExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     */
    public ExistsException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     * @param cause 原因となった例外（任意）
     */
    public ExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}