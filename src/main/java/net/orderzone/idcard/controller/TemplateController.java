package net.orderzone.idcard.controller;

import jakarta.validation.Valid;
import net.orderzone.idcard.dto.TemplateRequest;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;
    public TemplateController(TemplateService service) { this.service = service; }

    @GetMapping          public List<Template> getAll()          { return service.findAll(); }
    @GetMapping("/{id}") public Template       getById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public ResponseEntity<Template> create(@Valid @RequestBody TemplateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Template> update(@PathVariable Long id, @Valid @RequestBody TemplateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}