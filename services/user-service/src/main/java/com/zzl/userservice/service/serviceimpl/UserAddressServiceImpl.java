package com.zzl.userservice.service.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.userservice.entity.UserAddress;
import com.zzl.userservice.enums.UserServiceBizErrorCode;
import com.zzl.userservice.exception.AddressNotFoundException;
import com.zzl.userservice.mapper.UserAddressMapper;
import com.zzl.userservice.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {



    /**
     * 设置默认的地址
     * @param addressId 默认的地址Id
     * @param userId 用户Id
     */
    @Transactional
    @Override
    public void setDefaultAddress(Long addressId, Long userId) {
        // 先把该用户所有地址都改成 非默认
        this.lambdaUpdate()
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0) // 0=不是默认
                .update();

        // 再把当前选中的地址 改成 默认，这玩意是真高级
       boolean updated=lambdaUpdate()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getId, addressId)
                .set(UserAddress::getIsDefault, 1) // 1=默认
                .update();
       if(!updated){
           throw new AddressNotFoundException(UserServiceBizErrorCode.ADDRESS_NOT_FOUND,"默认地址设置失败");
       }
    }


    /**
     * 获取用户的所有地址
     * @param userId 用户Id
     * @return 用户的所有地址列表
     */
    @Override
    public List<UserAddress> getAddressesByUserId(Long userId) {
//        正常业务中不应该抛异常，给个空列表即可
//        if (addresses.isEmpty()) {
//            throw new AddressNotFoundException(UserServiceBizErrorCode.ADDRESS_NOT_FOUND);
//        }
        return lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime)
                .list();
    }

    /**
     * 通过地址Id修改地址
     * @param address 修改后地址实体（并没有注入Id）address为空回放原userAddress，不为空放回修改后的
     * @param addressId 地址Id
     */
    @Override
    @Transactional
    public UserAddress updateAddressById(UserAddress address, Long addressId) {
        UserAddress userAddress=getById(addressId);
        if(userAddress==null){
            throw new AddressNotFoundException(UserServiceBizErrorCode.ADDRESS_NOT_FOUND,"地址Id错误");
        }
        if (address!=null) {
            updateAddressIsNull(userAddress, address.getReceiverName(), address.getPhone(), address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail());
            boolean updated=updateById(userAddress);
            if(!updated){
                throw new AddressNotFoundException(UserServiceBizErrorCode.ADDRESS_NOT_FOUND,"地址修改失败");
            }
        }
        return getById(addressId);
    }

    /**
     *更新地址的中间转换
     *
     */
    public static void updateAddressIsNull(UserAddress userAddress, String receiverName, String phone, String province, String city, String district, String detail) {
        if (receiverName != null) userAddress.setReceiverName(receiverName);
        if (phone != null) userAddress.setPhone(phone);
        if (province != null) userAddress.setProvince(province);
        if (city != null) userAddress.setCity(city);
        if (district != null) userAddress.setDistrict(district);
        if (detail != null) userAddress.setDetail(detail);
    }

    /**
     * 通过地址Id获取地址
     * @param addressId 地址Id
     * @return UserAddress地址
     */
    @Override
    public UserAddress getAddressById(Long addressId) {
        UserAddress userAddress=getById(addressId);
        if (userAddress == null){
            throw new AddressNotFoundException(UserServiceBizErrorCode.ADDRESS_NOT_FOUND);//地址不存在异常处理
        }
        return  userAddress;
    }


    /**
     * 增加地址
     * @param address 需要增加的地址实体
     */
    @Override
    @Transactional
    public void addAddress(UserAddress address) {
        boolean saved=save(address);
        if(!saved){
            throw new AddressNotFoundException(UserServiceBizErrorCode.ADDRESS_NOT_FOUND,"地址新增失败");
        }
        if(address.getIsDefault()==1){
            setDefaultAddress(address.getId(), address.getUserId());
        }
    }

    /**
     * 删除地址记录
     * @param addressId 地址Id
     */
    @Override
    public void deleteAddressById(Long addressId) {
        if (!removeById(addressId)) {
            throw new AddressNotFoundException(UserServiceBizErrorCode.ADDRESS_NOT_FOUND,"地址删除失败");
        }
    }

    /**
     * 获取用户默认的地址
     * @param userId 用户Id
     * @return 返回默认地址
     */
    @Override
    public UserAddress getDefaultAddressByUserId(Long userId) {
        return lambdaQuery().eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
                .one();
    }
}
