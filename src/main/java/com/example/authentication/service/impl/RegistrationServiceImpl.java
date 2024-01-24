package com.example.authentication.service.impl;

import com.example.authentication.dto.mq_dto.RegistrationDto;
import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.entity.Role;
import com.example.authentication.exception.AccountAlreadyExistsException;
import com.example.authentication.mapper.AccountMapper;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.ConfirmationCodeSendingService;
import com.example.authentication.service.RegistrationService;
import com.example.authentication.service.UserMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final AccountService accountService;
    private final UserMessagingService userMessagingService;
    private final AccountMapper accountMapper;
    private final ConfirmationCodeSendingService confirmationCodeSendingService;

    @Override
    public void register(RegisterRequest registerRequest) {
        throwExceptionIfAccountWithGivenEmailAlreadyExists(registerRequest.email());
        Account account = accountService.createAccountFrom(registerRequest, Role.USER);
        confirmationCodeSendingService.sendConfirmationCodeForAccount(account);
        RegistrationDto registrationDto = accountMapper.mapAccountToRegistrationDto(account, registerRequest.username());
        userMessagingService.send(registrationDto);
    }

    private void throwExceptionIfAccountWithGivenEmailAlreadyExists(String email) {
        if (accountService.doesAccountExistsWithEmail(email)) {
            throw new AccountAlreadyExistsException(email);
        }
    }
}
