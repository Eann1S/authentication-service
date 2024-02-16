package test_util;

import com.example.authentication.dto.request.RegisterRequest;
import com.example.authentication.entity.Account;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.AccountService;
import com.example.authentication.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class IntegrationTestAccountUtil {

    private final RegisterService registerService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public void registerAccount(Account account) {
        RegisterRequest request = RegisterRequest.of(account.getEmail(), account.getUsername(), account.getPassword());
        registerService.register(request);
    }

    public void enableAccountByEmail(String email) {
        Account account = accountService.findAccountByEmailInDatabase(email);
        accountService.enableAccount(account);
    }

    public Account saveAccountToDatabase(Account account) {
        return accountRepository.save(account);
    }
}
