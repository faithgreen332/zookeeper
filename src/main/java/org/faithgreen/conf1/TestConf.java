package org.faithgreen.conf1;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConf {

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
    public void getConf() {

        MyWatcherCallBack w = new MyWatcherCallBack();
        MyConfig config = new MyConfig();
        w.setConfig(config);
        w.setZk(zk);
        w.getConf();
        while (true) {
            if ("".equals(config.getConfStr())) {
                System.out.println(" diu le ");
                w.getConf();
            } else {
                System.out.println(config.getConfStr());
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
