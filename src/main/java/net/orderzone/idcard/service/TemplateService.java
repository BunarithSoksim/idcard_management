package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.TemplateRequest;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    private final TemplateRepository repo;

    public TemplateService(TemplateRepository repo) { this.repo = repo; }

    public List<Template> findAll() { return repo.findAll(); }

    public Template findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Template not found: " + id));
    }

    public Template create(TemplateRequest req) {
        if (repo.existsByCode(req.code())) throw new RuntimeException("Code already exists: " + req.code());
        Template t = Template.builder()
                .code(req.code())
                .name(req.name())
                .organizationName(req.organizationName())
                .layout(req.layout() != null ? req.layout() : "VERTICAL")
                .primaryColor(req.primaryColor() != null ? req.primaryColor() : "#1d4ed8")
                .secondaryColor(req.secondaryColor() != null ? req.secondaryColor() : "#e0e7ff")
                .textColor(req.textColor() != null ? req.textColor() : "#111827")
                .tagline(req.tagline())
                .build();
        return repo.save(t);
    }

    public Template update(Long id, TemplateRequest req) {
        Template t = findById(id);
        t.setName(req.name());
        t.setOrganizationName(req.organizationName());
        if (req.layout() != null) t.setLayout(req.layout());
        if (req.primaryColor() != null) t.setPrimaryColor(req.primaryColor());
        if (req.secondaryColor() != null) t.setSecondaryColor(req.secondaryColor());
        if (req.textColor() != null) t.setTextColor(req.textColor());
        t.setTagline(req.tagline());
        return repo.save(t);
    }

    public void delete(Long id) { repo.deleteById(id); }
}