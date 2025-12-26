package com.ifengniao.server.xiyoucloud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class AccountDTO {
    private Integer userId;
    private String department;
    private String level;
    private String employeeName;
    private String accountName;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public AccountDTO(UserEntity userEntity) {
        this.userId = userEntity.getUserId();
        this.department = userEntity.getUserDepartment();
        this.level = userEntity.getUserLevel();
        this.employeeName = userEntity.getEmployeeName();
        this.accountName = userEntity.getUserName();
        this.remark = userEntity.getUserRemark();
        this.createTime = userEntity.getCreateTime();
    }
}
