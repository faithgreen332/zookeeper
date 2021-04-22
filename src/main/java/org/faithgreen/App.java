package org.faithgreen;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {

        // 知识点1：zookeeper 是异步连接，所以要拿到zookeeper连接，要阻塞线程
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 知识点：new 的时候的第二个参数是 session 超时时间，意味着这个时间之后，这个客户端的操作就没了，比如创建的新的 node 就没了
        ZooKeeper zk = new ZooKeeper("192.168.172.3:2181,192.168.172.4:2181,192.168.172.5:2181,192.168.172.6:2181,", 3000, new Watcher() {
            // 知识点2：这个 Watcher 是 session 级别的，默认的watcher, 跟 path ，node 没关系
            // 还有一种 watcher 是查询级别的，get exists 这种方法时可以注册
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
                        countDownLatch.countDown();
                        System.out.println("session event connected ....");
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
                        break;
                    case NodeDataChanged:
                        System.out.println("session type dataChanged ....");
                        break;
                    case NodeChildrenChanged:
                        break;
                }
            }
        });

        countDownLatch.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("zk state connecting ....");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("zk state connected ....");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

//        zk.delete("/faith", 0);
        String pathName = zk.create("/faith", "old faith".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Stat stat = new Stat();
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent e) {
                try {
                    zk.getData("/faith", this, stat);
                } catch (KeeperException keeperException) {
                    keeperException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                Event.KeeperState eks = e.getState();
                Event.EventType type = e.getType();
                String path = e.getPath();
                switch (eks) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
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
                        System.out.println(path + "node deleted");
                        break;
                    case NodeDataChanged:
                        System.out.println(path + " data changed");
                        break;
                    case NodeChildrenChanged:
                        break;
                }
            }
        };

        byte[] data = zk.getData("/faith", watcher, stat);

        System.out.println("old getData: " + new String(data));
        // 知识点3：path 的 watcher 是一次行的，在这个节点发生动作的时候出发，这是第一次改变数据，能检测到 watcher 执行了
        Stat stat1 = zk.setData("/faith", "new Data".getBytes(StandardCharsets.UTF_8), 0);
        // 知识点3：path 的 watcher 是一次行的，在这个节点发生动作的时候出发，这是第二次改变数据，不能检测到 watcher 执行了,想要每次都有watcher ，在时间发生的时候重复注册就行了
        zk.setData("/faith", "new Data1".getBytes(StandardCharsets.UTF_8), stat1.getVersion());



        // 知识点：在 geteData  的时候，可以异步注册一个回调，当数据返回时发生
        AsyncCallback.DataCallback dataCallback = new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println("o: " + o.toString());
                System.out.println("s: " + s);
                System.out.println("i: " + i);
                System.out.println("byte: " + new String(bytes));
                System.out.println("stat: " + stat.toString());
            }
        };
        zk.getData("/faith", watcher, dataCallback, "aaaaa");

        System.in.read();
    }
}
