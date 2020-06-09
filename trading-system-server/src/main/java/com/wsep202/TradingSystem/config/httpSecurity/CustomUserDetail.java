package com.wsep202.TradingSystem.config.httpSecurity;

import com.wsep202.TradingSystem.domain.trading_system_management.UserSystem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomUserDetail implements UserDetails {

    private static final long serialVersionUID = 1L;

    private UserSystem user;
    private Set<GrantedAuthority> authorities;

    public String getPassword() {
        return user.getPassword();
    }

    public String getUsername() {
        return user.getUserName();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
