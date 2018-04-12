import simpledb.buffer.Buffer;
import simpledb.buffer.LRUBufferManager;

public class TestLRUBufferManager extends LRUBufferManager {
    TestLRUBufferManager(int buffsize) {
        super(buffsize);
    }

    /**
     * CS4432-Project1: find a free buffer
     * @return the index of free buffer in buffer pool
     */
    public Integer chooseUnpinnedBuffer() {
        return super.chooseUnpinnedBuffer();
    }

    /**
     * CS4432-Project1: get the buffer pool
     * @return the buffer pool
     */
    public Buffer[] getpool() {
        return this.bufferpool;
    }
}
