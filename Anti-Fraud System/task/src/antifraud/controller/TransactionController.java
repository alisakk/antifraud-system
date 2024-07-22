package antifraud.controller;

import antifraud.domain.Feedback;
import antifraud.domain.StolenCard;
import antifraud.domain.SuspiciousIp;
import antifraud.domain.Transaction;
import antifraud.form.ResultForm;
import antifraud.service.StolenCardService;
import antifraud.service.SuspiciousIpService;
import antifraud.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    private final TransactionService transactionService;
    private final SuspiciousIpService suspiciousIpService;
    private final StolenCardService stolenCardService;

    public TransactionController(TransactionService transactionService, SuspiciousIpService saveSuspiciousIp, StolenCardService stolenCardService) {
        this.transactionService = transactionService;
        this.suspiciousIpService = saveSuspiciousIp;
        this.stolenCardService = stolenCardService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<ResultForm> checkTransaction(@RequestBody @Valid Transaction transaction) {
        return transactionService.checkTransaction(transaction);
    }

    @PutMapping("/transaction")
    public ResponseEntity<Transaction> addFeedback(@RequestBody @Valid Feedback feedback) {
        return transactionService.addFeedback(feedback);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<SuspiciousIp> saveSuspiciousIp(@RequestBody @Valid SuspiciousIp suspiciousIp) {
        return suspiciousIpService.saveSuspiciousIp(suspiciousIp);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<Map<String, String>> deleteIp(@PathVariable String ip) {
        return suspiciousIpService.deleteIp(ip);
    }

    @GetMapping("/suspicious-ip")
    public List<SuspiciousIp> showIpAddresses() {
        return suspiciousIpService.showIp();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<StolenCard> saveStolenCard(@RequestBody @Valid StolenCard stolenCard) {
        return stolenCardService.saveStolenCardData(stolenCard);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<Map<String, String>> deleteCardNumber(@PathVariable String number) {
        return stolenCardService.deleteCardNumber(number);
    }

    @GetMapping("/stolencard")
    public List<StolenCard> showCardNumbers() {
        return stolenCardService.showCardNumbers();
    }

    @GetMapping("/history")
    public List<Transaction> showTransactionHistory() {
        return transactionService.showHistory();
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<Transaction> showCardHistory(@PathVariable String number) {
        return transactionService.showCardHistory(number);
    }
}
