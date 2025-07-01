package com.wangshangxuankexitong.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student")
public class Student {
    @TableId
    private String sno;
    private String sname;
    private Integer sage;
    private String sgender;
    private String sdept;
}