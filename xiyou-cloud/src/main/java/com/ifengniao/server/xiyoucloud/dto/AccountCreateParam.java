package com.ifengniao.server.xiyoucloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class AccountCreateParam {
    private String department;
    private String level;
    private String employeeName;
    private String accountName;
    private String password;
    private String remark;
}
