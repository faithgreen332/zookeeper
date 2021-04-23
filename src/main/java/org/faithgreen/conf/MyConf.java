package org.faithgreen.conf;

/**
 * 模拟配置中心数据，zookeeper最大 1M
 * 比如是xml文件，需要自己实现io流和编解码的逻辑
 */
public class MyConf {

    private String confStr;

    public String getConfStr() {
        return confStr;
    }

    public void setConfStr(String confStr) {
        this.confStr = confStr;
    }
}
