package com.psa.proyecto_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rateLimitingFilter, "capacity", 2);
    }

    @Test
    void doFilterInternal_Disabled_CallsFilterChain() throws Exception {
        ReflectionTestUtils.setField(rateLimitingFilter, "enabled", false);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_Enabled_AllowsRequestsUnderLimit() throws Exception {
        ReflectionTestUtils.setField(rateLimitingFilter, "enabled", true);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(2)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_Enabled_BlocksRequestsOverLimit() throws Exception {
        ReflectionTestUtils.setField(rateLimitingFilter, "enabled", true);
        when(request.getRemoteAddr()).thenReturn("192.168.1.2");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Consume 2 (capacity)
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        
        // Exceed capacity
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(2)).doFilter(request, response);
        verify(response).setStatus(429); // HttpStatus.TOO_MANY_REQUESTS.value()
        verify(response).setContentType("application/json");
    }
}
