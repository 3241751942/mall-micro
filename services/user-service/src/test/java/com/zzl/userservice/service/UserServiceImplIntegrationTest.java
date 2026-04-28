package com.zzl.userservice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.userservice.entity.User;
import com.zzl.userservice.exception.UserNotFoundException;
import com.zzl.userservice.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("用户服务集成测试")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private final String rawPassword = "123456";

    @BeforeEach
    void setUp() {
        // 准备一个测试用户，每次测试前插入（事务回滚时自动清理）
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode(rawPassword));
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setNickname("测试用户");
        testUser.setStatus(1);
        userMapper.insert(testUser);
    }

    @Nested
    @DisplayName("注册用户测试")
    class RegisterTest {

        @Test
        @DisplayName("成功注册新用户")
        void shouldRegisterSuccess() {
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setPassword("password123");
            newUser.setEmail("new@example.com");

            userService.register(newUser);

            User saved = userMapper.selectById(newUser.getId());
            assertThat(saved).isNotNull();
            assertThat(saved.getUsername()).isEqualTo("newuser");
        }

        @Test
        @DisplayName("用户名已存在，注册失败")
        void shouldFailWhenUsernameExists() {
            User duplicateUser = new User();
            duplicateUser.setUsername("testuser"); // 已存在的用户名
            duplicateUser.setPassword("any");

            assertThatThrownBy(() -> userService.register(duplicateUser))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("用户账号已存在");
        }
    }

    @Nested
    @DisplayName("获取用户信息测试")
    class GetUserInfoTest {

        @Test
        @DisplayName("存在的用户返回正确信息")
        void shouldReturnUserWhenExists() {
            User found = userService.getUserInfo(testUser.getId());
            assertThat(found).isNotNull();
            assertThat(found.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("不存在的用户抛出异常")
        void shouldThrowWhenUserNotExists() {
            assertThatThrownBy(() -> userService.getUserInfo(99999L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("修改密码测试")
    class UpdatePasswordTest {

        @Test
        @DisplayName("旧密码正确，修改成功")
        void shouldUpdateSuccessWhenOldPasswordCorrect() {
            String newPassword = "newPassword789";
            userService.updatePassword(testUser.getId(), newPassword, rawPassword);

            User updated = userMapper.selectById(testUser.getId());

            assertThat(passwordEncoder.matches(newPassword, updated.getPassword())).isTrue();
        }

        @Test
        @DisplayName("旧密码错误，抛出异常")
        void shouldThrowWhenOldPasswordWrong() {
            assertThatThrownBy(() -> userService.updatePassword(testUser.getId(), "new", "wrong"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("原密码错误");
        }
    }

    @Nested
    @DisplayName("修改用户状态测试")
    class ChangeStatusTest {

        @Test
        @DisplayName("状态修改成功")
        void shouldChangeStatusSuccess() {
            userService.changeStatus(testUser.getId(), 0);
            User updated = userMapper.selectById(testUser.getId());
            assertThat(updated.getStatus()).isEqualTo(0);
        }

        @Test
        @DisplayName("用户不存在，修改失败")
        void shouldThrowWhenUserNotExists() {
            assertThatThrownBy(() -> userService.changeStatus(99999L, 1))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("分页查询用户列表测试")
    class ListUsersTest {

        @Test
        @DisplayName("无条件分页查询")
        void shouldPageAllUsers() {
            Page<User> page = userService.listUsers(1, 10, null, null);
            assertThat(page.getRecords()).isNotEmpty();
            assertThat(page.getTotal()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("按用户名模糊查询")
        void shouldFilterByUsername() {
            Page<User> page = userService.listUsers(1, 10, "test", null);
            assertThat(page.getRecords()).allMatch(u -> u.getUsername().contains("test"));
        }

        @Test
        @DisplayName("按状态过滤")
        void shouldFilterByStatus() {
            Page<User> page = userService.listUsers(1, 10, null, 1);
            assertThat(page.getRecords()).allMatch(u -> u.getStatus() == 1);
        }
    }

    @Nested
    @DisplayName("修改用户信息测试")
    class UpdateUserByIdTest {

        @Test
        @DisplayName("成功修改可更新字段")
        void shouldUpdateUserInfoSuccess() {
            User updateRequest = new User();
            updateRequest.setNickname("新昵称");
            updateRequest.setPhone("13912345678");
            updateRequest.setEmail("newemail@xx.com");
            updateRequest.setAvatar("http://avatar.com/1.jpg");

            User updated = userService.updateUserById(updateRequest, testUser.getId());

            assertThat(updated.getNickname()).isEqualTo("新昵称");
            assertThat(updated.getPhone()).isEqualTo("13912345678");
            assertThat(updated.getEmail()).isEqualTo("newemail@xx.com");
            assertThat(updated.getAvatar()).isEqualTo("http://avatar.com/1.jpg");
            // 密码不应被修改
            assertThat(updated.getAvatar()).isEqualTo("http://avatar.com/1.jpg");
            assertThat(updated.getNickname()).isEqualTo("新昵称");
            assertThat(updated.getPhone()).isEqualTo("13912345678");
            assertThat(passwordEncoder.matches(rawPassword, updated.getPassword())).isTrue();
        }

        @Test
        @DisplayName("用户不存在，修改失败")
        void shouldThrowWhenUserNotExists() {
            assertThatThrownBy(() -> userService.updateUserById(new User(), 99999L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("用户登录验证测试")
    class ValidateUserTest {

        @Test
        @DisplayName("账号密码正确，返回用户")
        void shouldReturnUserWhenCredentialsValid() {

            User validated = userService.validateUser("testuser", rawPassword);
            assertThat(validated).isNotNull();
            assertThat(validated.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("账号不存在，抛出异常")
        void shouldThrowWhenAccountNotExist() {
            assertThatThrownBy(() -> userService.validateUser("unknown", "any"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("账号或密码错误");
        }

        @Test
        @DisplayName("密码错误，抛出异常")
        void shouldThrowWhenPasswordWrong() {
            assertThatThrownBy(() -> userService.validateUser("testuser", "wrong"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("账号或密码错误");
        }
    }
}