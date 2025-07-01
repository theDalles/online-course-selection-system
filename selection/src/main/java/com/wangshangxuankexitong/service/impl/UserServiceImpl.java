package com.wangshangxuankexitong.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; // 导入 ServiceImpl
import com.wangshangxuankexitong.dao.UserDao;
import com.wangshangxuankexitong.entity.User;
import com.wangshangxuankexitong.service.UserService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService { // <-- 关键改动

    // login, register, findByUsername 这些自定义的方法完全不需要改变，保留原样即可。
    // getById, removeById, save, list 等方法已经由 ServiceImpl 自动提供了，我们不需要再写。

    @Override
    public User login(String username, String password) {
        User user = findByUsername(username);
        // 警告: 生产环境中密码应该是加密后比对!
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        if (findByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        }
        // 警告: 生产环境中密码必须加密存储!
        return this.save(user); // 使用继承来的 save 方法
    }

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper); // 使用继承来的 getOne 方法
    }
    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // 1. 根据用户名从数据库中查找用户
        User user = this.findByUsername(username);
        if (user == null) {
            // 理论上，已登录用户不可能找不到，但作为防御性编程，还是加上判断
            return false;
        }

        // 2. 验证旧密码是否正确
        // ！！！重要！！！
        // 这里的验证方式取决于你的密码是如何存储的。
        // 假设你的密码是明文存储的（非常不安全，但为了演示）
        if (!user.getPassword().equals(oldPassword)) {
            // 如果数据库存的密码和用户输入的旧密码不匹配，则验证失败
            return false;
        }

        // 如果你的密码是加密存储的（例如使用BCrypt），你需要这样做：
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // if (!encoder.matches(oldPassword, user.getPassword())) {
        //     return false;
        // }

        // 3. 将新密码更新到数据库
        // ！！！重要！！！
        // 如果密码需要加密，在这里加密新密码
        user.setPassword(newPassword); // 明文方式
        // user.setPassword(encoder.encode(newPassword)); // 加密方式

        // 4. 调用 updateById 保存更新后的用户信息
        return this.updateById(user);
    }
}