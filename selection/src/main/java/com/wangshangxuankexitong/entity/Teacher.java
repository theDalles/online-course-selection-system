package com.wangshangxuankexitong.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teacher")
public class Teacher {
    @TableId
    private String tno;
    private String tname;
    private String tdept;
}