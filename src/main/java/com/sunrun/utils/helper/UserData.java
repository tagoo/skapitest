package com.sunrun.utils.helper;

import com.sunrun.entity.Property;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@NoArgsConstructor
@Setter
public class UserData implements Serializable{

    private static final long serialVersionUID = 6270797158737863554L;

    private String username;
    @Getter
    private String password;
    @Getter
    private String email;
    @Getter
    private String name;
    @Getter
    private List<Property> properties;

    public String getUsername(){
        if (username != null) {
            int index = username.indexOf("_");
            if (index > -1) {
                return username.substring(0,index);
            }
        }
        return username;
    }
}
