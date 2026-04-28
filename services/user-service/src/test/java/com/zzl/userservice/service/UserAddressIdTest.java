package com.zzl.userservice.service;


import com.zzl.userservice.entity.UserAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserAddressIdTest {

    @Autowired
    private UserAddressService userAddressService;

    @Test
    public void testSaveAutoGetId() {
        // 1. 新建一个地址对象，此时 id = null
        UserAddress address = new UserAddress();
        address.setUserId(100L);           // 随便写个测试用户ID
        address.setReceiverName("测试姓名");
        address.setPhone("13800138000");
        address.setProvince("广东省");
        address.setCity("深圳市");
        address.setDistrict("南山区");
        address.setDetail("测试街道123号");
        address.setIsDefault(0);

        // 保存前 ID 一定是 null
        System.out.println("===== 保存前 id = " + address.getId());

        // 2. 调用 save 保存
        boolean saveSuccess = userAddressService.save(address);

        // 3. 保存后，直接拿 ID！
        System.out.println("===== 保存成功 = " + saveSuccess);
        System.out.println("===== 保存后 id = " + address.getId());
    }
}
