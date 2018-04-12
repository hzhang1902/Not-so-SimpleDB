package simpledb.buffer;

import simpledb.file.Block;

public class LRUBuffer extends Buffer {
    public long lastAccessed;

    /**
     * CS4432-Project1: Initialize buffer by id. refresh timer
     */
    public LRUBuffer (int id) {
        super(id);
        resetAccess();
    }

    /**
     * CS4432-Project1: refresh timer
     * rest see Buffer.getInt()
     */
    @Override
    public int getInt(int offset) {
        resetAccess();
        return super.getInt(offset);
    }

    /**
     * CS4432-Project1: refresh timer
     * rest see Buffer.getString()
     */
    @Override
    public String getString(int offset) {
        resetAccess();
        return super.getString(offset);
    }

    /**
     * CS4432-Project1: refresh timer
     * rest see Buffer.setInt()
     */
    @Override
    public void setInt(int offset, int val, int txnum, int lsn) {
        resetAccess();
        super.setInt(offset, val, txnum, lsn);
    }

    /**
     * CS4432-Project1: refresh timer
     * rest see Buffer.setString()
     */
    @Override
    public void setString(int offset, String val, int txnum, int lsn) {
        resetAccess();
        super.setString(offset, val, txnum, lsn);
    }

    /**
     * CS4432-Project1: refresh timer
     * rest see Buffer.pin()
     */
    @Override
    void pin() {
        super.pin();
        resetAccess();
    }

    /**
     * CS4432-Project1: refresh access timer
     */
    private void resetAccess() {
        System.out.println(""+this.id+" refresh access time");
        this.lastAccessed = System.nanoTime();
    }

    /**
     * CS4432-Project1: return a string describing this buffer
     * @return string
     */
    public String toString() {
        return super.toString()+" accessed: "+Long.toHexString(this.lastAccessed);
    }
}
