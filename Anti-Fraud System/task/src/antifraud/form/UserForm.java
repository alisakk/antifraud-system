package antifraud.form;

import antifraud.domain.User;
import antifraud.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserForm {
    Long id;
    String name;
    String username;
    @JsonProperty
    Role role;

    public UserForm(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRole();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
