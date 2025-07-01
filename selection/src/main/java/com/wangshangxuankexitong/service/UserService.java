package com.wangshangxuankexitong.service;

import com.baomidou.mybatisplus.extension.service.IService; // 导入 IService
import com.wangshangxuankexitong.entity.User;

public interface UserService extends IService<User> { // <-- 关键改动：继承 IService<User>
    // 我们自己定义的特殊方法仍然保留
    User login(String username, String password);
    boolean register(User user);
    User findByUsername(String username);
    boolean changePassword(String username, String oldPassword, String newPassword);
    }
