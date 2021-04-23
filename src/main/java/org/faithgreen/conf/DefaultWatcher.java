package org.faithgreen.conf;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher {

    CountDownLatch c;

    public void setC(CountDownLatch c) {
        this.c = c;
    }

    @Override
    public void process(WatchedEvent e) {
        Event.EventType type = e.getType();
        Event.KeeperState state = e.getState();
        String path = e.getPath();

        switch (type) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }

        switch (state) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
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
    }
}
