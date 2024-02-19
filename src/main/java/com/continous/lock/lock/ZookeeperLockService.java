package com.continous.lock.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.LockInternals;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static org.apache.curator.framework.recipes.locks.LockInternals.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Component
public class ZookeeperLockService {

    private static final String ZOOKEEPER_CONNECTION_STRING = "localhost:2181";
    private CuratorFramework client;
    private ConcurrentMap<String, ZookeeperSpinLock> locks;

    public ZookeeperLockService() {
        client = CuratorFrameworkFactory.newClient(ZOOKEEPER_CONNECTION_STRING,new RetryNTimes(0, 0));
        client.start();
        locks = new ConcurrentHashMap<>();
    }

    public Lock getLock(String lockPath) {
        return locks.computeIfAbsent(lockPath, this::createLock);
    }

    private ZookeeperSpinLock createLock(String lockPath) {
        return new ZookeeperSpinLock(client, lockPath);
    }

    private static class ZookeeperSpinLock implements Lock {
        private final CuratorFramework client;
        private final String lockPath;

        public ZookeeperSpinLock(CuratorFramework client, String lockPath) {
            this.client = client;
            this.lockPath = lockPath;
        }

        @Override
        public void lock() {
            while (true) {
                try {
                    if (tryLock()) {
                        System.out.println("Lock taken on key: " + lockPath);
                        return;
                    }
                    Thread.sleep(100); // Adjust the sleep duration as needed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while acquiring lock", e);
                }
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException("LockInterruptibly not supported");
        }

        @Override
        public boolean tryLock() {
            try {
                client.create().creatingParentsIfNeeded().forPath(lockPath);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException("TryLock with timeout not supported");
        }

        @Override
        public void unlock() {
            try {
                client.delete().forPath(lockPath);
                System.out.println("Lock released on key: " + lockPath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to release lock", e);
            }
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Conditions not supported");
        }
    }
}

