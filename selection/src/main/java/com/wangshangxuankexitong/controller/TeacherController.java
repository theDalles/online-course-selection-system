package com.wangshangxuankexitong.controller;

import com.wangshangxuankexitong.entity.Course;
import com.wangshangxuankexitong.entity.Student;
import com.wangshangxuankexitong.entity.Teacher;
import com.wangshangxuankexitong.entity.User;
import com.wangshangxuankexitong.service.CourseService;
import com.wangshangxuankexitong.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private CourseService courseService;
    // --- 辅助方法 (保持不变) ---
    private boolean isTeacher(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "teacher".equals(user.getRole());
    }

    // --- 主要功能接口 (进行优化修改) ---
    private void addUsernameToModel(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            model.addAttribute("currentUsername", currentUser.getUsername());
        }
    }
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        System.out.println("\n==================== 进入 TeacherController.dashboard 方法 ====================");

        if (!isTeacher(session)) {
            System.out.println("用户不是教师或未登录，重定向到 /login");
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("user");
        Teacher teacher = teacherService.getById(user.getUsername());

        if (teacher == null) {
            System.out.println("教师信息未补全，重定向到 /teacher/profile/complete");
            return "redirect:/teacher/profile/complete";
        }

        System.out.println("当前登录的教师信息: TNO=" + teacher.getTno() + ", TName=" + teacher.getTname());
        model.addAttribute("teacher", teacher);

        System.out.println("准备调用 courseService.findCoursesByTeacherTno...");
        // 调用 Service 方法
        List<Course> myCourses = courseService.findCoursesByTeacherTno(teacher.getTno());

        // 【关键调试点】检查从 Service 返回的值
        if (myCourses == null) {
            System.out.println("!!! 警告：courseService.findCoursesByTeacherTno 方法返回了 null !!!");
            // 进行补救，创建一个空列表，防止前端报错
            myCourses = new java.util.ArrayList<>();
            System.out.println("已手动创建空列表以避免前端错误。");
        } else {
            System.out.println("courseService.findCoursesByTeacherTno 方法返回了一个列表，大小为: " + myCourses.size());
        }

        // 将最终的 myCourses 变量添加到 Model
        model.addAttribute("myCourses", myCourses);
        System.out.println("已将 myCourses (大小: " + myCourses.size() + ") 添加到 Model。");
        System.out.println("==================== 准备渲染 teacher_dashboard 模板 ====================\n");

        return "teacher_dashboard";
    }

    @GetMapping("/profile/complete")
    public String showCompleteProfileForm(HttpSession session, Model model) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        Teacher teacher = new Teacher();
        teacher.setTno(user.getUsername());

        model.addAttribute("teacher", teacher);
        model.addAttribute("currentUsername", user.getUsername());
        model.addAttribute("backUrl", "/teacher/dashboard");
        return "complete_teacher_profile";
    }

    @PostMapping("/profile/complete")
    public String handleCompleteProfile(Teacher teacher, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.getUsername().equals(teacher.getTno())) {
            redirectAttributes.addFlashAttribute("error", "非法操作！提交的教师号与当前用户不匹配。");
            return "redirect:/teacher/profile/complete";
        }
        if (teacherService.getById(teacher.getTno()) != null) {
            redirectAttributes.addFlashAttribute("error", "您的信息已存在，无需重复提交。");
            return "redirect:/teacher/dashboard";
        }
        teacherService.save(teacher);
        redirectAttributes.addFlashAttribute("message", "个人信息已成功补全，欢迎您！");
        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(HttpSession session, Model model) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        User currentUser = (User) session.getAttribute("user");
        Teacher teacher = teacherService.getById(currentUser.getUsername());

        if (teacher == null) {
            return "redirect:/teacher/profile/complete";
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("formAction", "/teacher/profile/update");
        model.addAttribute("pageTitle", "修改我的个人信息");
        model.addAttribute("currentUsername", currentUser.getUsername());
        model.addAttribute("isProfileEdit", true);
        // 新增：为“返回”按钮提供动态URL
        model.addAttribute("backUrl", "/teacher/dashboard");
        model.addAttribute("backUrl", "/teacher/dashboard");

        return "edit_teacher_form";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Teacher teacher, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.getUsername().equals(teacher.getTno())) {
            redirectAttributes.addFlashAttribute("error", "非法操作！您只能修改自己的信息。");
            return "redirect:/teacher/dashboard";
        }

        teacherService.updateById(teacher);
        redirectAttributes.addFlashAttribute("message", "个人信息更新成功！");

        // ================== 核心修复：修改重定向目标 ==================
        // 不再重定向回修改页面，而是重定向回教师仪表板
        return "redirect:/teacher/dashboard";
        // ==========================================================
    }
    // 在 TeacherController.java 文件中，可以添加到文件的末尾，} 之前

