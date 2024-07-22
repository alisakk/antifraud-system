package antifraud.service;

import antifraud.domain.StolenCard;
import antifraud.repository.StolenCardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StolenCardService {
    private final StolenCardRepository stolenCardRepository;

    public StolenCardService(StolenCardRepository stolenCardRepository) {
        this.stolenCardRepository = stolenCardRepository;
    }

    public ResponseEntity<StolenCard> saveStolenCardData(StolenCard stolenCard) {
        if (checkInvalidity(stolenCard.getNumber())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (stolenCardRepository.findByNumber(stolenCard.getNumber()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        stolenCardRepository.save(stolenCard);
        return new ResponseEntity<>(stolenCard, HttpStatus.OK);
    }

    public boolean checkInvalidity(String number) {
        int[] array = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            array[i] = number.charAt(i) - 48;
        }
        int checksum = array[array.length - 1];
        array[array.length - 1] = 0;
        for (int i = 0; i < array.length; i++) {
            if ((i + 1) % 2 != 0) {
                array[i] *= 2;
            }
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 9) {
                array[i] -= 9;
            }
        }
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        return (sum + checksum) % 10 != 0;
    }

    public ResponseEntity<Map<String, String>> deleteCardNumber(String number) {
        if (checkInvalidity(number)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (stolenCardRepository.findByNumber(number).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Map<String, String> map = new HashMap<>();
        map.put("status", "Card " + number + " successfully removed!");
        StolenCard stolenCard = stolenCardRepository.findByNumber(number).get();
        stolenCardRepository.delete(stolenCard);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public List<StolenCard> showCardNumbers() {
        return stolenCardRepository.findAll();
    }
}
