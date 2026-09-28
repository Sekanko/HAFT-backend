package pl.neene.haft.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.neene.haft.auth.services.JwtFilter;
import pl.neene.haft.auth.services.JwtService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtFilterTest {

    JwtService jwtService;
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    MockFilterChain filterChain;

    JwtFilter jwtFilter;

    @BeforeEach
    void setUp(){
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        jwtService = mock(JwtService.class);

        jwtFilter = new JwtFilter(jwtService);
    }

    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_requestWithoutHeader_doNotAuthenticate() throws ServletException, IOException {
        jwtFilter.doFilter(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_requestWithInvalidHeader_doNotAuthenticate() throws ServletException, IOException {
        request.addHeader("RandomWords", "Bearer invalidToken");
        jwtFilter.doFilter(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_requestWithEmptyHeader_authenticate() throws ServletException, IOException {
        request.addHeader("", "");
        jwtFilter.doFilter(request, response, filterChain);
    }

    @Test
    void doFilter_requestWithValidHeaderAndInvalidToken_authenticate() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalidToken");
        when(jwtService.extractClaims("invalidToken")).thenThrow(JwtException.class);
        jwtFilter.doFilter(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_requestWithProperHeaderButNoToken_doNotAuthenticate() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer ");
        jwtFilter.doFilter(request, response, filterChain);
    }

    @Test
    void doFilter_requestWithValidHeaderAndValidToken_authenticate() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer validToken");
        UUID subject = UUID.fromString("3f24150f-276e-49fc-8bdf-e70a41d19f18");
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("sys_role", "USER");
        claimsMap.put("sub", subject.toString());
        Claims claims = Jwts.claims().add(claimsMap).build();
        when(jwtService.extractClaims("validToken")).thenReturn(claims);

        jwtFilter.doFilter(request, response, filterChain);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals(subject, auth.getPrincipal());
        assertEquals("USER", auth.getAuthorities().iterator().next().getAuthority());
    }
}
