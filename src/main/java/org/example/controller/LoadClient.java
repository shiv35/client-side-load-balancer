package org.example.controller;

import org.example.service.LoadBalancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class LoadClient {

    @Autowired
    private LoadBalancerService loadBalancerService;

    @PostMapping("/start")
    public String startClient(@RequestParam(defaultValue = "200") int count) {
        loadBalancerService.sendLoad(count);
        return "Load sent to best available servers.";
    }
}

