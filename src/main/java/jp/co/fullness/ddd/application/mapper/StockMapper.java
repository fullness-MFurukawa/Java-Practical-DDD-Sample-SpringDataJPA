package jp.co.fullness.ddd.application.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import jp.co.fullness.ddd.application.dto.StockDTO;
import jp.co.fullness.ddd.application.exception.InvalidInputException;
import jp.co.fullness.ddd.domain.mapper.DomainBiMapper;
import jp.co.fullness.ddd.domain.model.stock.Stock;
import jp.co.fullness.ddd.domain.model.stock.StockId;
import jp.co.fullness.ddd.domain.model.stock.StockQuantity;

/**
 * {@link Stock} エンティティと {@link StockDTO} の相互変換を行うMapper。
 */
@Mapper(componentModel = "spring")
public interface StockMapper extends DomainBiMapper<StockDTO, Stock> {

    /**
     * StockDTO から Stock エンティティを再構築する。
     *
     * @param dto StockDTO
     * @return Stock エンティティ
     * @throws InvalidInputException DTOの必須値が欠落している場合
     */
    @Override
    default Stock toDomain(StockDTO dto) {
        if (dto == null) {
            throw new InvalidInputException("StockDTOがnullです。");
        }
        if (dto.getQuantity() == null) {
            throw new InvalidInputException("在庫数は必須です。");
        }
        if (!StringUtils.hasText(dto.getId())) {
            return Stock.createNew(StockQuantity.of(dto.getQuantity()));
        }
        return Stock.restore(StockId.fromString(dto.getId()), StockQuantity.of(dto.getQuantity()));
    }

    /**
     * Stock エンティティを StockDTO に変換する。
     *
     * @param domain Stock エンティティ
     * @return StockDTO
     * @throws InvalidInputException 引数がnullの場合
     */
    @Override
    default StockDTO fromDomain(Stock domain) {
        if (domain == null) {
            throw new InvalidInputException("Stockがnullです。");
        }
        return new StockDTO(domain.getStockId().value(), domain.getQuantity().value());
    }
}