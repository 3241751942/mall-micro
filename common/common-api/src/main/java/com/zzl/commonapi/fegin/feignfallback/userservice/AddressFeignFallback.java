package com.zzl.commonapi.fegin.feignfallback.userservice;

import com.zzl.commonapi.dto.userservicedto.AddressInternalDTO;
import com.zzl.commonapi.fegin.userservicefeign.AddressFeignClient;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

public class AddressFeignFallback implements FallbackFactory<AddressFeignClient> {
    @Override
    public AddressFeignClient create(Throwable cause) {
        return new AddressFeignClient(){

            @Override
            public AddressInternalDTO getDefaultAddress(Long userId){
                return null;

            }
            @Override
            public List<AddressInternalDTO> getUserAddresses(Long userId){
                return List.of();
            }
            @Override
            public AddressInternalDTO getAddressById(Long addressId){
                return null;
            }

        };
    }
}
