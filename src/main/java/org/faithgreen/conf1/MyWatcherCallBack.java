package org.faithgreen.conf1;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class MyWatcherCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {
    ZooKeeper zk;
    CountDownLatch latch = new CountDownLatch(1);
    MyConfig config;

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public MyConfig getConfig() {
        return config;
    }

    public void setConfig(MyConfig config) {
        this.config = config;
    }

    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if (bytes != null) {
            latch.countDown();
            String s1 = new String(bytes);
            config.setConfStr(s1);
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null) {
            zk.getData("/appConf", this, this, "def");
        }
    }

    @Override
    public void process(WatchedEvent e) {
        Event.EventType type = e.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/appConf", this, this, "ooo");
                break;
            case NodeDeleted:
                config.setConfStr("");
                latch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/appConf", this, this, "ddd");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    public void getConf() {
        zk.exists("/appConf", this, this, "abc");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
