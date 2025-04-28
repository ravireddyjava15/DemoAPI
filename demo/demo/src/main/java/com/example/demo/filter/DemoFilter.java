package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import static com.example.demo.constants.Routes.ONE;
import static com.example.demo.constants.Routes.THREE;
import static com.example.demo.constants.Routes.TWO;

@Component
public class DemoFilter extends OncePerRequestFilter {

  private final Semaphore semaphore;
  //TODO Make this configurable
  public static final int MAX_CONCURRENT_REQUESTS = 2;

  public DemoFilter() {
    this.semaphore = new Semaphore(MAX_CONCURRENT_REQUESTS, true);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    PathContainer pathContainer = PathContainer.parsePath(request.getServletPath());
    String path = pathContainer.value();
    //System.out.println("==> path: " + path);

    if (path.contains(ONE) || path.contains(TWO) || path.contains(THREE)) {
      boolean acquired = semaphore.tryAcquire();
      if (!acquired) {
        sendError(response);
        return;
      }

      try {
        filterChain.doFilter(request, response);
      } finally {
        semaphore.release();
      }
    } else {
      filterChain.doFilter(request, response);
    }

  }

  private void sendError(HttpServletResponse response) throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.getWriter().print("Too many requests. Please try again later.");
    response.getWriter().flush();
  }
}
