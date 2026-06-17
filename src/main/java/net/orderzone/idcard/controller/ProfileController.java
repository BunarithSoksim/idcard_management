package net.orderzone.idcard.controller;

import jakarta.validation.Valid;
import net.orderzone.idcard.dto.ProfileRequest;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) { this.service = service; }

    @GetMapping
    public List<Profile> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Profile getById(@PathVariable Long id) { return service.findById(id); }

    @GetMapping("/uuid/{uuid}")
    public Profile getByUuid(@PathVariable String uuid) { return service.findByUuid(uuid); }

    @GetMapping("/search")
    public List<Profile> search(@RequestParam String name) { return service.search(name); }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Profile> create(
            @Valid @RequestPart("profile") ProfileRequest req,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        return ResponseEntity.ok(service.create(req, photo));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Profile> update(
            @PathVariable Long id,
            @Valid @RequestPart("profile") ProfileRequest req,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        return ResponseEntity.ok(service.update(id, req, photo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}