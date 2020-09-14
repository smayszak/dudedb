package org.mayszak.io;

import java.util.List;

import org.mayszak.io.exceptions.UnknownPartitionException;

/**
 * The PartitionResolver abstracts the behavior of looking up a given partition based on a key. This allows us to exercise
 * various aspects of routing without having to actually route.
 */
interface PartitionResolver<K, P> extends AutoCloseable {
    /**
     * Given a key, either return the partition handle associated with that key or throw an exception if unable to locate the
     * key. An Either type would be preferable here if we weren't using Java.
     */
    P resolveByKey(K key) throws UnknownPartitionException;

    /**
     * Return a list of all partitions known to the system. It is not required that resolvers support this, so a given
     * implementation MUST throw an UnsupportedOperationException if it does not.
     */
    List<P> listAllPartitions() throws UnsupportedOperationException;

    /**
     * Performs any needed shutdown operations on this resolver. This interface is marked AutoCloseable so that it may be
     * used in a try statement. The close() function should be made idempotent by implementors.
     */
    void close();
}
