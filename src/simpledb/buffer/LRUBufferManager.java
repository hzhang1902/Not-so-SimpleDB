package simpledb.buffer;

import simpledb.file.Block;

import java.util.HashMap;
import java.util.Stack;

public class LRUBufferManager implements BufferManager {
    protected LRUBuffer[] bufferpool;
    private HashMap<Integer, Integer> usedbuffer = new HashMap<>();
    private Stack<Integer> freebuffer = new Stack<>();
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
            freebuffer.add(i);
        }
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
        }
        buff.pin();
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
        buff.pin();
        System.out.println(toString());
        return buff;
    }

    synchronized public void unpin(Buffer buff) {
        buff.unpin();
        if (!buff.isPinned()) {
            freebuffer.push(buff.id);
            numAvailable++;
        }
        System.out.println(toString());
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
        for (Integer i : freebuffer) {
            LRUBuffer buff = bufferpool[i];
            if (!buff.isPinned()){
                long accessAgo = currentTime - buff.lastAccessed;
                if (accessAgo > max) {
                    max = accessAgo;
                    index = i;
                }
            }
        }
        if (index != null) {
            freebuffer.remove(index);
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
