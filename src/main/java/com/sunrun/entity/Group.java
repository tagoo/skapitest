package com.sunrun.entity;

import lombok.*;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Group implements Serializable {

    private static final long serialVersionUID = 1483641648386124108L;
    @NonNull
    private String name;
    private String description;
    private List<String> admins;
    private List<String> members;

    public Group(String name) {
        this.name = name;
    }
}
