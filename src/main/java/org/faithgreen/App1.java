package org.faithgreen;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 再联系一下 zookeeper 的 api
 */
public class App1 {
    public static void main(String[] args) throws Exception {

        final String address = "192.168.172.3:2181,192.168.172.4:2181,192.168.172.5:2181,192.168.172.6:2181/api";
        int sessionTimeOut = 3000;
        // 因为 zookeeper 的连接用的是netty，即异步连接，所以用门闩来保证连接脸上之后再进行 api 操作
        CountDownLatch c = new CountDownLatch(1);
        Watcher sessionWatcher = new Watcher() {
            @Override
            public void process(WatchedEvent e) {
                String path = e.getPath();
                Event.KeeperState state = e.getState();
                Event.EventType type = e.getType();
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("SyncConnected ....");
                        c.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                }
                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
                        System.out.println("node " + path + " deleted ....");
                        break;
                    case NodeDataChanged:
                        System.out.println("node " + path + "dataChanged ....");
                        break;
                    case NodeChildrenChanged:
                        break;
                }
            }
        };
        ZooKeeper zk = new ZooKeeper(address, sessionTimeOut, sessionWatcher);
        c.await();

        ZooKeeper.States state = zk.getState();
        // 创建一个节点 testApi
        final String apiPath = "/testApi";
        byte[] data = "oldData".getBytes(StandardCharsets.UTF_8);
        ArrayList<ACL> openAclUnsafe = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        CreateMode ephemeral = CreateMode.EPHEMERAL;
        String pathName = zk.create(apiPath, data, openAclUnsafe, ephemeral);

        MyWatcher myWatcher = new MyWatcher();
        myWatcher.setZk(zk);
        // 给 zookeeper 注册一些 watcher
        zk.getData(apiPath, myWatcher, new MyCallBack(), "abc");
        System.in.read();

        // 改变值测试一下 watcher 是否调用
        zk.setData(apiPath, "newData".getBytes(StandardCharsets.UTF_8), 0, new MyCallBack(), "def");
        while (true){

        }
    }
}
