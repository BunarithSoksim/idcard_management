package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.ProfileRequest;
import net.orderzone.idcard.model.*;
import net.orderzone.idcard.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock ProfileRepository profileRepository;
    @Mock TemplateRepository templateRepository;
    @Mock PhotoStorageService photoStorage;
    @Mock IdGeneratorService idGenerator;

    @InjectMocks ProfileService profileService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void create_savesProfile() throws Exception {
        when(idGenerator.generateUuid()).thenReturn(UUID.randomUUID().toString());
        when(idGenerator.generateRegistrationNumber(any(), any())).thenReturn("2026-ENG-001");

        Profile saved = Profile.builder()
                .id(1L).uuid("test-uuid").registrationNumber("2026-ENG-001")
                .type(ProfileType.STUDENT).fullName("Alice").build();
        when(profileRepository.save(any())).thenReturn(saved);

        ProfileRequest req = new ProfileRequest(
                "Alice", ProfileType.STUDENT, "Engineering",
                "Student", null, null, null, null, null, null, null);

        Profile result = profileService.create(req, null);
        assertThat(result.getFullName()).isEqualTo("Alice");
        verify(profileRepository).save(any());
    }

    @Test
    void findById_returnsProfile() {
        Profile p = Profile.builder().id(1L).fullName("Bob")
                .uuid("u").registrationNumber("R").type(ProfileType.EMPLOYEE).build();
        when(profileRepository.findById(1L)).thenReturn(Optional.of(p));
        assertThat(profileService.findById(1L).getFullName()).isEqualTo("Bob");
    }

    @Test
    void delete_callsRepository() throws Exception {
        Profile p = Profile.builder().id(1L).fullName("Carol")
                .uuid("u").registrationNumber("R").type(ProfileType.USER).build();
        when(profileRepository.findById(1L)).thenReturn(Optional.of(p));
        profileService.delete(1L);
        verify(profileRepository).deleteById(1L);
    }
}