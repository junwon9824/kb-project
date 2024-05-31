package com.example.demo.Service;

import com.example.demo.dto.GPTResponseDto;
import com.example.demo.service.GPTChatRestService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GPTChatRestServiceTest {

    @Mock
    GPTChatRestService gptChatRestService;

    @Test
    void testCompletionChat() {
        // Given
        String input = "김건에게 만원 송금";
        GPTResponseDto mockResponse = new GPTResponseDto("송금", "김건", new BigDecimal("10000"));
        when(gptChatRestService.completionChat(input)).thenReturn(mockResponse);

        // When
        GPTResponseDto gptResponseDto = gptChatRestService.completionChat(input);

        // Then
        GPTResponseDto expectedResponse = new GPTResponseDto("송금", "김건", new BigDecimal("10000"));
        assertEquals(expectedResponse.getName(), gptResponseDto.getName());
        assertEquals(expectedResponse.getAction(), gptResponseDto.getAction());
        assertEquals(expectedResponse.getAmount(), gptResponseDto.getAmount());

    }
}
