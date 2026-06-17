package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.ProfileRequest;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.repository.ProfileRepository;
import net.orderzone.idcard.repository.TemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final TemplateRepository templateRepository;
    private final PhotoStorageService photoStorage;
    private final IdGeneratorService idGenerator;

    public ProfileService(ProfileRepository profileRepository,
                          TemplateRepository templateRepository,
                          PhotoStorageService photoStorage,
                          IdGeneratorService idGenerator) {
        this.profileRepository = profileRepository;
        this.templateRepository = templateRepository;
        this.photoStorage = photoStorage;
        this.idGenerator = idGenerator;
    }

    public List<Profile> findAll() { return profileRepository.findAll(); }

    public Profile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + id));
    }

    public Profile findByUuid(String uuid) {
        return profileRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + uuid));
    }

    public Profile create(ProfileRequest req, MultipartFile photo) throws Exception {
        Template template = req.templateId() != null
                ? templateRepository.findById(req.templateId()).orElse(null)
                : null;

        Profile profile = Profile.builder()
                .uuid(idGenerator.generateUuid())
                .registrationNumber(idGenerator.generateRegistrationNumber(req.type(), req.department()))
                .type(req.type())
                .fullName(req.fullName())
                .department(req.department())
                .title(req.title())
                .email(req.email())
                .phone(req.phone())
                .bloodGroup(req.bloodGroup())
                .dateOfBirth(req.dateOfBirth())
                .expiryDate(req.expiryDate())
                .template(template)
                .barcodeType(req.barcodeType() != null ? req.barcodeType() : net.orderzone.idcard.model.BarcodeType.CODE_128)
                .build();

        if (photo != null && !photo.isEmpty()) {
            String filename = photoStorage.store(photo);
            profile.setPhotoFileName(filename);
            profile.setPhotoContentType(photo.getContentType());
        }

        return profileRepository.save(profile);
    }

    public Profile update(Long id, ProfileRequest req, MultipartFile photo) throws Exception {
        Profile profile = findById(id);
        profile.setFullName(req.fullName());
        profile.setType(req.type());
        profile.setDepartment(req.department());
        profile.setTitle(req.title());
        profile.setEmail(req.email());
        profile.setPhone(req.phone());
        profile.setBloodGroup(req.bloodGroup());
        profile.setDateOfBirth(req.dateOfBirth());
        profile.setExpiryDate(req.expiryDate());

        if (req.templateId() != null) {
            templateRepository.findById(req.templateId()).ifPresent(profile::setTemplate);
        }
        if (req.barcodeType() != null) profile.setBarcodeType(req.barcodeType());

        if (photo != null && !photo.isEmpty()) {
            photoStorage.delete(profile.getPhotoFileName());
            String filename = photoStorage.store(photo);
            profile.setPhotoFileName(filename);
            profile.setPhotoContentType(photo.getContentType());
        }

        return profileRepository.save(profile);
    }

    public void delete(Long id) throws Exception {
        Profile profile = findById(id);
        photoStorage.delete(profile.getPhotoFileName());
        profileRepository.deleteById(id);
    }

    public List<Profile> findByDepartment(String dept) {
        return profileRepository.findByDepartment(dept);
    }

    public List<Profile> search(String name) {
        return profileRepository.findByFullNameContainingIgnoreCase(name);
    }
}