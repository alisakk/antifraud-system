package antifraud.service;

import antifraud.domain.Feedback;
import antifraud.domain.Transaction;
import antifraud.form.ResultForm;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import antifraud.enums.Region;
import antifraud.enums.ResultEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final StolenCardRepository stolenCardRepository;
    private final SuspiciousIpRepository suspiciousIpRepository;
    private final StolenCardService stolenCardService;
    private final SuspiciousIpService suspiciousIpService;
    private final LimitsService limitsService;

    public TransactionService(TransactionRepository transactionRepository, StolenCardRepository stolenCardRepository,
                              SuspiciousIpRepository suspiciousIpRepository, StolenCardService stolenCardService,
                              SuspiciousIpService suspiciousIpService, LimitsService limitsService) {
        this.transactionRepository = transactionRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.stolenCardService = stolenCardService;
        this.suspiciousIpService = suspiciousIpService;
        this.limitsService = limitsService;
    }

    private HttpStatus status;
    private ResultEnum resultEnum;
    private List<String> errors;

    public ResponseEntity<ResultForm> checkTransaction(Transaction transaction) {
        try {
            Region.valueOf(transaction.getRegion());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (stolenCardService.checkInvalidity(transaction.getNumber()) || suspiciousIpService.checkInvalidity(transaction.getIp())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        status = HttpStatus.OK;
        resultEnum = ResultEnum.PROHIBITED;
        errors = new LinkedList<>();
        validateRegionAndIp(transaction, transaction.getRegion());
        int res = limitsService.validateAmount(transaction.getAmount(), transaction.getNumber());
        validateAmount(res);
        transactionRepository.save(new Transaction(transaction.getIp(), transaction.getAmount(), transaction.getNumber(), transaction.getRegion(), transaction.getDate(), resultEnum.toString()));
        return new ResponseEntity<>(new ResultForm(resultEnum, getErrorInfo()), status);
    }

    private void validateRegionAndIp(Transaction transaction, String transactionRegion) {
        if (suspiciousIpRepository.findByIp(transaction.getIp()).isPresent()) {
            resultEnum = ResultEnum.PROHIBITED;
            errors.add("ip");
        }
        if (stolenCardRepository.findByNumber(transaction.getNumber()).isPresent()) {
            resultEnum = ResultEnum.PROHIBITED;
            errors.add("card-number");
        }
        LocalDateTime date = transaction.getDate();
        List<Transaction> transactions = transactionRepository.findByNumberAndDateBetween(transaction.getNumber(), date.minusHours(1), date);
        HashSet<String> regions = transactions.stream().map(Transaction::getRegion).collect(Collectors.toCollection(HashSet::new));
        regions.add(transactionRegion);
        HashSet<String> ips = transactions.stream().map(Transaction::getIp).collect(Collectors.toCollection(HashSet::new));
        ips.add(transaction.getIp());
        if (regions.size() == 3) {
            resultEnum = ResultEnum.MANUAL_PROCESSING;
            errors.add("region-correlation");
        }
        if (ips.size() == 3) {
            resultEnum = ResultEnum.MANUAL_PROCESSING;
            errors.add("ip-correlation");
        }
        if (regions.size() > 3) {
            resultEnum = ResultEnum.PROHIBITED;
            errors.add("region-correlation");
        }
        if (ips.size() > 3) {
            resultEnum = ResultEnum.PROHIBITED;
            errors.add("ip-correlation");
        }
    }

    private void validateAmount(int res) {
        if (res == 1) {
            errors.add("amount");
            status = HttpStatus.BAD_REQUEST;
        } else if (res == 2) {
            errors.add("amount");
        } else if (res == 3 && errors.size() < 1) {
            resultEnum = ResultEnum.MANUAL_PROCESSING;
            errors.add("amount");
        } else if (errors.size() < 1) {
            resultEnum = ResultEnum.ALLOWED;
            errors.add("none");
        }
    }

    private String getErrorInfo() {
        StringBuilder info = new StringBuilder();
        errors.sort((String::compareToIgnoreCase));
        info.append(errors.get(0));
        for (int i = 1; i < errors.size(); i++) {
            info.append(", ").append(errors.get(i));
        }
        return info.toString();
    }

    public ResponseEntity<Transaction> addFeedback(Feedback feedback) {
        if (transactionRepository.findById(feedback.getTransactionId()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Transaction transaction = transactionRepository.findById(feedback.getTransactionId()).get();
        try {
            ResultEnum.valueOf(feedback.getFeedback());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (transaction.getFeedback() != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (transaction.getResult().equals(feedback.getFeedback())) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        limitsService.processLimits(transaction.getNumber(), transaction.getAmount(), transaction.getResult(), feedback.getFeedback());
        transaction.setFeedback(feedback.getFeedback());
        transactionRepository.save(transaction);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    public List<Transaction> showHistory() {
        return transactionRepository.findAll();
    }

    public ResponseEntity<Transaction> showCardHistory(String number) {
        if (stolenCardService.checkInvalidity(number)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (transactionRepository.findByNumber(number).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Transaction transaction = transactionRepository.findByNumber(number).get();
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}
