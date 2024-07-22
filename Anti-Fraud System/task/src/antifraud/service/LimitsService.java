package antifraud.service;

import antifraud.domain.Limits;
import antifraud.repository.LimitsRepository;
import org.springframework.stereotype.Service;

@Service
public class LimitsService {
    private final LimitsRepository limitsRepository;

    public LimitsService(LimitsRepository limitsRepository) {
        this.limitsRepository = limitsRepository;
    }

    public void processLimits(String number, Long amount, String result, String feedback) {
        if (limitsRepository.findByNumberOfCard(number).isPresent()) {
            Limits limits = limitsRepository.findByNumberOfCard(number).get();
            switch (feedback) {
                case "ALLOWED":
                    if (result.equals("MANUAL_PROCESSING")) {
                        limits.setMaxAllowed(increaseLimit(limits.getMaxAllowed(), amount));
                    } else if (result.equals("PROHIBITED")) {
                        limits.setMaxManual(increaseLimit(limits.getMaxManual(), amount));
                        limits.setMaxAllowed(increaseLimit(limits.getMaxAllowed(), amount));
                    }
                    break;
                case "MANUAL_PROCESSING":
                    if (result.equals("ALLOWED")) {
                        limits.setMaxAllowed(decreaseLimit(limits.getMaxAllowed(), amount));
                    } else if (result.equals("PROHIBITED")) {
                        limits.setMaxManual(increaseLimit(limits.getMaxManual(), amount));
                    }
                    break;
                case "PROHIBITED":
                    if (result.equals("ALLOWED")) {
                        limits.setMaxAllowed(decreaseLimit(limits.getMaxAllowed(), amount));
                        limits.setMaxManual(decreaseLimit(limits.getMaxManual(), amount));
                    } else if (result.equals("MANUAL_PROCESSING")) {
                        limits.setMaxManual(decreaseLimit(limits.getMaxManual(), amount));
                    }
                    break;
            }
            limitsRepository.save(limits);
        }
    }

    private long increaseLimit(long currentLimit, long amount) {
        return (long) Math.ceil(0.8 * currentLimit + 0.2 * amount);
    }

    private long decreaseLimit(long currentLimit, long amount) {
        return (long) Math.ceil(0.8 * currentLimit - 0.2 * amount);
    }

    public int validateAmount(Long amount, String number) {
        if (limitsRepository.findByNumberOfCard(number).isEmpty()) {
            Limits amountLimits = new Limits(number, 200L, 1500L);
            limitsRepository.save(amountLimits);
        }
        Limits limits = limitsRepository.findByNumberOfCard(number).get();
        Long maxAllowed = limits.getMaxAllowed();
        Long maxManual = limits.getMaxManual();
        if (amount < 1) {
            return 1;
        } else if (amount > maxManual) {
            return 2;
        } else if (amount > maxAllowed) {
            return 3;
        }
        return 4;
    }
}
