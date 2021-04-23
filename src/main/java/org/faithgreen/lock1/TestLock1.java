package org.faithgreen.lock1;

import org.apache.zookeeper.ZooKeeper;
import org.faithgreen.conf1.ZooKeeperUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestLock1 {

    ZooKeeper zk;

    @Before
    public void getZk() {
        zk = ZooKeeperUtils.getZk();
    }

    @After
    public void after() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lock() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                LockWatchCallBack1 w = new LockWatchCallBack1();
                String name = Thread.currentThread().getName();
                w.setThreadName(name);
                w.setZk(zk);

                // 获取锁
                w.tryLock();

                // 模拟客户端得到锁之后的工作
                System.out.println(name + " working ...");

                // 释放锁
                w.unLock();

            }).start();
        }
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
