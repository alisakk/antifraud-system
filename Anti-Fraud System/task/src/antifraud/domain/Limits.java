package antifraud.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Limits {
    @Id
    private String numberOfCard;

    private Long maxAllowed;

    private Long maxManual;

    public Limits() {

    }

    public Limits(String numberOfCard, Long maxAllowed, Long maxManual) {
        this.numberOfCard = numberOfCard;
        this.maxAllowed = maxAllowed;
        this.maxManual = maxManual;
    }

    public void setNumberOfCard(String numberOfCard) {
        this.numberOfCard = numberOfCard;
    }

    public void setMaxAllowed(Long maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public void setMaxManual(Long maxManual) {
        this.maxManual = maxManual;
    }

    public String getNumberOfCard() {
        return numberOfCard;
    }

    public Long getMaxAllowed() {
        return maxAllowed;
    }

    public Long getMaxManual() {
        return maxManual;
    }
}
