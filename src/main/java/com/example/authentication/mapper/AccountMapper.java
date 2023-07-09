package com.example.authentication.mapper;

import com.example.authentication.dto.request.EmailRegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.password()))")
    @Mapping(target = "role", expression = "java(role)")
    @Mapping(target = "isEmailConfirmed", expression = "java(isEmailConfirmed)")
    @Mapping(target = "isAccountNonExpired", expression = "java(true)")
    @Mapping(target = "isAccountNonLocked", expression = "java(true)")
    @Mapping(target = "isCredentialsNonExpired", expression = "java(true)")
    @Mapping(target = "isEnabled", expression = "java(true)")
    Account toEntity(EmailRegisterRequest request, @Context PasswordEncoder passwordEncoder, @Context Role role, @Context boolean isEmailConfirmed);

    Account updateAccount(String email, @MappingTarget Account accountToUpdate);
}
