package jp.co.fullness.ddd.presentation.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * OpenAPI ドキュメント全体のメタ情報を定義する設定クラス。
 *
 * <p>springdoc がこの {@code @OpenAPIDefinition} を読み取り、生成される
 * OpenAPI 仕様（{@code /v3/api-docs}）のタイトル・バージョン・サーバ情報等を設定する。
 * 表示UI（Swagger UI / Scalar）に依存せず共通で使える。</p>
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "商品管理API",
        version = "v1.0",
        description = "ドメイン駆動設計実践サンプルAPIドキュメント(ORM:Pring Data JPA)"
    ),
    servers = {
        // ※ 実際の server.port に合わせること
        @Server(url = "http://localhost:8080", description = "ローカル環境")
    }
)
public class OpenApiConfig {
}
