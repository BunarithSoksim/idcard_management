package net.orderzone.idcard.repository;

import net.orderzone.idcard.model.Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TemplateRepositoryTest {

    @Autowired TemplateRepository templateRepository;

    private Template buildTemplate(String code, String name) {
        return Template.builder().code(code).name(name)
                .primaryColor("#1d4ed8").secondaryColor("#e0e7ff").textColor("#111827").build();
    }

    @Test
    void saveAndFindByCode() {
        templateRepository.save(buildTemplate("BLUE_VERTICAL", "Blue Vertical"));
        assertThat(templateRepository.findByCode("BLUE_VERTICAL")).isPresent();
    }

    @Test
    void existsByCode() {
        templateRepository.save(buildTemplate("RED_CARD", "Red Card"));
        assertThat(templateRepository.existsByCode("RED_CARD")).isTrue();
        assertThat(templateRepository.existsByCode("GREEN_CARD")).isFalse();
    }

    @Test
    void findByLayout() {
        Template t = buildTemplate("HORIZ_CARD", "Horizontal Card");
        t.setLayout("HORIZONTAL");
        templateRepository.save(t);
        assertThat(templateRepository.findByLayout("HORIZONTAL")).isNotEmpty();
    }
}