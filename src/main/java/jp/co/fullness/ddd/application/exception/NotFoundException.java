package jp.co.fullness.ddd.application.exception;

/**
 * {@code NotFoundException} は、指定されたデータが存在しないことを
 * 表すアプリケーション層の実行時例外です。
 *
 * <p>Repository経由の検索で該当データが見つからなかった場合にスローされます。
 * この例外は技術的な「null参照」や「SQL例外」と異なり、
 * ビジネス上の「リソースが存在しない」状態を表現します。
 *
 * <p>例：
 * <pre>{@code
 * return repository.findById(id)
 *     .orElseThrow(() -> new NotFoundException("商品ID[" + id + "]は存在しません。"));
 * }</pre>
 *
 * <p>層の責務：
 * <ul>
 *   <li>発生層：アプリケーション層（Service、Repository呼び出し部）</li>
 *   <li>捕捉層：ControllerまたはExceptionHandler（HTTP 404 Not Foundに変換）</li>
 * </ul>
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     * @param cause 原因となった例外（任意）
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}