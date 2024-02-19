package project.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.service.UserService;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                //.anyRequest().permitAll() //all request are permitted
                .antMatchers(HttpMethod.GET,"/api/v1/user/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/v1/user").permitAll()
                .antMatchers(HttpMethod.DELETE,"/api/v1/user/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/v1/user/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/api/v1/post/**").hasRole("OWNER")
                .antMatchers(HttpMethod.POST,"/api/v1/post/**").hasRole("OWNER")
                .antMatchers(HttpMethod.GET,"http://localhost:9000/user/confirmed/**").permitAll()
                .anyRequest().authenticated() //all request need authentications
                .and()
                .csrf().disable()
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
