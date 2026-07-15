package com.veridium.auth_service.utils;

import com.veridium.auth_service.repository.TenantRepository;
import org.springframework.stereotype.Component;

@Component
public class GenerateSlug {

    private final TenantRepository tenantRepository;

    public GenerateSlug(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    private String generateBaseSlug(String companyName) {
        return companyName.trim()
                          .toLowerCase()
                          .replaceAll("[^a-z0-9\\s-]", "")
                          .replaceAll("\\s+", "-")
                          .replaceAll("-+", "-");
    }

    public String generateUniqueSlug(String companyName) {
        String base = generateBaseSlug(companyName);

        String slug = base;
        int counter = 2;

        while (tenantRepository.existsBySlug(slug)) {
            slug = base + "-" + counter++;
        }

        return slug;
    }
}