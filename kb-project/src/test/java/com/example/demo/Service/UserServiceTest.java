package com.example.demo.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.Bank;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "password";
        List<BankAccount> bankAccount = new ArrayList<>();

        bankAccount.add(BankAccount.builder()
                .bank(Bank.builder()
                        .bankname("shinhan")
                        .id(2L)
                        .build())
                .build());

        UserDto userDto = UserDto.builder()
                .password(password)
                .username("a")
                .userid("sdf")
                .bankAccounts(bankAccount)
                .phone("010111111")
                .disabled(true)
                .address("seoul")
                .clientSafeIp("123.222.222")
                .account_password("1234")
                .id(1L)
                .build();

        User newUser = User.builder()
                .username(username)
                .password(password)
                .build();

//        return User.builder().username(username).userid(userid).password(password).phone(phone).address(address)
//                .disabled(disabled).bankAccounts(bankAccounts).account_password(account_password).clientSafeIp(clientSafeIp).build();

        // Mock userRepository.save() 메소드 호출 시 반환할 객체 설정
//        when(userRepository.save(newUser)).thenReturn(newUser);

        // When
        User createdUser = userService.createUser(userDto);

        // Then
        assertNotNull(createdUser); // 새로운 사용자가 null이 아닌지 확인
        assertEquals(username, createdUser.getUsername()); // 사용자명이 일치하는지 확인
        // 기타 필요한 추가적인 확인 작업 수행
    }
}
