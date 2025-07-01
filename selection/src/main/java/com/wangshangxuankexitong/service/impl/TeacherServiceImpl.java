package com.wangshangxuankexitong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangshangxuankexitong.dao.SelectionDao;
import com.wangshangxuankexitong.dao.StudentDao;
import com.wangshangxuankexitong.dao.TeacherDao;
import com.wangshangxuankexitong.entity.*;
import com.wangshangxuankexitong.service.CourseService;
import com.wangshangxuankexitong.service.TeacherService;
import com.wangshangxuankexitong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherDao, Teacher> implements TeacherService {
    @Autowired
    private CourseService courseService;
    @Autowired
    private SelectionDao selectionDao;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private UserService userService;
    // 在 TeacherServiceImpl.java 中


    @Override
    @Transactional // 关键！保证操作的原子性，要么都成功，要么都失败
    public void deleteTeacherAndHandleCourses(String tno) throws Exception {
        // 1. 检查该教师是否还有关联的课程
        long courseCount = courseService.count(new QueryWrapper<Course>().eq("tno_fk", tno));

        if (courseCount > 0) {
            // 如果还有课程，不允许删除，并抛出明确的业务异常
            throw new Exception("删除失败：该教师名下还有 " + courseCount + " 门课程，请先处理这些课程！");
        }

        // 2. 如果没有关联课程，才执行删除操作
        // 2.1 删除 teacher 表记录
        this.removeById(tno);

        // 2.2 删除对应的 user 表记录
        User userToDelete = userService.findByUsername(tno); // 假设 userService 已注入
        if (userToDelete != null) {
            userService.removeById(userToDelete.getId());
        }
    }

    @Override
    public List<Course> getCoursesByTeacher(String tno) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("tno_fk", tno);
        return courseService.list(wrapper);
    }

    @Override
    public List<Student> getEnrolledStudents(String cno) {
        QueryWrapper<Selection> selectionWrapper = new QueryWrapper<>();
        selectionWrapper.eq("cno_fk", cno);
        List<Selection> selections = selectionDao.selectList(selectionWrapper);

        // 防御：如果选课记录本身就为 null 或空，直接返回空列表
        if (selections == null || selections.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> snos = selections.stream()
                .map(Selection::getSnoFk)
                .collect(Collectors.toList());

        // 再次防御：如果提取出的学生ID列表为空，也直接返回空列表
        if (snos.isEmpty()) {
            return new ArrayList<>();
        }

        // 调用批量查询
        List<Student> students = studentDao.selectBatchIds(snos);

        // 【最终防御】 这是最关键的一步！
        // 检查批量查询的结果，如果为 null，则返回一个安全的空列表
        if (students == null) {
            return new ArrayList<>();
        }

        return students;
    }
}