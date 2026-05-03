package com.zzl.stockservice.controller.internal;


import com.zzl.commonapi.dto.stockservicedto.StockDTO;
import com.zzl.commonapi.dto.stockservicedto.StockLockRequest;
import com.zzl.commonapi.dto.stockservicedto.StockLockResult;
import com.zzl.commoncore.result.Result;
import com.zzl.stockservice.converter.StockConverter;
import com.zzl.stockservice.entity.Stock;
import com.zzl.stockservice.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/internal/stocks")
@RequiredArgsConstructor
public class InternalStockController {
    private final StockService stockService;

    @PostMapping("/lock")
    public StockLockResult lock(@RequestBody @Valid StockLockRequest request) {
        return stockService.lockStock(request.getProductId(), request.getQuantity(), request.getOrderNo());
    }

    @PostMapping("/confirm")
    public Result<Void> confirm(@RequestBody @Valid StockLockRequest request) {
        boolean success = stockService.confirmStock(request.getProductId(), request.getQuantity(), request.getOrderNo());
        return success ? Result.success() : Result.error("确认扣减失败");
    }

    @PostMapping("/unlock")
    public Result<Void> unlock(@RequestBody @Valid StockLockRequest request) {
        boolean success = stockService.unlockStock(request.getProductId(), request.getQuantity(), request.getOrderNo());
        return success ? Result.success() : Result.error("解锁失败");
    }

    @PostMapping("/batch")
    public List<StockDTO> batchQuery(@RequestBody List<Long> productIds) {
        List<Stock> stocks= stockService.batchGetStock(productIds);
        return stocks.stream().map(StockConverter::toDTO).toList();
    }
}