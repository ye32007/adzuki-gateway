package com.adzuki.gateway.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "api_config")
public class ApiConfig implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api")
    private String api;

    @Column(name = "version")
    private String version;

    @Column(name = "dubbo_service_name")
    private String dubboServiceName;

    @Column(name = "dubbo_service_version")
    private String dubboServiceVersion;

    @Column(name = "dubbo_service_method")
    private String dubboServiceMethod;

    @Column(name = "param_class")
    private String paramClass;

    @Column(name = "need_login")
    private Integer needLogin;

    @Column(name = "owner")
    private String owner;

    @Column(name = "description")
    private String description;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}