package net.orderzone.idcard.service;

import net.orderzone.idcard.model.ProfileType;
import net.orderzone.idcard.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class IdGeneratorService {

    private final ProfileRepository profileRepository;

    public IdGeneratorService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /** Generates a unique registration number like 2026-ENG-014 */
    public String generateRegistrationNumber(ProfileType type, String department) {
        int year = LocalDate.now().getYear();
        String dept = (department != null && !department.isBlank())
                ? department.substring(0, Math.min(department.length(), 4)).toUpperCase()
                : type.name().substring(0, 3);
        long count = profileRepository.count() + 1;
        return String.format("%d-%s-%03d", year, dept, count);
    }

    public String generateUuid() {
        return UUID.randomUUID().toString();
    }
}