package com.wangshangxuankexitong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangshangxuankexitong.entity.Course;
import com.wangshangxuankexitong.entity.Student;

import java.util.List;

public interface StudentService extends IService<Student> {
    List<Course> getSelectedCourses(String sno);
    List<Course> getAvailableCourses(String sno);
    boolean selectCourse(String sno, String cno);
    boolean dropCourse(String sno, String cno);
}