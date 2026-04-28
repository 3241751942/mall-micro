package com.zzl.userservice.service;

import com.zzl.userservice.entity.UserAddress;
import com.zzl.userservice.exception.AddressNotFoundException;
import com.zzl.userservice.service.serviceimpl.UserAddressServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // 自动回滚，不污染数据库
public class UserAddressServiceImplTest {

    @Autowired
    private UserAddressServiceImpl userAddressService;

    // ====================== 1. 测试 新增地址 ======================
    @Test
    void testAddAddress() {
        UserAddress address = new UserAddress();
        address.setUserId(100L);
        address.setReceiverName("测试姓名");
        address.setPhone("13800138000");
        address.setProvince("广东省");
        address.setCity("深圳市");
        address.setDistrict("南山区");
        address.setDetail("测试街道123号");
        address.setIsDefault(0);

        // 执行新增
        userAddressService.addAddress(address);

        // 断言：ID 已回填
        Assertions.assertNotNull(address.getId());
    }

    // ====================== 2. 测试 根据ID查询地址 ======================
    @Test
    void testGetAddressById() {
        // 先新增一条
        UserAddress address = buildTestAddress(100L);
        userAddressService.addAddress(address);

        // 查询
        UserAddress result = userAddressService.getAddressById(address.getId());
        assertNotNull(result);
    }

    @Test
    void testGetAddressById_NotFound() {
        // 查不存在的ID，必须抛异常
        assertThrows(AddressNotFoundException.class, () -> {
            userAddressService.getAddressById(999999L);
        });
    }

    // ====================== 3. 测试 修改地址 ======================
    @Test
    void testUpdateAddressById() {
        // 1. 新增
        UserAddress address = buildTestAddress(100L);
        userAddressService.addAddress(address);

        // 2. 构造修改数据
        UserAddress update = new UserAddress();
        update.setReceiverName("修改姓名");
        update.setPhone("13999999999");

        // 3. 执行修改
        UserAddress updated = userAddressService.updateAddressById(update, address.getId());

        // 4. 验证
        assertEquals("修改姓名", updated.getReceiverName());
        assertEquals("13999999999", updated.getPhone());
    }

    // ====================== 4. 测试 设置默认地址 ======================
    @Test
    void testSetDefaultAddress() {
        // 1. 新增2条地址
        UserAddress a1 = buildTestAddress(100L);
        UserAddress a2 = buildTestAddress(100L);
        userAddressService.addAddress(a1);
        userAddressService.addAddress(a2);

        // 2. 设置默认
        userAddressService.setDefaultAddress(a2.getId(), 100L);

        // 3. 查默认
        UserAddress defaultAddr = userAddressService.getDefaultAddressByUserId(100L);
        assertEquals(a2.getId(), defaultAddr.getId());
    }

    // ====================== 5. 测试 获取用户所有地址 ======================
    @Test
    void testGetAddressesByUserId() {
        userAddressService.addAddress(buildTestAddress(100L));
        userAddressService.addAddress(buildTestAddress(100L));

        var list = userAddressService.getAddressesByUserId(100L);
        assertTrue(list.size() >= 2);
    }

    // ====================== 6. 测试 获取默认地址 ======================
    @Test
    void testGetDefaultAddress() {
        UserAddress address = buildTestAddress(100L);
        address.setIsDefault(1);
        userAddressService.addAddress(address);

        UserAddress defaultAddr = userAddressService.getDefaultAddressByUserId(100L);
        assertNotNull(defaultAddr);
    }

    // ====================== 7. 测试 删除地址 ======================
    @Test
    void testDeleteAddress() {
        UserAddress address = buildTestAddress(100L);
        userAddressService.addAddress(address);

        // 删除
        userAddressService.deleteAddressById(address.getId());

        // 再查会抛异常
        assertThrows(AddressNotFoundException.class, () -> {
            userAddressService.getAddressById(address.getId());
        });
    }

    // ====================== 工具方法：构建测试地址 ======================
    private UserAddress buildTestAddress(Long userId) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiverName("测试");
        address.setPhone("13800138000");
        address.setProvince("广东省");
        address.setCity("深圳市");
        address.setDistrict("南山区");
        address.setDetail("测试地址");
        address.setIsDefault(0);
        return address;
    }
}