// 文件名: Selection.java (这是正确的)
package com.wangshangxuankexitong.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("selection")
public class Selection {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("sno_fk")
    private String snoFk;

    @TableField("cno_fk")
    private String cnoFk;
}