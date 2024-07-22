package antifraud.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private Long transactionId;

    private String feedback;

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getFeedback() {
        return feedback;
    }
}
