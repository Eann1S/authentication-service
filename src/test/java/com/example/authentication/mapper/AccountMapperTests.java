package com.example.authentication.mapper;

import com.example.authentication.dto.mq_dto.RegistrationDto;
import com.example.authentication.dto.mq_dto.UpdateDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.mapper.qualifier.password.AccountPasswordQualifier;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class AccountMapperTests {

    @Mock
    private AccountPasswordQualifier accountPasswordQualifier;
    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapperImpl(accountPasswordQualifier);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldMapRegisterRequestToAccount(RegisterRequest registerRequest) {
        when(accountPasswordQualifier.encodePassword(registerRequest.password()))
                .then(returnsFirstArg());

        Account actualAccount = accountMapper.mapRegisterRequestToAccount(registerRequest, Role.USER);

        assertThat(actualAccount)
                .extracting(Account::getEmail, Account::getPassword, Account::getAuthorities)
                .containsExactly(registerRequest.email(), registerRequest.password(), List.of(Role.USER));
    }

    @ParameterizedTest
    @InstancioSource
    void shouldUpdateAccountFromUpdateDto(Account account, UpdateDto updateDto) {
        Account updatedAccount = accountMapper.updateAccountFieldsFrom(updateDto, account);

        assertThat(updatedAccount.getEmail())
                .isEqualTo(updateDto.email());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldMapAccountToRegistrationDto(Account account, String username) {
        RegistrationDto registrationDto = accountMapper.mapAccountToRegistrationDto(account, username);

        assertThat(registrationDto)
                .extracting(RegistrationDto::id, RegistrationDto::email, RegistrationDto::username)
                .containsExactly(account.getId(), account.getEmail(), username);
    }
}