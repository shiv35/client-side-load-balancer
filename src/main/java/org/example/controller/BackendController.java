package org.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class BackendController {

    private final ServletWebServerApplicationContext webServerContext;

    @Autowired
    public BackendController(ServletWebServerApplicationContext webServerContext) {
        this.webServerContext = webServerContext;
    }

    @GetMapping("/process")
    public String process() throws InterruptedException {
        long delay = (long) (Math.random() * 120 + 30);
        Thread.sleep(delay);
        int port = webServerContext.getWebServer().getPort();

        return String.format(
                "Processed by server on port %d at %s with delay %dms",
                port, LocalDateTime.now(), delay
        );
    }
}
