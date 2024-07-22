package antifraud.form;

import antifraud.enums.ResultEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResultForm {

    @JsonIgnore
    private String status;

    private ResultEnum result;

    private String info;

    public ResultForm(ResultEnum result, String info) {
        this.result = result;
        this.info = info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public ResultEnum getResult() {
        return result;
    }

    public void setResult(ResultEnum result) {
        this.result = result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
