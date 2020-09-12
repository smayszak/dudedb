package org.mayszak.io;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.mayszak.io.exceptions.UnknownPartitionException;

/**
 * A PartitionResolver that relies on a single file for all storage.
 */
public class BasicPartitionResolver implements PartitionResolver<String, Partition> {
    private final Partition diskPartition;
    private final List<Partition> allPartitions;

    public BasicPartitionResolver(String filePath) throws IOException {
        diskPartition = new Partition(filePath + ".data", filePath + ".index");
        allPartitions = Collections.singletonList(diskPartition);
    }

    public Partition resolveByKey(String key) throws UnknownPartitionException {
        return diskPartition;
    }

    public List<Partition> listAllPartitions() throws UnsupportedOperationException {
        return allPartitions;
    }

    public void close() {
        diskPartition.close();
    }
}
