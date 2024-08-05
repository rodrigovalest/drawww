package com.rodrigo.drawing_contest.config.security;

import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserDetails;
import com.rodrigo.drawing_contest.services.JwtService;
import com.rodrigo.drawing_contest.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null) ? authHeader.replace("Bearer ", "") : null;

        if (token != null) {
            String username = this.jwtService.getUsernameByToken(token);
            User user = this.userService.findUserByUsername(username);

            if (user != null) {
                UserDetails userDetails = new UserDetails(user);

                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
