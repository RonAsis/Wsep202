package com.wsep202.TradingSystem.config.httpSecurity;

import com.wsep202.TradingSystem.data_access_layer.UserRepository;
import com.wsep202.TradingSystem.domain.trading_system_management.TradingSystemDao;
import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TradingSystemDao tradingSystemDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserSystem> userSystem = tradingSystemDao.getUserSystem(username);
        userSystem.orElseThrow(()-> new UsernameNotFoundException(username));
        return CustomUserDetail.builder()
                .user(userSystem.get())
                .authorities(userSystem.get().getGrantedAuthorities())
                .build();
    }
}
