package com.example.demo.debug;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitData implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("USER").build());
            roleRepository.save(Role.builder().name("ADMIN").build());
        }
    }
}
