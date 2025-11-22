package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "xy_user", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private Integer userId;
    @Basic
    @Column(name = "user_name")
    private String userName;
    @Basic
    @Column(name = "user_pass")
    private String userPass;
    @Basic
    @Column(name = "user_email")
    private String userEmail;
    @Basic
    @Column(name = "user_phone")
    private String userPhone;

}
