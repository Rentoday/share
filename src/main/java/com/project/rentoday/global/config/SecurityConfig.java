package com.project.rentoday.global.config;

import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.service.NotificationService;
import com.project.rentoday.global.filter.JwtFilter;
import com.project.rentoday.global.filter.LoginFilter;
import com.project.rentoday.global.filter.LogoutFilter;
import com.project.rentoday.global.jwt.repository.RefreshRepository;
import com.project.rentoday.global.jwt.service.JwtService;
import com.project.rentoday.global.oauth.service.OAuth2UserService;
import com.project.rentoday.global.oauth.service.Oauth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

//설정파일임을 명시
@Configuration
//security임을 명시
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //AuthenticationManager의 생성자 인자로 필요하기 때문에 주입 받음
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final OAuth2UserService oAuth2UserService;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    //커스텀한 loginFilter의 생성자 인자로 넣기 위해 빈으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    //단방향 암호화인 BCrypt를 BEAN으로 등록하여 사용자 패스워드를 암호화하는데 사용
    //단방향 암호화 : 암호화만 가능하며 복호화할 수 없다.
    //양방향 암호화 : 암호화와 복호화 두가지가 가능하다.
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //교차 출처 리소스 공유(Cross-origin resource sharing, CORS)
        //웹 브라우저는 다른출처의 리소스에 접근하는것을 허용하지 않음 -> 동일 출처 정책
        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                //CORS 허용 URL
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8888"));
                                //GET, POST등의 메서드 허용
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                //★★★★★★★★★★★★★★★★★★
                                configuration.setAllowCredentials(true);
                                //허용할 헤더
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                //CORS 허용시간
                                configuration.setMaxAge(3600L);

                                //허용할 헤더 Authorization를 추가하여 jwt 클라이언트에게 응답가능
                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                                return configuration;
                            }
                        }));

        //csrf?
        //csrf는 세션 방식에 로그인시에 공격에 대한 방어를 위한 설정이다.
        //세션리스 형식의 jwt에는 해당하지 않기 떄문에 disable 설정한다.
        http
                .csrf((auth) -> auth.disable());

        //security에서 기본제공하는 login폼을 사용하지 않기때문에 disable
        //UsernamePasswordAuthenticationFilter는 formlogin에서만 활성화됨
        //UsernamePasswordAuthenticationFilter가 동작하지 않기 때문에 custom 사용해야함
        http
                .formLogin((auth) -> auth.disable());

        //★★★★★★★★★httpbasic이 뭔지 찾아봐야함
        http
                .httpBasic((auth) -> auth.disable());

        http
                .oauth2Login((oauth2) -> oauth2
//                    .loginPage("/login")
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(oAuth2UserService))
                        .successHandler(oauth2SuccessHandler));

        http
                .authorizeHttpRequests((auth) -> auth
                        //인증없이도 접근이 가능한 주소에 대한 설정
                        .requestMatchers("/login", "/main", "/join", "/reissue", "/api/emailCheck", "/api/verification/**","/**").permitAll()
                        //ADMIN 사용자만 접근이 가능한 주소에 대한 설정
                        .requestMatchers("/admin").hasRole("ADMIN")
                        //위의 접근 가능한 요청 주소를 제외한 모든 요청은 반드시 인증(로그인)되어야한다.
                        .anyRequest().authenticated());

        // Custom login filter with custom URL
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtService, refreshRepository, memberRepository);
        loginFilter.setFilterProcessesUrl("/api/member/login");

        //jwt커스텀 필터 등록
        http
                .addFilterAfter(new JwtFilter(jwtService, memberRepository), LoginFilter.class);

        //커스텀한 필터를 등록
        //필터의 순서를 지정하여 등록할 수 있다.
        //addFilterAt : 커스텀한 필터의 자리에 그대로 커스텀 필터를 대신 적용한다.
        //addFilterAfter : 특정 필터 이후로 등록
        //addFilterBefore : 특정 필터 이전으로 등록
        //LoginFilter에서 authenticationManager를 생성자로 주입받았기 때문에 인자로 넣어야함
        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new LogoutFilter(jwtService, refreshRepository, notificationService), org.springframework.security.web.authentication.logout.LogoutFilter.class);

        //세션에 대한 설정부분
        http
                //세션을 stateless로 설정
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //모든 설정을 적용하여 build
        return http.build();
    }
}
