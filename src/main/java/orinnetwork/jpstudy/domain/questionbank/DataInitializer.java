package orinnetwork.jpstudy.domain.questionbank;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SectionRepository sectionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (sectionRepository.count() == 0) {
            Section langKnowledge = Section.builder().name("언어지식(문자・어휘・문법)").build();
            Section reading = Section.builder().name("독해").build();
            Section listening = Section.builder().name("청해").build();

            sectionRepository.saveAll(List.of(langKnowledge, reading, listening));
        }
    }
}