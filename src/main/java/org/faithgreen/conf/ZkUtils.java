package org.faithgreen.conf;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZkUtils {
    static ZooKeeper zk;
    final static String address = "192.168.172.3:2181,192.168.172.4:2181,192.168.172.5:2181,192.168.172.6:2181/testConf";
    final static DefaultWatcher defaultWatcher = new DefaultWatcher();
    static CountDownLatch c = new CountDownLatch(1);

    public static ZooKeeper getZK() {
        try {
            zk = new ZooKeeper(address, 4000, defaultWatcher);
            defaultWatcher.setC(c);
            c.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }
}
