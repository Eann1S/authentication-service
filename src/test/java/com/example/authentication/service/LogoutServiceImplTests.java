package com.example.authentication.service;

import com.example.authentication.entity.Account;
import com.example.authentication.service.impl.LogoutServiceImpl;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class LogoutServiceImplTests {

    @Mock
    private AccountService accountService;
    @Mock
    private JwtService jwtService;
    private LogoutServiceImpl logoutService;

    @BeforeEach
    void setUp() {
        logoutService = new LogoutServiceImpl(accountService, jwtService);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldLogout(Account account) {
        when(accountService.findAccountByEmailInDatabase(account.getEmail()))
                .thenReturn(account);

        logoutService.logout(account.getEmail());

        verify(jwtService).invalidateJwtOf(account);
    }
}