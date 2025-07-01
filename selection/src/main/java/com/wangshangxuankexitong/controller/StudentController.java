package com.wangshangxuankexitong.controller;

import com.wangshangxuankexitong.entity.Student;
import com.wangshangxuankexitong.entity.User;
import com.wangshangxuankexitong.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    // 通用权限检查方法
    private boolean isStudent(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "student".equals(user.getRole());
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isStudent(session)) return "redirect:/login";

        User user = (User) session.getAttribute("user");
        // 不再向 session 存 student
        // session.setAttribute("student", student);

        // 每次都从数据库获取最新的学生信息
        Student student = studentService.getById(user.getUsername());

        if (student == null) {
            return "redirect:/student/profile/complete";
        }

        // 将所有需要的数据都通过 model 传递
        model.addAttribute("student", student); // 把 student 对象传给前端
        model.addAttribute("myCourses", studentService.getSelectedCourses(student.getSno()));
        model.addAttribute("availableCourses", studentService.getAvailableCourses(student.getSno()));

        // 你原来的 addUsernameToModel 的功能，可以合并到这里
        model.addAttribute("currentUsername", user.getUsername());

        return "student_dashboard";
    }


    @GetMapping("/profile/complete")
    public String showCompleteProfileForm(HttpSession session, Model model) {
        if (!isStudent(session)) return "redirect:/login";
        User user = (User) session.getAttribute("user");

        Student student = new Student();
        student.setSno(user.getUsername()); // 自动填充学号为用户名
        model.addAttribute("student", student);

        return "complete_student_profile";
    }
    @GetMapping("/profile/edit")
    public String showEditProfileForm(HttpSession session, Model model) {
        // 1. 从 Session 中获取当前登录的用户信息
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"student".equals(currentUser.getRole())) {
            // 如果未登录或角色不正确，重定向到登录页
            return "redirect:/login";
        }

        // 2. 使用当前用户的用户名（即学号）从数据库获取完整的学生信息
        Student student = studentService.getById(currentUser.getUsername());
        if (student == null) {
            // 如果找不到学生信息（理论上不应该发生），给出提示
            model.addAttribute("error", "无法加载您的个人信息，请联系管理员。");
            return "student_dashboard"; // 或者一个错误页面
        }

        // 3. 将学生信息传递给前端页面
        model.addAttribute("student", student);
        // 告诉表单，提交地址是这里
        model.addAttribute("formAction", "/student/profile/update");
        // 设置页面标题
        model.addAttribute("pageTitle", "修改我的个人信息");
        // 添加用户名到模型，用于页面顶部显示
        model.addAttribute("currentUsername", currentUser.getUsername());
        // 禁用学号输入框，因为学号（主键）不应该被修改
        model.addAttribute("isProfileEdit", true);
        model.addAttribute("backUrl", "/student/dashboard");

        // 4. 复用管理员的编辑表单页面
        return "edit_student_form";
    }

    /**
     * 处理学生提交的个人信息修改。
     */
    @PostMapping("/profile/update")
    public String updateProfile(Student student, HttpSession session, RedirectAttributes redirectAttributes) {
        // 1. 权限检查
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"student".equals(currentUser.getRole())) {
            return "redirect:/login";
        }

        // 2. 安全性检查：确保学生只能修改自己的信息！
        // 防止恶意用户通过修改表单的 sno 值来更新其他学生的信息。
        if (!currentUser.getUsername().equals(student.getSno())) {
            redirectAttributes.addFlashAttribute("error", "非法操作！您只能修改自己的信息。");
            return "redirect:/student/dashboard";
        }

        // 3. 调用服务层更新信息
        studentService.updateById(student);

        // 4. 给出成功提示并重定向
        redirectAttributes.addFlashAttribute("message", "个人信息更新成功！");
        return "redirect:/student/dashboard"; // 重定向回修改页面，可以立即看到修改结果
    }

    @PostMapping("/profile/complete")
    public String handleCompleteProfile(Student student, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isStudent(session)) return "redirect:/login";

        User user = (User) session.getAttribute("user");
        if(!student.getSno().equals(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "禁止操作！学号与登录用户不符。");
            return "redirect:/student/profile/complete";
        }

        if (studentService.getById(student.getSno()) != null){
            redirectAttributes.addFlashAttribute("error", "该学号信息已被录入，请勿重复操作!");
            return "redirect:/student/profile/complete";
        }

        studentService.save(student);
        redirectAttributes.addFlashAttribute("message", "个人信息已完善！");
        return "redirect:/student/dashboard";
    }

    @PostMapping("/course/select/{cno}")
    public String selectCourse(@PathVariable String cno, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isStudent(session)) return "redirect:/login";
        // 不再从 session 获取 student，而是从 user 获取学号
        User user = (User) session.getAttribute("user");
        if (studentService.selectCourse(user.getUsername(), cno)) {
            redirectAttributes.addFlashAttribute("message", "选课成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "选课失败，可能已选过该课程。");
        }
        return "redirect:/student/dashboard";
    }

    @PostMapping("/course/drop/{cno}")
    public String dropCourse(@PathVariable String cno, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isStudent(session)) return "redirect:/login";
        // 不再从 session 获取 student，而是从 user 获取学号
        User user = (User) session.getAttribute("user");
        if (studentService.dropCourse(user.getUsername(), cno)) {
            redirectAttributes.addFlashAttribute("message", "退课成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "退课失败！");
        }
        return "redirect:/student/dashboard";
    }
}