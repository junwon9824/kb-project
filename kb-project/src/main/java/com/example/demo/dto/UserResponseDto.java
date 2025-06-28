package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String userid;
    private String clientSafeIp;
    private String phone;
    private String address;
    private boolean disabled;
    private String roleName;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userid(user.getUserid())
                .clientSafeIp(user.getClientSafeIp())
                .phone(user.getPhone())
                .address(user.getAddress())
                .disabled(user.isDisabled())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
} 