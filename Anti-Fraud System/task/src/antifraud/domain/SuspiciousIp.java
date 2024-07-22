package antifraud.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class SuspiciousIp {

    @NotNull
    @NotEmpty
    private String ip;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public Long getId() {
        return id;
    }
}
