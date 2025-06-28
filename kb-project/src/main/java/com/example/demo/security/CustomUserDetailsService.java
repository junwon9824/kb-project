package com.example.demo.security;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // JPA Repository 등

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByuserid(username).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다: \" + username"));
//
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUserid(),
//                user.getPassword(),
//                user.getRoles().stream()
//                        .map(role -> new SimpleGrantedAuthority(role.getName()))
//                        .collect(Collectors.toList())
//        );
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByuserid(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = user.getRole();
        List<GrantedAuthority> authorities;
        
        if (role != null) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        } else {
            // role이 null인 경우 기본 권한 부여
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUserid(), user.getPassword(), authorities);
    }



}
