package com.wangshangxuankexitong.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangshangxuankexitong.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentDao extends BaseMapper<Student> {}