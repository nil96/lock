package com.continous.lock.controller;
import com.continous.lock.dto.ApiResponse;
import com.continous.lock.lock.ZookeeperLockService;
import com.continous.lock.service.LockServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@RestController
public class LockControllerDemo {

    @Autowired
    private ZookeeperLockService zookeeperLockService;

    @Autowired
    LockServiceApi lockServiceApi;

    @GetMapping("/api1")
    public ApiResponse api1() {
        Lock lock = zookeeperLockService.getLock("/api1/idempotencyKey");
        String s1 = lockServiceApi.processApi(lock, "API 1");
        return new ApiResponse(s1, true);
    }
}