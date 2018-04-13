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
    private HashMap<Integer, Integer> freebuffer = new HashMap<>();
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
            freebuffer.put(i, null);
        }
//        System.out.println(freebuffer.toString());
    }


    public synchronized void flushAll(int txnum) {
        for (Buffer buff : bufferpool) {
            if (buff.isModifiedBy(txnum)) {
                buff.flush();
            }
        }
    }


    public synchronized Buffer pin(Block blk) {
        //System.out.println("pin blk");
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

            buff.assignToBlock(blk);
            usedbuffer.put(blk.hashCode(), unpinnedBufferIndex);
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


    public synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
        //System.out.println("pin new");
        Integer unpinnedBufferIndex = chooseUnpinnedBuffer();
        if (unpinnedBufferIndex == null)
            return null;
        Buffer buff = bufferpool[unpinnedBufferIndex];
        if (buff.block() != null) {
            usedbuffer.remove(buff.block().hashCode(), unpinnedBufferIndex);
        }
        Block newBlock = buff.assignToNew(filename, fmtr);
        usedbuffer.put(newBlock.hashCode(), unpinnedBufferIndex);
        numAvailable--;
        freebuffer.remove(buff.id);
        buff.pin();

//        System.out.println(freebuffer.toString());
        System.out.println(toString());
        return buff;
    }

    public synchronized void unpin(Buffer buff) {
        buff.unpin();
        if (!buff.isPinned()) {
            freebuffer.put(buff.id, 0);
            numAvailable++;
        }
//        System.out.println(freebuffer.toString());
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
        return freebuffer.keySet().iterator().next();
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
