package simpledb.buffer;

import simpledb.file.Block;

import java.util.HashMap;

public class ClockBufferManager implements BufferManager {
    private int numBuffs;
    protected ClockBuffer[] bufferpool;
    private HashMap<Integer, Integer> usedbuffer = new HashMap<>();
    private int numAvailable;
    protected int headerPos;

    /**
     * CS4432-Project1: Initialize clock buffer manager. set header position to 0.
     * Rest see BasicBufferManager()
     * @param numbuffs maximum number of buffers
     */
    public ClockBufferManager(int numbuffs) {
        bufferpool = new ClockBuffer[numbuffs];
        numAvailable = numbuffs;
        for (int i=0; i<numbuffs; i++) {
            bufferpool[i]= new ClockBuffer(i);
        }
        this.numBuffs = numbuffs;
        this.headerPos = 0;
    }

    @Override
    synchronized public void flushAll(int txnum) {
        for (ClockBuffer buff : bufferpool) {
            if (buff.isModifiedBy(txnum)) {
                buff.flush();
            }
        }
    }

    @Override
    public int available() {
        return numAvailable;
    }

    @Override
    synchronized public void unpin(Buffer buff) {
        buff.unpin();
        if (!buff.isPinned()) {
            numAvailable++;
        }
        System.out.println(toString());
    }

    @Override
    synchronized public ClockBuffer pin(Block blk) {
        Integer existingBufferIndex = findExistingBuffer(blk);
        ClockBuffer buff;
        if (existingBufferIndex == null) {
            Integer unpinnedBufferIndex = chooseUnpinnedBuffer();
            if (unpinnedBufferIndex == null)
                return null;
            buff = bufferpool[unpinnedBufferIndex];

            if (buff.block() != null) {
                usedbuffer.remove(buff.block().hashCode(), unpinnedBufferIndex);
                System.out.println("replace: "+unpinnedBufferIndex);
            }
            System.out.println("header at "+headerPos);

            buff.assignToBlock(blk);
            usedbuffer.put(blk.hashCode(), buff.id);
        } else {
            buff = bufferpool[existingBufferIndex];
        }

        if (!buff.isPinned()) {
            numAvailable--;
        }
        buff.pin();
        System.out.println(toString());
        return buff;
    }

    @Override
    synchronized public ClockBuffer pinNew(String filename, PageFormatter fmtr) {
        Integer unpinnedBufferIndex = chooseUnpinnedBuffer();
        if (unpinnedBufferIndex == null)
            return null;
        ClockBuffer buff = bufferpool[unpinnedBufferIndex];

        if (buff.block() != null) {
            usedbuffer.remove(buff.block().hashCode(), unpinnedBufferIndex);
            System.out.println("replace: "+unpinnedBufferIndex);
        }
        System.out.println("header at "+headerPos);

        Block keyBlock = buff.assignToNew(filename, fmtr);
        usedbuffer.put(keyBlock.hashCode(), unpinnedBufferIndex);
        numAvailable--;
        buff.pin();

        System.out.println(toString());
        return buff;
    }

    /**
     * CS4432-Project1: Find an unpinned buffer
     * Return null if not found
     * @return index of an unpinned buffer in bufferpool
     */
    protected Integer chooseUnpinnedBuffer() {
        int counter = 0;
        Boolean hasRef = false;
        while (counter < numBuffs || hasRef) {
            ClockBuffer cBuff = bufferpool[headerPos];
            if (cBuff.isPinned()){
                System.out.println("header at "+headerPos+" pinned");
                headerPos = nextHeaderPos(headerPos);
                counter++;
                continue;
            }
            if (cBuff.isRef()) {
                System.out.println("header at "+headerPos+" ref=1");
                hasRef = true;
                cBuff.unref();
                headerPos = nextHeaderPos(headerPos);
                continue;
            }
            System.out.println("header at "+headerPos+" found");
            int lastHead = headerPos;
            headerPos = nextHeaderPos(headerPos);
            return lastHead;
        }
        return null;
    }

    /**
     * CS4432-Project1: get the next header position. if exceeds limit, wrap back to 0
     * @param currentPos current position of header
     * @return the next position of header
     */
    private Integer nextHeaderPos(int currentPos) {
        if (currentPos + 1 >= numBuffs) {
            return 0;
        }
        return currentPos + 1;
    }

    /**
     * CS4432-Project1: Find an existing buffer using a block. Return
     * the index of found buffer, null if not found.
     * @param blk block for finding buffer
     * @return index of found buffer in bufferpool
     */
    private Integer findExistingBuffer(Block blk) {
        return usedbuffer.get(blk.hashCode());
    }

    /**
     * CS4432-Project1: return a string describing details about the buffer pool
     * and each buffer
     * @return string
     */
    @Override
    public String toString() {
        String s = "Pool header: "+this.headerPos+"\n";
        for (Buffer buff : bufferpool){
            s+= (buff.toString() + "\n");
        }
        return s;
    }
}
