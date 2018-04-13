package simpledb.buffer;

import simpledb.file.Block;

import java.util.HashMap;
import java.util.Stack;

public class LRUBufferManager implements BufferManager {
    protected LRUBuffer[] bufferpool;
    private HashMap<Integer, Integer> usedbuffer = new HashMap<>();
    private HashMap<Integer, Integer> freebuffer = new HashMap<>();
    private int numAvailable;

    /**
     * CS4432-Project1: Initialize LRU buffer manager.
     * Rest see BasicBufferManager()
     * @param numbuffs maximum number of buffers
     */
    public LRUBufferManager(int numbuffs) {
        bufferpool = new LRUBuffer[numbuffs];
        numAvailable = numbuffs;
        for (int i=0; i<numbuffs; i++) {
            bufferpool[i]= new LRUBuffer(i);
            freebuffer.put(i, null);
        }
//        System.out.println(freebuffer.toString());
    }

    synchronized public void flushAll(int txnum) {
        for (LRUBuffer buff : bufferpool) {
            if (buff.isModifiedBy(txnum)) {
                buff.flush();
            }
        }
    }

    public int available() {
        return this.numAvailable;
    }

    synchronized public LRUBuffer pin(Block blk) {
        Integer existingBufferIndex = findExistingBuffer(blk);
        LRUBuffer buff;
        if (existingBufferIndex == null) {
            Integer unpinnedBufferIndex = chooseUnpinnedBuffer();
            if (unpinnedBufferIndex == null)
                return null;
            System.out.println("replace: "+unpinnedBufferIndex);
            buff = bufferpool[unpinnedBufferIndex];

            if (buff.block() != null) {
                usedbuffer.remove(buff.block().hashCode(), unpinnedBufferIndex);
            }

            usedbuffer.put(blk.hashCode(), buff.id);
            buff.assignToBlock(blk);
        } else {
            buff = bufferpool[existingBufferIndex];
        }
        if (!buff.isPinned()) {
            numAvailable--;
            freebuffer.remove(buff.id);
        }
        buff.pin();
//        System.out.println(freebuffer.toString());
        System.out.println(toString());
        return buff;
    }

    synchronized public LRUBuffer pinNew(String filename, PageFormatter fmtr) {
        Integer unpinnedBufferIndex = chooseUnpinnedBuffer();
        if (unpinnedBufferIndex == null)
            return null;
        System.out.println("replace: "+unpinnedBufferIndex);
        LRUBuffer buff = bufferpool[unpinnedBufferIndex];
        if (buff.block() != null) {
            usedbuffer.remove(buff.block().hashCode(), unpinnedBufferIndex);
        }
        Block keyBlock = buff.assignToNew(filename, fmtr);
        usedbuffer.put(keyBlock.hashCode(), unpinnedBufferIndex);
        numAvailable--;
        freebuffer.remove(buff.id);
        buff.pin();
//        System.out.println(freebuffer.toString());
        System.out.println(toString());
        return buff;
    }

    synchronized public void unpin(Buffer buff) {
        buff.unpin();
        if (!buff.isPinned()) {
            freebuffer.put(buff.id, null);
            numAvailable++;
        }
        System.out.println(toString());
//        System.out.println(freebuffer.toString());
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
     * CS4432-Project1: Find an unpinned buffer.
     * If found, remove from freebuffer
     * Return null if not found
     * @return index of an unpinned buffer in bufferpool
     */
    protected Integer chooseUnpinnedBuffer() {
        Integer index = null;
        Long currentTime = System.nanoTime();
        long max = 0;
        for (Integer i : freebuffer.keySet()) {
            LRUBuffer buff = bufferpool[i];
            long accessAgo = currentTime - buff.lastAccessed;
            if (accessAgo > max) {
                max = accessAgo;
                index = i;
            }
        }
        return index;
    }

    /**
     * CS4432-Project1: return a string describing details about the buffer pool
     * and each buffer
     * @return string
     */
    @Override
    public String toString() {
        String s = "Pool\n";
        for (Buffer buff : bufferpool){
            s+= (buff.toString() + "\n");
        }
        return s;
    }
}
