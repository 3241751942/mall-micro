package com.zzl.paymentservice.service.impl;


import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.zzl.commonapi.dto.orderservicedto.Item;
import com.zzl.paymentservice.config.AlipayConfig;
import com.zzl.paymentservice.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;

    @Override
    public String aliPagePay(String paymentNo, List<Item> items, BigDecimal totalAmount) throws Exception{

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setNotifyUrl(alipayConfig.getNotifyUrl());

        List<GoodsDetail> goods =new ArrayList<>();
        for(Item item:items){
            GoodsDetail goodsDetail = new GoodsDetail();
            goodsDetail.setGoodsId(item.getItemId().toString());
            goodsDetail.setGoodsName(item.getItemName());
            goodsDetail.setPrice(item.getPrice().multiply(new BigDecimal(100)).longValue() + "");
            goodsDetail.setQuantity(Long.valueOf(item.getQuantity()));
            goods.add(goodsDetail);
        }

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(paymentNo);
        model.setSubject("商城订单");
        model.setTotalAmount(totalAmount.toString());
        model.setGoodsDetail(goods);
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        request.setBizModel(model);

        AlipayTradePagePayResponse response =alipayClient.pageExecute(request);

        return response.getBody();
    }
}
