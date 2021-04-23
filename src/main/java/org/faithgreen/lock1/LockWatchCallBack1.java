package org.faithgreen.lock1;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LockWatchCallBack1 implements Watcher, AsyncCallback.StatCallback, AsyncCallback.ChildrenCallback, AsyncCallback.StringCallback {
    private ZooKeeper zk;
    private String threadName;
    private String pathName;
    private CountDownLatch latch = new CountDownLatch(1);

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

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    /**
     * 创建 CreateMode.EPHEMERAL_SEQUENTIAL 节点，阻塞
     */
    public void tryLock() {
        zk.create("/lock", threadName.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * zk.create 的 StringCallBack impl
     * 如果创建完了，这个回调会被调用，获取children
     *
     * @param rc
     * @param path
     * @param o
     * @param childPath
     */
    @Override
    public void processResult(int rc, String path, Object o, String childPath) {

        if (childPath != null) {
            System.out.println(childPath + " created");
            pathName = childPath;
            zk.getChildren("/", this, this, "dev");
        }
    }

    /**
     * 获取 children 的回调
     * 多个客户端创建节点，list 总是有第一个的，如果是第一个，线程放行，否则在前一个节点上添加 watcher
     *
     * @param rc
     * @param s
     * @param o
     * @param list
     */
    @Override
    public void processResult(int rc, String s, Object o, List<String> list) {
//        Collections.sort(list);
        int i = list.indexOf(pathName.substring(1));

        if (i == 0) {
            // 是第一个，就对了，获取锁了
            System.out.println(threadName + " is first ....");
            latch.countDown();
        } else {
            // 不是第一个，就在他的前一个加 watcher
            zk.exists("/" + list.get(i - 1), this, this, "dev");
        }
    }

    /**
     * children 节点的 watcher
     * 在释放锁是删除节点的时候触发再一次获取 children 的过程
     *
     * @param e
     */
    @Override
    public void process(WatchedEvent e) {
        switch (e.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "dadd");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

    }

    /**
     * 释放锁的时候，删除节点
     */
    public void unLock() {
        try {
            System.out.println(threadName + "工作结束，释放锁 ....");
            zk.delete(pathName, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }
}
