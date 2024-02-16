package com.example.authentication.mapper;

import com.example.authentication.dto.mq_dto.RegisterDto;
import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.mapper.qualifier.password.AccountPasswordQualifier;
import com.example.authentication.mapper.qualifier.password.EncodePassword;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = AccountPasswordQualifier.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password", qualifiedBy = EncodePassword.class)
    @Mapping(target = "role", expression = "java(role)")
    @Mapping(target = "enabled", expression = "java(false)")
    @Mapping(target = "accountNonExpired", expression = "java(true)")
    @Mapping(target = "accountNonLocked", expression = "java(true)")
    @Mapping(target = "credentialsNonExpired", expression = "java(true)")
    Account mapRegisterRequestToAccount(RegisterRequest registerRequest, @Context Role role);

    @Mapping(target = "id", ignore = true)
    Account updateAccountFieldsFrom(UpdateDto updateDto, @MappingTarget Account accountToUpdate);

    @Mapping(target = "username", expression = "java(username)")
    RegisterDto mapAccountToRegistrationDto(Account account, @Context String username);
}
