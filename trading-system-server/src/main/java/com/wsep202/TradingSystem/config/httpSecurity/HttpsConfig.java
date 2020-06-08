package com.wsep202.TradingSystem.config.httpSecurity;
import com.wsep202.TradingSystem.web.controllers.api.PublicApiPaths;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class HttpsConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(PublicApiPaths.SELLER_OWNER_PATH+ "/**").hasRole("USER")
                .antMatchers(PublicApiPaths.BUYER_REG_PATH+ "/**").hasRole("USER")
                .antMatchers(PublicApiPaths.SELLER_OWNER_PATH+ "/**").hasRole("USER")
                .antMatchers(PublicApiPaths.GUEST_PATH+ "/**").permitAll()
                .antMatchers(PublicApiPaths.ADMIN_PATH+ "/**").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .and().logout().logoutSuccessUrl(PublicApiPaths.GUEST_PATH+ "/**").permitAll()
                .and().csrf().disable();
    }
}