// --- 新增：课程管理功能 ---

    /**
     * 显示“开设新课程”的表单页面。
     * 这个方法处理 GET /teacher/course/create 请求。
     */
    @GetMapping("/course/create")
    public String showCreateCourseForm(HttpSession session, Model model) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }

        model.addAttribute("course", new Course());
        model.addAttribute("formAction", "/teacher/course/save");
        model.addAttribute("pageTitle", "开设新课程");
        model.addAttribute("backUrl", "/teacher/dashboard");
        addUsernameToModel(session, model);

        // ================== 添加这一行，这是关键 ==================
        // 设置一个标志，告诉前端现在是教师在操作，应该隐藏教师选择框
        model.addAttribute("isTeacherMode", true);
        // =======================================================

        // 注意：在教师模式下，我们【不】需要传递 teachers 列表
        // model.addAttribute("teachers", teacherService.list()); // 这行不需要
        model.addAttribute("backUrl", "/teacher/dashboard");

        return "edit_course_form";
    }



    /**
     * 处理教师提交的“开设新课程”表单。
     * 这个方法处理 POST /teacher/course/save 请求。
     */
    @PostMapping("/course/save")
    public String saveNewCourse(Course course, HttpSession session, RedirectAttributes redirectAttributes) {
        // 权限检查
        if (!isTeacher(session)) {
            return "redirect:/login";
        }

        User currentUser = (User) session.getAttribute("user");

        // 1. 自动设置授课教师为当前登录的教师
        course.setTnoFk(currentUser.getUsername());

        // 2. 检查课程号是否已被使用
        // 注意：这里需要注入 CourseService
        // 确保你的 TeacherController 顶部有 @Autowired private CourseService courseService;
        Course existingCourse = courseService.getById(course.getCno());
        if (existingCourse != null) {
            redirectAttributes.addFlashAttribute("error", "开设失败：课程号 " + course.getCno() + " 已经存在！");
            redirectAttributes.addFlashAttribute("course", course); // 将用户填写的数据传回
            return "redirect:/teacher/course/create";
        }

        // 3. 保存新课程
        courseService.save(course);

        // 4. 给出成功提示并重定向到仪表板
        redirectAttributes.addFlashAttribute("message", "课程 " + course.getCname() + " 已成功开设！");
        return "redirect:/teacher/dashboard";
    }
    @GetMapping("/course/detail/{cno}")
    public String showCourseDetail(@PathVariable("cno") String cno, Model model, HttpSession session) {
        // 权限检查
        if (!isTeacher(session)) {
            return "redirect:/login";
        }

        // 1. 获取课程信息
        Course course = courseService.getById(cno);
        if (course == null) {
            // 课程不存在，重定向回仪表盘
            return "redirect:/teacher/dashboard";
        }

        // 2. 【核心修改】直接调用您在 TeacherService 中已经写好的方法！
        List<Student> selectedStudents = teacherService.getEnrolledStudents(cno);

        // 3. 将数据添加到 Model 中
        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", selectedStudents); // 将查询到的学生列表传递给前端
        addUsernameToModel(session, model);
        model.addAttribute("backUrl", "/teacher/dashboard");

        // 4. 返回详情页面的模板名
        // 确保您在 templates 目录下创建了 course_detail.html 文件
        return "course_detail";
    }
}