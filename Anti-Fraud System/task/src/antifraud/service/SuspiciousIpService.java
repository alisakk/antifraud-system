package antifraud.service;

import antifraud.domain.SuspiciousIp;
import antifraud.repository.SuspiciousIpRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SuspiciousIpService {
    private final SuspiciousIpRepository suspiciousIpRepository;

    public SuspiciousIpService(SuspiciousIpRepository suspiciousIpRepository) {
        this.suspiciousIpRepository = suspiciousIpRepository;
    }

    public ResponseEntity<SuspiciousIp> saveSuspiciousIp(SuspiciousIp suspiciousIp) {
        if (checkInvalidity(suspiciousIp.getIp())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (suspiciousIpRepository.findByIp(suspiciousIp.getIp()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        suspiciousIpRepository.save(suspiciousIp);
        return new ResponseEntity<>(suspiciousIpRepository.findByIp(suspiciousIp.getIp()).get(), HttpStatus.OK);
    }

    public boolean checkInvalidity(String ip) {
        String[] ipSplitByDot = ip.split("\\.");
        if (ipSplitByDot.length != 4) {
            return true;
        }
        for (String s : ipSplitByDot) {
            int num = Integer.parseInt(s);
            if (num < 0 || num > 255) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<Map<String, String>> deleteIp(String ip) {
        if (checkInvalidity(ip)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (suspiciousIpRepository.findByIp(ip).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        SuspiciousIp suspiciousIp = suspiciousIpRepository.findByIp(ip).get();
        suspiciousIpRepository.delete(suspiciousIp);
        Map<String, String> map = new HashMap<>();
        map.put("status", "IP " + ip + " successfully removed!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public List<SuspiciousIp> showIp() {
        return suspiciousIpRepository.findAll();
    }
}
