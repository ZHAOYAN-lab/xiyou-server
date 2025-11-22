package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserDTO {
    private Integer userId;
    private String userName;
    private String userPass;
    private String userEmail;
    private String userPhone;

    private String mqttUrl;
    private String mqttUsername;
    private String mqttPassword;
    private String mqttTopicPrefix;

    public static List<UserDTO> covert(Collection<UserEntity> list) {
        List<UserDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new UserDTO(source));
            }
        }
        return result;
    }

    public UserDTO(UserEntity userEntity) {
        this.userId = userEntity.getUserId();
        this.userName = userEntity.getUserName();
        this.userPass = null;
        this.userEmail = userEntity.getUserEmail();
        this.userPhone = userEntity.getUserPhone();
    }
}
