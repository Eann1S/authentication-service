package test_util;

import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class IntegrationTestAccountUtil {

    private final RegistrationService registrationService;
    private final AccountRepository accountRepository;

    public void registerAccount(Account account) {
        RegisterRequest request = RegisterRequest.of(account.getEmail(), account.getUsername(), account.getPassword());
        registrationService.register(request);
    }

    public Account saveAccountToDatabase(Account account) {
        return accountRepository.save(account);
    }
}
