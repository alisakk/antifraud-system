package antifraud.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull
    private String ip;

    @NotNull
    private Long amount;

    @NotNull
    private String number;

    @NotNull
    private LocalDateTime date;

    private String region;

    private String result;

    String feedback;

    public Transaction() {
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public Transaction(Long id) {
        this.transactionId = id;
    }

    public Transaction(String ip, Long amount, String number, String region, LocalDateTime date, String result) {
        this.ip = ip;
        this.amount = amount;
        this.number = number;
        this.region = region;
        this.date = date;
        this.result = result;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIp() {
        return ip;
    }

    public String getNumber() {
        return number;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }
}
