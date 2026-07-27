package jp.co.fullness.ddd.application.exception;

/**
 * {@code InvalidInputException} は、アプリケーション層で受け取った
 * 入力データが不正であることを表す実行時例外です。
 *
 * <p>アプリケーション層の入力変換やユースケースで、ドメインのバリデーションに
 * 到達する前に弾くべき入力不備（必須項目の欠落など）を表します。
 * ※「UUID形式が不正」「価格が負数」などVOの不変条件に関わる検証は
 * ドメイン層のVOが {@code DomainException} として弾くため、本例外の対象外です
 *（VOの {@code DomainException} をアプリ層で本例外に翻訳する方針を採る場合を除く）。
 *
 * <p>層の責務：
 * <ul>
 *   <li>発生層：アプリケーション層（入力変換部、Usecase）</li>
 *   <li>捕捉層：ControllerまたはExceptionHandler（HTTP 400 Bad Requestに変換）</li>
 * </ul>
 */
public class InvalidInputException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     * @param message エラーメッセージ
     * @param cause 原因となった例外（任意）
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}