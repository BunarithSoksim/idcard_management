package net.orderzone.idcard.repository;

import net.orderzone.idcard.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProfileRepositoryTest {

    @Autowired ProfileRepository profileRepository;

    private Profile buildProfile(String name, String regNum) {
        return Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .registrationNumber(regNum)
                .type(ProfileType.STUDENT)
                .fullName(name)
                .department("Engineering")
                .build();
    }

    @Test
    void saveAndFindById() {
        Profile saved = profileRepository.save(buildProfile("Alice", "2026-ENG-001"));
        assertThat(profileRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findByUuid() {
        Profile p = profileRepository.save(buildProfile("Bob", "2026-ENG-002"));
        assertThat(profileRepository.findByUuid(p.getUuid())).isPresent();
    }

    @Test
    void existsByRegistrationNumber() {
        profileRepository.save(buildProfile("Carol", "2026-ENG-003"));
        assertThat(profileRepository.existsByRegistrationNumber("2026-ENG-003")).isTrue();
        assertThat(profileRepository.existsByRegistrationNumber("9999-XXX-000")).isFalse();
    }

    @Test
    void findByType() {
        profileRepository.save(buildProfile("Dave", "2026-ENG-004"));
        assertThat(profileRepository.findByType(ProfileType.STUDENT)).isNotEmpty();
    }

    @Test
    void searchByName() {
        profileRepository.save(buildProfile("Eve Smith", "2026-ENG-005"));
        assertThat(profileRepository.findByFullNameContainingIgnoreCase("eve")).isNotEmpty();
    }

    @Test
    void deleteProfile() {
        Profile p = profileRepository.save(buildProfile("Frank", "2026-ENG-006"));
        profileRepository.deleteById(p.getId());
        assertThat(profileRepository.findById(p.getId())).isEmpty();
    }
}