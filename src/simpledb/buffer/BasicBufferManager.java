package simpledb.buffer;

import simpledb.file.*;

import java.util.HashMap;
import java.util.Stack;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 *
 * @author Edward Sciore
 */
class BasicBufferManager implements BufferManager {
    private Buffer[] bufferpool;
    private HashMap<Integer, Integer> usedbuffer = new HashMap<>();
    private Stack<Integer> freebuffer = new Stack<>();
    private int numAvailable;

    /**
     * Creates a buffer manager having the specified number
     * of buffer slots.
     * This constructor depends on both the {@link FileMgr} and
     * {@link simpledb.log.LogMgr LogMgr} objects
     * that it gets from the class
     * {@link simpledb.server.SimpleDB}.
     * Those objects are created during system initialization.
     * Thus this constructor cannot be called until
     * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
     * is called first.
     *
     * @param numbuffs the number of buffer slots to allocate
     */
    BasicBufferManager(int numbuffs) {
        bufferpool = new Buffer[numbuffs];
        numAvailable = numbuffs;
        for (int i = 0; i < numbuffs; i++) {
            bufferpool[i] = new Buffer(i);
            freebuffer.push(i);
        }
    }


    public synchronized void flushAll(int txnum) {
        for (Buffer buff : bufferpool) {
            if (buff.isModifiedBy(txnum)) {
                buff.flush();
            }
        }
    }


    public synchronized Buffer pin(Block blk) {
        Integer existingBufferIndex = findExistingBuffer(blk);
        Buffer buff;
        if (existingBufferIndex == null) {
            Integer unpinnedBufferIndex = chooseUnpinnedBuffer();
            if (unpinnedBufferIndex == null)
                return null;
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


    public synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
        Integer unpinnedBufferIndex = chooseUnpinnedBuffer();
        if (unpinnedBufferIndex == null)
            return null;
        Buffer buff = bufferpool[unpinnedBufferIndex];
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

    public synchronized void unpin(Buffer buff) {
        buff.unpin();
        if (!buff.isPinned()) {
            freebuffer.push(buff.id);
            numAvailable++;
        }
        System.out.println(toString());
    }

    public int available() {
        return numAvailable;
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
     * CS4432-Project1: Find an unpinned buffer
     * If found, remove from freebuffer
     * Return null if not found
     * @return index of an unpinned buffer in bufferpool
     */
    private Integer chooseUnpinnedBuffer() {
        return freebuffer.pop();
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
