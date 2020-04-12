package com.assigment_1;

import java.util.concurrent.ConcurrentHashMap;

public class FileInfo {

    String pathname;
    String id;
    int replication_degree;

    public final ConcurrentHashMap<Integer, BackUpChunk> backedUpChunk = new ConcurrentHashMap<>();

    public FileInfo(String pathname, String id, int replication_degree) {
        this.pathname = pathname;
        this.id = id;
        this.replication_degree = replication_degree;
    }
}
