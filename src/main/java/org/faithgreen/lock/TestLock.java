package org.faithgreen.lock;

import org.apache.zookeeper.ZooKeeper;
import org.faithgreen.conf1.ZooKeeperUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestLock {

    ZooKeeper zk;

    @Before
    public void before() {
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
                LockWatchCallBack w = new LockWatchCallBack();
                String threadName = Thread.currentThread().getName();
                w.setThreadName(threadName);
                w.setZk(zk);

                w.tryLock();

                System.out.println("业务干活 。。。。");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
