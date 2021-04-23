package org.faithgreen.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LockWatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.StringCallback, AsyncCallback.Children2Callback {

    private ZooKeeper zk;
    private String threadName;
    CountDownLatch latch = new CountDownLatch(1);
    String pathName;

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void tryLock() {
        zk.create("/appLock", threadName.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zk.delete(pathName, -1);
            System.out.println(threadName + " 释放锁 。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * StringCallBack impl
     *
     * @param rc
     * @param path
     * @param o
     * @param name
     */
    @Override
    public void processResult(int rc, String path, Object o, String name) {
        if (name != null) {
            System.out.println(threadName + " created node: " + name);
            pathName = name;
            zk.getChildren("/", false, this, "sdf");
        }
    }

    /**
     * Children2Callback impl
     *
     * @param rc
     * @param s
     * @param o
     * @param list
     * @param stat
     */
    @Override
    public void processResult(int rc, String s, Object o, List<String> list, Stat stat) {
        Collections.sort(list);
        int i = list.indexOf(pathName.substring(1));
        // 如果是第一个
        if (i == 0) {
            System.out.println(threadName + " i am first ....");
            try {
                zk.setData("/", threadName.getBytes(StandardCharsets.UTF_8), -1);
                latch.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // 不是第一个，那就 watcher 他的前一个
            zk.exists("/" + list.get(i - 1), this, this, "sdf");
        }
    }

    @Override
    public void process(WatchedEvent e) {
        switch (e.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "sdf");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null) {
            try {
                zk.getData("/", this, stat);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
