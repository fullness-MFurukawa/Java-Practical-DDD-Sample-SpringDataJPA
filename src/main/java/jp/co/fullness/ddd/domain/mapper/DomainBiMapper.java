package jp.co.fullness.ddd.domain.mapper;

/**
 * ドメインエンティティとDTOを相互に変換するためのMapperインターフェイス。
 *
 * <p>このインターフェイスは、アプリケーション層とドメイン層の間に明確な変換責務を定義し、
 * 双方向のデータマッピングを統一的に扱うための契約を表します。
 *
 * <p>DDDにおいて、DTOはユースケース境界を越えてデータを受け渡すための構造体であり、
 * Domainはビジネスルールと不変条件を保持する純粋なドメインモデルです。
 * <br>このMapperを通じて、DTO ⇔ Domain の変換ロジックを一元化することで、
 * コードの重複を防ぎ、層間依存を最小化します。
 *
 * <p><b>利用例:</b>
 * <pre>{@code
 * ProductDTO dto = mapper.fromDomain(product);
 * Product entity = mapper.toDomain(dto);
 * }</pre>
 *
 * @param <DTO>    データ転送用のオブジェクト型
 * @param <DOMAIN> ドメインエンティティ型
 */
public interface DomainBiMapper<DTO, DOMAIN> {

    /**
     * DTOなどの外部データ構造からドメインエンティティを再構築します。
     * <p>外部から受け取ったデータをドメインモデルへ変換し、
     * 不変条件の検証や正規化を行う責務を担います。
     *
     * @param input 変換対象のDTO(アプリケーション層で利用)
     * @return 再構築されたドメインエンティティ
     */
    DOMAIN toDomain(DTO input);

    /**
     * ドメインエンティティをDTOなどの外部データ構造に変換します。
     * <p>ドメイン層から取得した情報をアプリケーション層やインフラストラクチャ層などで
     * 利用可能な形式に変換します。
     *
     * @param domain 変換対象のドメインエンティティ
     * @return 変換結果のDTO(ユースケース出力やAPIレスポンスなどに利用)
     */
    DTO fromDomain(DOMAIN domain);
}
