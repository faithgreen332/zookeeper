package org.faithgreen.conf;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 注册了节点的事件和回调
 */
public class WatchCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

    ZooKeeper zk;
    CountDownLatch cc = new CountDownLatch(1);
    MyConf conf;

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public CountDownLatch getCc() {
        return cc;
    }

    public void setCc(CountDownLatch cc) {
        this.cc = cc;
    }

    public MyConf getConf() {
        return conf;
    }

    public void setConf(MyConf conf) {
        this.conf = conf;
    }

    public void await() {
        zk.exists("/appConf", this, this, "abc");
        try {
            // 让主线程阻塞住，上面那行代码运行下去
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Watcher 的接口实现
     *
     * @param e event
     */
    @Override
    public void process(WatchedEvent e) {
        String path = e.getPath();
        Event.EventType type = e.getType();
        Event.KeeperState state = e.getState();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                // 如果节点被创建，就获取它，让它调用回调，给conf设值
                zk.getData("/appConf", this, this, "def");
                break;
            case NodeDeleted:
                // 如果节点被删除了，考虑到容忍性的
                // 配置置空
                conf.setConfStr("");
                // 重新枷锁
                cc = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                // 如果节点被改了，就重新获取,目的是让他调用回调函数，给conf设置新的值
                zk.getData("/appConf", this, this, "def");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    /**
     * DataCallBack 的实现
     *
     * @param i     版本
     * @param s     path
     * @param o     o
     * @param bytes data
     * @param stat  stat
     */
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if (bytes != null) {
            String s1 = new String(bytes);
            conf.setConfStr(s1);
            // 取到数据了，让主线程继续往下走
            cc.countDown();
        }
    }

    /**
     * StatCallBack 的实现
     *
     * @param i    版本
     * @param s    path
     * @param o    o
     * @param stat stat
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null) {
            zk.getData("/appConf", this, this, "def");
        }
    }
}
