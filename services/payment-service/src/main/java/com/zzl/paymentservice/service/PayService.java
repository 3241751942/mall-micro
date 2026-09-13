package com.zzl.paymentservice.service;

import com.zzl.commonapi.dto.orderservicedto.Item;

import java.math.BigDecimal;
import java.util.List;

public interface PayService {
    String aliPagePay(String paymentNo, List<Item> items, BigDecimal totalAmount) throws Exception;
}
