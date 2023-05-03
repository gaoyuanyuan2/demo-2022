package com.example.demo2022.example.juc;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * CopyOnWrite
 */
public class Router {
    public final String ip;
    public final Integer port;
    public final String iface;

    // 构造函数
    public Router(String ip,
                  Integer port, String iface) {
        this.ip = ip;
        this.port = port;
        this.iface = iface;
    }

    // 重写 equals 方法
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Router) {
            Router r = (Router) obj;
            return iface.equals(r.iface) &&
                    ip.equals(r.ip) &&
                    port.equals(r.port);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, iface);
    }


    // 路由表信息
    public static class RouterTable {
        //Key: 接口名
        //Value: 路由集合
        ConcurrentHashMap<String, CopyOnWriteArraySet<Router>>
                rt = new ConcurrentHashMap<>();

        // 根据接口名获取路由表
        public Set<Router> get(String iface) {
            return rt.get(iface);
        }

        // 删除路由
        public void remove(Router router) {
            Set<Router> set = rt.get(router.iface);
            if (set != null) {
                set.remove(router);
            }
        }

        // 增加路由
        public void add(Router router) {
            Set<Router> set = rt.computeIfAbsent(
                    router.iface, r ->
                            new CopyOnWriteArraySet<>());
            set.add(router);
        }
    }
}
