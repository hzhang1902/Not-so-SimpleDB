package simpledb.buffer;

public class ClockBuffer extends Buffer {
    private int ref = 0;

    /**
     * CS4432-Project1: Initialize buffer by id.
     */
    public ClockBuffer (int id) {super(id);}

    /**
     * CS4432-Project1: set ref bit to 1
     * rest see Buffer.getInt()
     */
    @Override
    public int getInt(int offset) {
        ref();
        return super.getInt(offset);
    }

    /**
     * CS4432-Project1: set ref bit to 1
     * rest see Buffer.getString()
     */
    @Override
    public String getString(int offset) {
        ref();
        return super.getString(offset);
    }

    /**
     * CS4432-Project1: set ref bit to 1
     * rest see Buffer.setInt()
     */
    @Override
    public void setInt(int offset, int val, int txnum, int lsn) {
        ref();
        super.setInt(offset, val, txnum, lsn);
    }

    /**
     * CS4432-Project1: set ref bit to 1
     * rest see Buffer.setString()
     */
    @Override
    public void setString(int offset, String val, int txnum, int lsn) {
        ref();
        super.setString(offset, val, txnum, lsn);
    }

    /**
     * CS4432-Project1: set ref bit to 1
     * rest see Buffer.pin()
     */
    @Override
    void pin() {
        ref();
        super.pin();
    }

    /**
     * CS4432-Project1: set ref bit to 0
     */
    void unref () {
        ref = 0;
        System.out.println("" + id + " unref");
    }

    /**
     * CS4432-Project1: set ref bit to 1
     */
    private void ref () {
        ref = 1;
        System.out.println("" + id + " ref");
    }

    /**
     * CS4432-Project1: check if ref bit is 1
     * @return is ref bit = 1?
     */
    public Boolean isRef() {
        return ref > 0;
    }

    /**
     * CS4432-Project1: return a string describing this buffer
     * @return string
     */
    @Override
    public String toString() {
        return super.toString()+" ref: "+this.ref;
    }

}
