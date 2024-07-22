package antifraud.form;

import antifraud.enums.Operation;

public class AccessForm {
    private String username;
    private Operation operation;

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getUsername() {
        return username;
    }

    public Operation getOperation() {
        return operation;
    }

    public AccessForm(String username, Operation operation) {
        this.username = username;
        this.operation = operation;
    }
}
