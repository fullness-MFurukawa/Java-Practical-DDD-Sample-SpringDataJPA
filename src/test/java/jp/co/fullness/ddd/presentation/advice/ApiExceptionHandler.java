package jp.co.fullness.ddd.presentation.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import jp.co.fullness.ddd.application.exception.ExistsException;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.application.exception.NotFoundException;
import jp.co.fullness.ddd.domain.exception.DomainException;
import jp.co.fullness.ddd.infrastructure.exception.InternalException;

/**
 * 🎯 {@code @RestControllerAdvice} による全体例外ハンドラ。
 *
 * <p>各層でスローされた例外を一括で捕捉し、適切なHTTPステータスへ変換する。
 * Spring MVC 標準の例外（ボディ不正・必須パラメータ欠落・メソッド不許可など）は
 * {@link ResponseEntityExceptionHandler} を継承して基底クラスに委ね、
 * ドメイン／アプリ／インフラ層の独自例外だけを本クラスで個別に処理する。</p>
 *
 * <p>※ これは AOP の AfterThrowing アドバイスではなく、Spring MVC の
 * ExceptionHandlerExceptionResolver によって呼び出される仕組みである。</p>
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** @RequestParam / @PathVariable などのメソッドパラメータ検証エラー → 400 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(msg);
    }

    /** アプリ層の入力不正 → 400 */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInput(InvalidInputException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /** ドメインルール違反（VOの不変条件違反など） → 400 */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomain(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /** リソース未存在 → 404 */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /** リソース重複 → 409 */
    @ExceptionHandler(ExistsException.class)
    public ResponseEntity<String> handleExists(ExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /** インフラ層の内部障害 → 500（詳細はログのみ。クライアントには汎用メッセージ） */
    @ExceptionHandler(InternalException.class)
    public ResponseEntity<String> handleInternal(InternalException ex) {
        log.error("内部エラーが発生しました。", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("サーバ内部エラーが発生しました。");
    }

    /** 想定外の例外（独自例外・基底クラスのいずれにも該当しないもの） → 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(Exception ex) {
        log.error("想定外のエラーが発生しました。", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("サーバ内部エラーが発生しました。");
    }
}