package com.zzl.stockservice.converter;


import com.zzl.commonapi.dto.stockservicedto.StockDTO;
import com.zzl.stockservice.dto.StockVO;
import com.zzl.stockservice.entity.Stock;

/**
 * 库存实体与DTO转换器
 */
public class StockConverter {

    /**
     * 将实体转换为内部DTO
     * @param stock 库存实体
     * @return 对内内部DTO
     */
    public static StockDTO toDTO(Stock stock) {
        if (stock == null) {
            return null;
        }
        StockDTO dto = new StockDTO();
        dto.setProductId(stock.getProductId());
        dto.setTotalStock(stock.getTotalStock());
        dto.setLockedStock(stock.getLockedStock());
        dto.setSoldStock(stock.getSoldStock());
        dto.setAvailableStock(stock.getTotalStock() - stock.getLockedStock());
        return dto;
    }

    /**
     * 将实体转换为对外VO
     * @param stock 库存实体
     * @return 对外VO
     */
    public static StockVO toVO(Stock stock) {
        if (stock == null) {
            return null;
        }
        StockVO vo = new StockVO();
        vo.setProductId(stock.getProductId());
        vo.setAvailableStock(stock.getTotalStock() - stock.getLockedStock());
        return vo;
    }
}