package com.wangshangxuankexitong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangshangxuankexitong.entity.Course;
import com.wangshangxuankexitong.entity.Student;
import com.wangshangxuankexitong.entity.Teacher;

import java.util.List;

public interface TeacherService extends IService<Teacher> {
    List<Course> getCoursesByTeacher(String tno);
    List<Student> getEnrolledStudents(String cno);
    void deleteTeacherAndHandleCourses(String tno) throws Exception;

}