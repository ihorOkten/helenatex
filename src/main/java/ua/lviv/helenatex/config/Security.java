package ua.lviv.helenatex.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ComponentScan("ua.lviv.helenatex.*")
public class Security extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
//    @Autowired
//    DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


    private InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryConfigure() {
        return new InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder>();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                AuthenticationProvider provider) throws Exception {
        inMemoryConfigure()
                .withUser("ss")
                .password("ss")
                .roles("ADMIN")
                .and()
                .configure(auth);
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .and().formLogin().loginPage("/login")
                .usernameParameter("username").passwordParameter("password")
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .and().csrf().and()
                .exceptionHandling()/*.accessDeniedHandler()*/.accessDeniedPage("/403");
    }


}


