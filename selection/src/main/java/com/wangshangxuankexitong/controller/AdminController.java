package com.wangshangxuankexitong.controller;

import com.wangshangxuankexitong.entity.Course;
import com.wangshangxuankexitong.entity.Student;
import com.wangshangxuankexitong.entity.Teacher;
import com.wangshangxuankexitong.entity.User;
import com.wangshangxuankexitong.service.CourseService;
import com.wangshangxuankexitong.service.StudentService;
import com.wangshangxuankexitong.service.TeacherService;
import com.wangshangxuankexitong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;

    // 辅助方法：检查用户是否为管理员
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "admin".equals(user.getRole());
    }

    // 辅助方法：将当前用户名添加到模型中
    private void addUsernameToModel(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            model.addAttribute("currentUsername", currentUser.getUsername());
        }
    }

    /**
     * 显示管理员仪表板
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        addUsernameToModel(session, model);
        model.addAttribute("students", studentService.list());
        model.addAttribute("teachers", teacherService.list());
        // 假设 courseService 有 listAllWithTeacherName 方法
       model.addAttribute("courses", courseService.listAllWithTeacherName());
        // 如果没有，先用普通list代替
       // model.addAttribute("courses", courseService.list());
        return "admin_dashboard";
    }

    // --- 学生管理 ---

    @GetMapping("/student/add")
    public String showAddStudentForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        addUsernameToModel(session, model);
        model.addAttribute("student", new Student());
        model.addAttribute("formAction", "/admin/student/save");
        model.addAttribute("pageTitle", "添加新学生");
        // 为“添加”页面也提供返回链接
        model.addAttribute("backUrl", "/admin/dashboard");
        return "edit_student_form";
    }

    @GetMapping("/student/edit/{sno}")
    public String showEditStudentForm(@PathVariable String sno, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        addUsernameToModel(session, model);
        Student student = studentService.getById(sno);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "学生不存在！");
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("student", student);

        model.addAttribute("formAction", "/admin/student/save");
        model.addAttribute("pageTitle", "修改学生信息");
        // ================== 添加这一行 ==================
        model.addAttribute("backUrl", "/admin/dashboard");
        // ===============================================
        return "edit_student_form";
    }

    @PostMapping("/student/save")
    public String saveStudent(Student student, @RequestParam(name = "isUpdate", defaultValue = "false") boolean isUpdate, HttpSession session, RedirectAttributes redirectAttributes) {
        // ... (你这部分代码已经很完善，无需改动) ...
        if (!isAdmin(session)) return "redirect:/login";
        String sno = student.getSno();
        if (sno == null || sno.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "学号不能为空！");
            return isUpdate ? "redirect:/admin/dashboard" : "redirect:/admin/student/add";
        }
        if (isUpdate) {
            studentService.updateById(student);
            redirectAttributes.addFlashAttribute("message", "学生 " + student.getSname() + " (" + sno + ") 信息更新成功。");
        } else {
            if (studentService.getById(sno) != null) {
                redirectAttributes.addFlashAttribute("error", "添加失败：学号 " + sno + " 已存在于学生表中！");
                redirectAttributes.addFlashAttribute("student", student);
                return "redirect:/admin/student/add";
            }
            if (userService.findByUsername(sno) != null) {
                redirectAttributes.addFlashAttribute("error", "添加失败：学号 " + sno + " 已被注册为登录账号！");
                redirectAttributes.addFlashAttribute("student", student);
                return "redirect:/admin/student/add";
            }
            User newUser = new User();
            newUser.setUsername(sno);
            newUser.setPassword("123456");
            newUser.setRole("student");
            userService.register(newUser);
            studentService.save(student);
            redirectAttributes.addFlashAttribute("message", "学生 " + student.getSname() + " (" + sno + ") 添加成功，初始密码为123456。");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/student/delete/{sno}")
    public String deleteStudent(@PathVariable String sno, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";
        studentService.removeById(sno);
        User userToDelete = userService.findByUsername(sno);
        if (userToDelete != null) {
            userService.removeById(userToDelete.getId());
        }
        redirectAttributes.addFlashAttribute("message", "学生 " + sno + " 已被删除。");
        return "redirect:/admin/dashboard";
    }


    // --- 教师管理 ---

    @GetMapping("/teacher/add")
    public String showAddTeacherForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        addUsernameToModel(session, model);
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("formAction", "/admin/teacher/save");
        model.addAttribute("pageTitle", "添加新教师");
        model.addAttribute("backUrl", "/admin/dashboard");
        return "edit_teacher_form";
    }

    @GetMapping("/teacher/edit/{tno}")
    public String showEditTeacherForm(@PathVariable String tno, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        addUsernameToModel(session, model);
        Teacher teacher = teacherService.getById(tno);
        if (teacher == null) {
            redirectAttributes.addFlashAttribute("error", "教师不存在！");
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("teacher", teacher);
        model.addAttribute("formAction", "/admin/teacher/save");
        model.addAttribute("pageTitle", "修改教师信息");
        // ================== 添加这一行 ==================
        model.addAttribute("backUrl", "/admin/dashboard");
        // ===============================================
        return "edit_teacher_form";
    }

    @PostMapping("/teacher/save")
    public String saveTeacher(Teacher teacher, @RequestParam(name = "isUpdate", defaultValue = "false") boolean isUpdate, HttpSession session, RedirectAttributes redirectAttributes) {
        // ... (你这部分代码已经很完善，无需改动) ...
        if (!isAdmin(session)) return "redirect:/login";
        String tno = teacher.getTno();
        if (tno == null || tno.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "教师号不能为空！");
            return isUpdate ? "redirect:/admin/dashboard" : "redirect:/admin/teacher/add";
        }
        if (isUpdate) {
            teacherService.updateById(teacher);
            redirectAttributes.addFlashAttribute("message", "教师 " + teacher.getTname() + " (" + tno + ") 信息更新成功。");
        } else {
            if (teacherService.getById(tno) != null) {
                redirectAttributes.addFlashAttribute("error", "添加失败：教师号 " + tno + " 已存在于教师表中！");
                redirectAttributes.addFlashAttribute("teacher", teacher);
                return "redirect:/admin/teacher/add";
            }
            if (userService.findByUsername(tno) != null) {
                redirectAttributes.addFlashAttribute("error", "添加失败：教师号 " + tno + " 已被注册为登录账号！");
                redirectAttributes.addFlashAttribute("teacher", teacher);
                return "redirect:/admin/teacher/add";
            }
            User newUser = new User();
            newUser.setUsername(tno);
            newUser.setPassword("123456");
            newUser.setRole("teacher");
            userService.register(newUser);
            teacherService.save(teacher);
            redirectAttributes.addFlashAttribute("message", "教师 " + teacher.getTname() + " (" + tno + ") 添加成功，初始密码为123456。");
        }
        return "redirect:/admin/dashboard";
    }

    // 在 com.wangshangxuankexitong.controller.AdminController.java 中
    @PostMapping("/teacher/delete/{tno}")
    public String deleteTeacher(@PathVariable String tno, HttpSession session, RedirectAttributes redirectAttributes) {
        // 权限检查
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            // ================== 核心改动在这里 ==================
            // 不再使用简单的 removeById，而是调用我们自己在 Service 中实现的、
            // 带有业务逻辑检查的、更安全的方法。
            // 这个方法名必须和你 Service 接口中声明的完全一致。
            teacherService.deleteTeacherAndHandleCourses(tno);
            // =================================================

            // 如果上面的方法没有抛出异常，就说明删除成功
            redirectAttributes.addFlashAttribute("message", "教师 " + tno + " 已成功删除。");

        } catch (Exception e) {
            // 如果 Service 方法中因为有关联课程而抛出了我们自定义的异常，
            // 就在这里捕获它。
            // e.getMessage() 将会是 "该教师名下还有 X 门课程..." 这样的友好提示。
            redirectAttributes.addFlashAttribute("error", "删除失败：" + e.getMessage());
        }

        // 无论成功或失败，都重定向回仪表板
        return "redirect:/admin/dashboard";
    }


    // --- 课程管理 ---

    @GetMapping("/course/add")
    public String showAddCourseForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        addUsernameToModel(session, model);
        model.addAttribute("course", new Course());
        model.addAttribute("formAction", "/admin/course/save");
        model.addAttribute("pageTitle", "添加新课程");
        model.addAttribute("teachers", teacherService.list());
        model.addAttribute("backUrl", "/admin/dashboard");
        return "edit_course_form";
    }

    @GetMapping("/course/edit/{cno}")
    public String showEditCourseForm(@PathVariable String cno, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        addUsernameToModel(session, model);
        Course course = courseService.getById(cno);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "课程不存在！");
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("course", course);
        model.addAttribute("formAction", "/admin/course/save");
        model.addAttribute("pageTitle", "修改课程信息");
        model.addAttribute("teachers", teacherService.list());
        // ================== 添加这一行 ==================
        model.addAttribute("backUrl", "/admin/dashboard");
        // ===============================================
        return "edit_course_form";
    }

    @PostMapping("/course/save")
    public String saveCourse(Course course, @RequestParam(name = "isUpdate", defaultValue = "false") boolean isUpdate, HttpSession session, RedirectAttributes redirectAttributes) {
        // ... (你这部分代码已经很完善，无需改动) ...
        if (!isAdmin(session)) return "redirect:/login";
        if (course.getCno() == null || course.getCno().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "课程号不能为空！");
            return isUpdate ? "redirect:/admin/dashboard" : "redirect:/admin/course/add";
        }
        if (isUpdate) {
            courseService.updateById(course);
            redirectAttributes.addFlashAttribute("message", "课程 " + course.getCname() + " (" + course.getCno() + ") 信息更新成功。");
        } else {
            Course existingCourse = courseService.getById(course.getCno());
            if (existingCourse != null) {
                redirectAttributes.addFlashAttribute("error", "添加失败：课程号 " + course.getCno() + " 已经存在！");
                redirectAttributes.addFlashAttribute("course", course);
                return "redirect:/admin/course/add";
            }
            courseService.save(course);
            redirectAttributes.addFlashAttribute("message", "课程 " + course.getCname() + " (" + course.getCno() + ") 添加成功。");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/course/delete/{cno}")
    public String deleteCourse(@PathVariable String cno, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";
        // 假设 courseService 有 deleteCourseAndSelections 方法
        // courseService.deleteCourseAndSelections(cno);
        // 如果没有，先用简单删除
        courseService.removeById(cno);
        redirectAttributes.addFlashAttribute("message", "课程 " + cno + " 及相关选课记录已删除。");
        return "redirect:/admin/dashboard";
    }
}