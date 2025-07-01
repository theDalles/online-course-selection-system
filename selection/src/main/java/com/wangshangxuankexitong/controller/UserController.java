package com.wangshangxuankexitong.controller;

import com.wangshangxuankexitong.entity.User;
import com.wangshangxuankexitong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping({"/", "/login"})
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(User user, RedirectAttributes redirectAttributes) {
        // 基本校验
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getRole() == null || user.getRole().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "所有字段均为必填项!");
            return "redirect:/register";
        }

        if (userService.register(user)) {
            redirectAttributes.addFlashAttribute("message", "注册成功，请登录！");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "用户名已存在！");
            return "redirect:/register";
        }
    }

    @PostMapping("/login")
    public String handleLogin(String username, String password, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            switch (user.getRole()) {
                case "student": return "redirect:/student/dashboard";
                case "teacher": return "redirect:/teacher/dashboard";
                case "admin": return "redirect:/admin/dashboard";
                default:
                    redirectAttributes.addFlashAttribute("error", "未知角色!");
                    return "redirect:/login";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "用户名或密码错误!");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "您已成功退出登录。");
        return "redirect:/login";
    }
    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        // 检查用户是否已登录
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUsername", currentUser.getUsername());

        // 根据用户角色，设置返回按钮的URL
        String backUrl = "/"; // 默认返回首页
        switch (currentUser.getRole()) {
            case "admin":
                backUrl = "/admin/dashboard";
                break;
            case "student":
                backUrl = "/student/dashboard";
                break;
            case "teacher":
                backUrl = "/teacher/dashboard";
                break;
        }
        model.addAttribute("backUrl", backUrl);

        return "change_password";
    }

    /**
     * 处理修改密码的表单提交。
     * 使用 @RequestParam 来接收表单字段。
     */
    @PostMapping("/change-password")
    public String handleChangePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        // 再次检查用户登录状态
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 基本校验：新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "两次输入的新密码不一致！");
            return "redirect:/change-password";
        }

        try {
            // 调用 Service 层的方法来处理核心业务逻辑
            boolean success = userService.changePassword(currentUser.getUsername(), oldPassword, newPassword);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "密码修改成功！");
            } else {
                // Service 层返回 false 通常意味着旧密码错误
                redirectAttributes.addFlashAttribute("error", "旧密码不正确，请重试！");
            }
        } catch (Exception e) {
            // 捕获其他可能的异常
            redirectAttributes.addFlashAttribute("error", "修改密码时发生未知错误：" + e.getMessage());
        }

        // 无论成功与否，都重定向回修改密码页面显示结果
        return "redirect:/change-password";
    }
}