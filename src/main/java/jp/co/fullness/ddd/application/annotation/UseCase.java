package jp.co.fullness.ddd.application.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * ユースケース（アプリケーションサービス）であることを示すステレオタイプ・アノテーション。
 *
 * <p>{@link Component} を合成しているため、本アノテーションを付けたクラスは
 * コンポーネントスキャンで Spring の Bean として登録される（{@code @Service} 等と同じ仕組み）。
 * 「これはユースケースである」という意図をコード上で表現しつつ、DI の対象にする。</p>
 */
@Target(ElementType.TYPE)               // クラスに付与する
@Retention(RetentionPolicy.RUNTIME)     // 実行時まで保持する（メタアノテーション検出のため）
@Documented
@Component                              // Spring コンポーネントとして登録
public @interface UseCase {

    /**
     * Bean 名（省略時は Spring の既定命名）。{@link Component#value()} へのエイリアス。
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
