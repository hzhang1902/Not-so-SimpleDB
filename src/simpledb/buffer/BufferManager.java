package simpledb.buffer;

import simpledb.file.Block;

public interface BufferManager {
    /**
     * Flushes the dirty buffers modified by the specified transaction.
     *
     * @param txnum the transaction's id number
     */
    void flushAll(int txnum);

    /**
     * Pins a buffer to the specified block.
     * If there is already a buffer assigned to that block
     * then that buffer is used;
     * otherwise, an unpinned buffer from the pool is chosen.
     * Returns a null value if there are no available buffers.
     *
     * @param blk a reference to a disk block
     * @return the pinned buffer
     */
    Buffer pin(Block blk);

    /**
     * Allocates a new block in the specified file, and
     * pins a buffer to it.
     * Returns null (without allocating the block) if
     * there are no available buffers.
     *
     * @param filename the name of the file
     * @param fmtr     a pageformatter object, used to format the new block
     * @return the pinned buffer
     */
    Buffer pinNew(String filename, PageFormatter fmtr);

    /**
     * Unpins the specified buffer.
     *
     * @param buff the buffer to be unpinned
     */
    void unpin(Buffer buff);

    /**
     * Returns the number of available (i.e. unpinned) buffers.
     *
     * @return the number of available buffers
     */
    int available();
}
