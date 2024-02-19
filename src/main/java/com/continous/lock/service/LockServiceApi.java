package com.continous.lock.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class LockServiceApi {
    // ideally move this function at filter level so that
    // normal sde 1 and sde 2 dont see regularly
    public String processApi(Lock lock, String apiName) {
        try {
            // Acquire the provided lock before processing
            lock.lock();
            // mimicking the api work
            TimeUnit.SECONDS.sleep(10);

            // Critical section of the API
            // Put your logic here

            return apiName + " processed successfully";
        } catch (Exception e) {
            return "Failed to process " + apiName + ": " + e.getMessage();
        } finally {
            // Release the lock after processing
            lock.unlock();
        }
    }
}
