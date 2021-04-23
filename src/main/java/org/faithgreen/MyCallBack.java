package org.faithgreen;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

public class MyCallBack implements AsyncCallback.DataCallback, AsyncCallback.StatCallback {
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        System.out.println("version: " + i);
        System.out.println("path: " + s);
        System.out.println("mark: " + o.toString());
        System.out.println("data: " + new String(bytes));
        System.out.println("stat: " + stat);
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        System.out.println("stat version: " + i);
        System.out.println("stat path: " + s);
        System.out.println("stat mark: " + o.toString());
        System.out.println("stat stat: " + stat);
    }
}
