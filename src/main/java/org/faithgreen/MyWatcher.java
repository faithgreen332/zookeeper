package org.faithgreen;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class MyWatcher implements Watcher {
    ZooKeeper zk;

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    @Override
    public void process(WatchedEvent e) {
        String path = e.getPath();
        Event.KeeperState state = e.getState();
        Event.EventType type = e.getType();
        try {
            zk.getData(path,this,new Stat());
        } catch (KeeperException keeperException) {
            keeperException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        switch (state) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                System.out.println("SyncConnected ....");
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
}
