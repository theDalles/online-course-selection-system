package com.wangshangxuankexitong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangshangxuankexitong.dao.CourseDao;
import com.wangshangxuankexitong.dao.SelectionDao;
import com.wangshangxuankexitong.dao.TeacherDao;
import com.wangshangxuankexitong.entity.Course;
import com.wangshangxuankexitong.entity.Selection;
import com.wangshangxuankexitong.entity.Teacher;
import com.wangshangxuankexitong.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseDao, Course> implements CourseService {
    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private SelectionDao selectionDao;
    @Autowired
    private CourseDao courseDao;

    @Override
    public List<Course> listAllWithTeacherName() {
        List<Course> courseList = this.list();
        if (courseList.isEmpty()) {
            return courseList;
        }
        // 批量查询教师信息，提高效率
        List<String> tnos = courseList.stream().map(Course::getTnoFk).distinct().collect(Collectors.toList());
        Map<String, String> teacherNameMap = teacherDao.selectBatchIds(tnos)
                .stream()
                .collect(Collectors.toMap(Teacher::getTno, Teacher::getTname));

        // 为每门课程设置教师姓名
        courseList.forEach(course -> course.setTeacherName(teacherNameMap.get(course.getTnoFk())));

        return courseList;
    }
    @Override
    public List<Course> findCoursesByTeacherTno(String tno) {
        System.out.println("    -> 进入 CourseServiceImpl.findCoursesByTeacherTno, 查询的 TNO 是: " + tno);

        // 调用 DAO 方法
        List<Course> coursesFromDb = courseDao.findCoursesByTeacherTno(tno);

        // 【关键调试点】检查从 DAO 返回的值
        if (coursesFromDb == null) {
            System.out.println("    -> !!! 警告：courseDao.findCoursesByTeacherTno 方法返回了 null !!!");
            // 最佳实践：如果DAO返回null，Service层应该将其转换为空列表
            return new java.util.ArrayList<>();
        } else {
            System.out.println("    -> courseDao.findCoursesByTeacherTno 方法返回了一个列表，大小为: " + coursesFromDb.size());
        }

        System.out.println("    -> CourseServiceImpl.findCoursesByTeacherTno 方法准备返回列表。");
        return coursesFromDb;
    }
    @Override
    @Transactional // 保证原子性操作
    public boolean deleteCourseAndSelections(String cno) {
        // 1. 删除所有与该课程相关的选课记录
        QueryWrapper<Selection> wrapper = new QueryWrapper<>();
        wrapper.eq("cno_fk", cno);
        selectionDao.delete(wrapper);

        // 2. 删除课程本身
        return this.removeById(cno);
    }
}
