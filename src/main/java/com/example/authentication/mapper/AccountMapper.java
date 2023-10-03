package com.example.authentication.mapper;

import com.example.authentication.dto.mq_dto.RegistrationDto;
import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(role)")
    @Mapping(target = "emailConfirmed", expression = "java(emailConfirmed)")
    @Mapping(target = "accountNonExpired", expression = "java(true)")
    @Mapping(target = "accountNonLocked", expression = "java(true)")
    @Mapping(target = "credentialsNonExpired", expression = "java(true)")
    @Mapping(target = "enabled", expression = "java(true)")
    Account mapRegisterRequestToAccount(RegisterRequest registerRequest, @Context Role role, @Context boolean emailConfirmed);

    @Mapping(target = "id", ignore = true)
    Account updateAccountFieldsFrom(UpdateDto updateDto, @MappingTarget Account accountToUpdate);

    @Mapping(target = "username", expression = "java(username)")
    RegistrationDto mapAccountToRegistrationDto(Account account, @Context String username);
}
