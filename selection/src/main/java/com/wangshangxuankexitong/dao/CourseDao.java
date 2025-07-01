package com.wangshangxuankexitong.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangshangxuankexitong.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select; // 1. 确保导入了 @Select 注解

import java.util.List; // 2. 确保导入了 List

@Mapper
public interface CourseDao extends BaseMapper<Course> {

    // ================== 添加下面这个完整的方法 ==================
    /**
     * 自定义SQL：查询所有课程，并使用LEFT JOIN关联教师表以获取教师姓名。
     *
     * 关键点：使用 AS 关键字为 t.tname 创建一个别名 teacherName，
     * 这个别名必须和 Course 实体类中的属性名 (teacherName) 完全一致。
     *
     * @return 包含教师姓名的课程列表
     */
    @Select("SELECT c.*, t.tname AS teacherName FROM course c LEFT JOIN teacher t ON c.tno_fk = t.tno")
    List<Course> selectAllWithTeacherName();
    @Select("SELECT * FROM course WHERE tno_fk = #{tno}")
    List<Course> findCoursesByTeacherTno(String tno);


}