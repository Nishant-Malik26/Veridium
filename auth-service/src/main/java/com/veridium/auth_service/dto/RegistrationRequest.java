package com.veridium.auth_service.dto;


public record RegistrationRequest(String companyName, String email, String password, String firstName,
                                  String lastName) {
}
