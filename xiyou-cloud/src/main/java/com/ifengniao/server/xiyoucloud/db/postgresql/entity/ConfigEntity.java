package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "xy_config", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ConfigEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "config_id")
    private Integer configId;
    @Basic
    @Column(name = "config_type")
    private String configType;
    @Basic
    @Column(name = "config_name")
    private String configName;
    @Basic
    @Column(name = "config_value")
    private Short configValue;
    @Basic
    @Column(name = "config_extra")
    private String configExtra;
    @Basic
    @Column(name = "config_order")
    private Short configOrder;
    @Basic
    @Column(name = "config_desc")
    private String configDesc;

}
