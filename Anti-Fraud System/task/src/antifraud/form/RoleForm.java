package antifraud.form;

public class RoleForm {
    private String username;
    private String role;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public RoleForm(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
