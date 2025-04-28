package com.example.demo.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.example.demo.constants.Routes.ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DemoFilterTest {

  private DemoFilter demoFilter;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    demoFilter = new DemoFilter();
    filterChain = mock(FilterChain.class);
  }

  @Test
  void testAllowedRequestWithinLimit() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath(ONE);
    MockHttpServletResponse response = new MockHttpServletResponse();

    demoFilter.doFilterInternal(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void testRejectRequestWhenConcurrencyLimitReached() throws InterruptedException {
    List<Integer> statusCodes = excuteAndReturnStatusCodes(3, ONE);
    assertEquals(2, statusCodes.stream().filter(status -> status == HttpStatus.OK.value()).count());
    assertEquals(1, statusCodes.stream().filter(status -> status == HttpStatus.TOO_MANY_REQUESTS.value()).count());
  }

  @Test
  void testSystemsRequestNoRestriction() throws InterruptedException {

    List<Integer> statusCodes = excuteAndReturnStatusCodes(5, "actuator/health");

    assertEquals(5, statusCodes.stream().filter(status -> status == HttpStatus.OK.value()).count());
  }

  private List<Integer> excuteAndReturnStatusCodes(int numRequests, String servletPath) throws InterruptedException {

    ExecutorService executor = Executors.newFixedThreadPool(numRequests);
    List<Future<Integer>> results = new ArrayList<>();

    for (int i = 0; i < numRequests; i++) {
      results.add(executor.submit(() -> {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(servletPath);
        MockHttpServletResponse response = new MockHttpServletResponse();

        demoFilter.doFilterInternal(request, response, filterChain);
        return response.getStatus();
      }));
    }

    executor.shutdown();
    boolean b = executor.awaitTermination(5, TimeUnit.SECONDS);

    return results.stream().map(future -> {
      try {
        return future.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).toList();
  }
}
