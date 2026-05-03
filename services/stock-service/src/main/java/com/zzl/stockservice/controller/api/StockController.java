package com.zzl.stockservice.controller.api;

import com.zzl.commoncore.result.Result;
import com.zzl.stockservice.dto.StockVO;
import com.zzl.stockservice.entity.Stock;
import com.zzl.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;


    /**
     * 查看该商品的可用库存量
     * @param productId 目标商品Id
     * @return StockVO 商品Id及其可用库存
     */
    @GetMapping("/{productId}")
    public Result<StockVO> getStock(@PathVariable Long productId) {
        Stock stock = stockService.getStockByProductId(productId);
        StockVO vo = new StockVO();
        vo.setProductId(productId);
        if (stock != null) {
            int available = stock.getTotalStock() - stock.getLockedStock() - stock.getSoldStock();
            vo.setAvailableStock(Math.max(available, 0));
        } else {
            vo.setAvailableStock(0);
        }
        return Result.success(vo);
    }
}
