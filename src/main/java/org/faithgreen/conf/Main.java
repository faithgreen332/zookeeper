package org.faithgreen.conf;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 模拟 zookeeper 获取配置的操作
 */
public class Main {

    ZooKeeper zk;

    @Before
    public void before() {
        zk = ZkUtils.getZK();
    }

    @After
    public void after() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 核心是 watcher 和回调 callback 的处理
     */
    @Test
    public void getConf() {
        WatchCallBack w = new WatchCallBack();
        MyConf conf = new MyConf();
        w.setConf(conf);
        w.setZk(zk);
        w.await();
        while (true) {
            if (conf.getConfStr().equals("")) {
                // 如果取不到，就要阻塞等到取到
                System.out.println("配置丢了 ....");
                w.await();
            } else {
                System.out.println("conf: " + conf.getConfStr());
            }
        }
    }
}
