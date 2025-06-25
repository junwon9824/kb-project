package com.example.demo.debug;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.stereotype.Component;

@Component
public class EntityDebugger {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @PostConstruct
    public void printEntities() {
        System.out.println("=== Hibernate Mapped Entities ===");
        emf.getMetamodel().getEntities().forEach(e ->
                System.out.println("Mapped entity: " + e.getJavaType().getName()));
    }
}
