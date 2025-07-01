package com.wangshangxuankexitong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangshangxuankexitong.dao.CourseDao;
import com.wangshangxuankexitong.dao.SelectionDao;
import com.wangshangxuankexitong.dao.StudentDao;
import com.wangshangxuankexitong.entity.Course;
import com.wangshangxuankexitong.entity.Selection;
import com.wangshangxuankexitong.entity.Student;
import com.wangshangxuankexitong.service.CourseService;
import com.wangshangxuankexitong.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentDao, Student> implements StudentService {

    @Autowired
    private SelectionDao selectionDao;

    @Autowired
    private CourseService courseService;

    @Override
    public List<Course> getSelectedCourses(String sno) {
        QueryWrapper<Selection> selectionWrapper = new QueryWrapper<>();
        selectionWrapper.eq("sno_fk", sno);
        List<String> selectedCnos = selectionDao.selectList(selectionWrapper)
                .stream()
                .map(Selection::getCnoFk)
                .collect(Collectors.toList());

        if (selectedCnos.isEmpty()) {
            // return Collections.emptyList(); // 方式1: 返回一个不可变的空列表
            return new ArrayList<>();     // 方式2: 返回一个可变的空列表 (更常见)
        }

        List<Course> courseList = courseService.listAllWithTeacherName();
        return courseList.stream()
                .filter(course -> selectedCnos.contains(course.getCno()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getAvailableCourses(String sno) {
        List<Course> allCourses = courseService.listAllWithTeacherName();
        List<Course> selectedCourses = getSelectedCourses(sno);
        List<String> selectedCnos = selectedCourses.stream().map(Course::getCno).collect(Collectors.toList());

        return allCourses.stream()
                .filter(course -> !selectedCnos.contains(course.getCno()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean selectCourse(String sno, String cno) {
        QueryWrapper<Selection> wrapper = new QueryWrapper<>();
        wrapper.eq("sno_fk", sno).eq("cno_fk", cno);
        if (selectionDao.selectCount(wrapper) > 0) {
            return false; // 已选过
        }
        Selection selection = new Selection();
        selection.setSnoFk(sno);
        selection.setCnoFk(cno);
        return selectionDao.insert(selection) > 0;
    }

    @Override
    public boolean dropCourse(String sno, String cno) {
        QueryWrapper<Selection> wrapper = new QueryWrapper<>();
        wrapper.eq("sno_fk", sno).eq("cno_fk", cno);
        return selectionDao.delete(wrapper) > 0;
    }
}