package com.wangshangxuankexitong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangshangxuankexitong.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> {
    List<Course> listAllWithTeacherName();
    boolean deleteCourseAndSelections(String cno);
    List<Course> findCoursesByTeacherTno(String tno);

}