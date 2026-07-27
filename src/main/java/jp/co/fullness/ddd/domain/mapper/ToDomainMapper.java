package jp.co.fullness.ddd.domain.mapper;

/**
 * DTOや外部データ構造からドメインエンティティを再構築するためのAdapterインターフェイス。
 *
 * <p>このインターフェイスはGoFの「Adapterパターン」をDDDに応用したものであり、
 * ドメイン層を外部依存(DTO、永続化レコード〈各ORMのRecord/Entity〉、APIレスポンスなど)から隔離する
 * 「腐敗防止層(Anti-Corruption Layer, ACL)」としての役割を持ちます。
 *
 * <p>外部で定義されたデータ構造をそのままドメインに持ち込まず、
 * <b>「ドメインモデルの語彙に変換して再構築する」</b>ことを目的としています。
 *
 * <p><b>利用例:</b>
 * <pre>{@code
 * Category entity = mapper.toDomain(record); // 永続化レコード → Domain Entity
 * }</pre>
 *
 * @param <DTO>    外部データ型(例：DTO, 永続化レコード, Responseなど)
 * @param <DOMAIN> ドメインエンティティ型
 */
public interface ToDomainMapper<DTO, DOMAIN> {

    /**
     * 外部データ構造(DTO、永続化レコードなど)からドメインエンティティを再構築します。
     * <p>変換過程では、必須項目の検証や値オブジェクトの生成などを通じて、
     * ドメインモデルの一貫性を保証します。
     *
     * @param input 変換する外部データ(DTO、永続化レコードなど)
     * @return 検証済みのドメインエンティティ
     */
    DOMAIN toDomain(DTO input);
}
