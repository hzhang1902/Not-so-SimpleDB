import simpledb.buffer.Buffer;
import simpledb.buffer.ClockBufferManager;

public class TestClockBufferManager extends ClockBufferManager{
    TestClockBufferManager(int numbuffs){
        super(numbuffs);
    }

    /**
     * CS4432-Project1: get the header position
     * @return header position
     */
    public int getHeader () {
        return this.headerPos;
    }

    /**
     * CS4432-Project1: find a free buffer
     * @return the index of free buffer in buffer pool
     */
    public Integer chooseUnpinnedBuffer () {
        return super.chooseUnpinnedBuffer();
    }

    /**
     * CS4432-Project1: get the buffer pool
     * @return the buffer pool
     */
    public Buffer[] getpool () {
        return this.bufferpool;
    }

}
