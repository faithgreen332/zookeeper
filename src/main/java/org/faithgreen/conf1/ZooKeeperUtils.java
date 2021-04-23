package org.faithgreen.conf1;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperUtils {

    static ZooKeeper zk;
    final static String address = "192.168.172.3:2181,192.168.172.4:2181,192.168.172.5:2181,192.168.172.6:2181/testLock";
    static CountDownLatch latch = new CountDownLatch(1);
    static DefaultWatcher defaultWatcher = new DefaultWatcher();

    public static ZooKeeper getZk() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(address, 3000, defaultWatcher);
            defaultWatcher.setLatch(latch);
            latch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }

}
