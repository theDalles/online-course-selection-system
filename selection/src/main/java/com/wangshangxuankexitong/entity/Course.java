package com.wangshangxuankexitong.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course")
public class Course {
    @TableId
    private String cno;
    private String cname;
    private Double credit;
    @TableField("tno_fk")
    private String tnoFk;

    // 非数据库字段，用于显示教师姓名
    @TableField(exist = false)
    private String teacherName;
}