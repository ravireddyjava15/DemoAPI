package com.example.demo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.constants.Routes.ONE;
import static com.example.demo.constants.Routes.THREE;
import static com.example.demo.constants.Routes.TWO;

@RestController
@Slf4j
public class DemoController {

  @GetMapping(ONE)
  public ResponseEntity<String> getOne() {
    log.info("getOne called");
    return ResponseEntity.ok("this is one");
  }

  @GetMapping(TWO)
  public ResponseEntity<String> getTwo() {
    log.info("getTwo called");
    return ResponseEntity.ok("this is two");
  }

  @GetMapping(THREE)
  public ResponseEntity<String> getThree() {
    log.info("getThree called");
    return ResponseEntity.ok("this is three");
  }


/*  private void testSleep() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }*/
}
