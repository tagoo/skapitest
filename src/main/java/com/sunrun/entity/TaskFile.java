package com.sunrun.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Entity
@Table(name = "tbTaskFile")
@Getter
@Setter
@ToString
public class TaskFile implements Serializable{
    private static final long serialVersionUID = -7095297964191537022L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "taskJID")
    private String taskJID;
    private String userName;
    private String fileName;
    private String fileSize;
    private String fileType;
    private String fileUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
