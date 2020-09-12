package org.mayszak.io;

import org.mayszak.io.exceptions.UnknownPartitionException;

import java.io.IOException;
import java.util.List;

public class PartitionManager<K, P> implements AutoCloseable {
    private final PartitionResolver<K, P> resolver;
    private static PartitionManager instance = null;

    public PartitionManager(PartitionResolver<K, P> resolver)  {
        this.resolver = resolver;
    }

    public static PartitionManager getPartitionManager() throws IOException {
        String dir = System.getProperty("user.home");
        return getPartitionManager(dir + "/dudedb");
    }

    public static PartitionManager getPartitionManager(String filePath) throws IOException {
        return getPartitionManager(new BasicPartitionResolver(filePath));
    }

    public static <K, P> PartitionManager<K, P> getPartitionManager(PartitionResolver<K, P> resolver) {
        if (instance == null) {
            instance = new PartitionManager(resolver);
        }
        return instance;
    }

    public P getPartition(K key) throws UnknownPartitionException {
        return resolver.resolveByKey(key);
    }

    public List<P> getAllPartitions() {
        return resolver.listAllPartitions();
    }

    public void close(){
        resolver.close();
        instance = null;
    }
}
