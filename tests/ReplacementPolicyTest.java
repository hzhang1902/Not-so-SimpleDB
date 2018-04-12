import simpledb.buffer.*;
import simpledb.file.Block;
import simpledb.server.SimpleDB;

import java.io.OutputStream;
import java.io.PrintStream;

public class ReplacementPolicyTest {
    private static Block fakeContent1 = new Block("rptest", 1);
    private static Block fakeContent2 = new Block("rptest", 2);
    private static Block fakeContent3 = new Block("rptest", 3);
    private static Block fakeContent4 = new Block("rptest", 4);
    private static Block fakeContent5 = new Block("rptest", 5);
    private static Block fakeContent6 = new Block("rptest", 6);
    private static Block fakeContent7 = new Block("rptest", 7);

    // suppress system.out
    private static PrintStream originalStream = System.out;

    // cancel suppress
    private static PrintStream dummyStream = new PrintStream(new OutputStream(){
        public void write(int b) {
            // do nothing
        }
    });


    public static void main (String[] args) {
        System.setOut(dummyStream);
        SimpleDB.init("cs4432DB", null);
        System.setOut(originalStream);


        System.out.println("Clock replacement test");
        System.out.println("header wraps around: "+headerWrapsAround());
        System.out.println("clear and check ref bit: "+clearAndCheckRefBit());
        System.out.println("fail when full: "+clockFailWhenFull());
        System.out.println("full test: "+clockFullTest());

        System.out.println("LRU replacement test");
        System.out.println("pick LRU buffer: "+pickLRUBuffer());
        System.out.println("fail when full: "+LRUFailWhenFull());
        System.out.println("full test: "+LRUFullTest());

    }

    private static Boolean headerWrapsAround() {
        //System.setOut(dummyStream);

        TestClockBufferManager bm = new TestClockBufferManager(4);
        Buffer buff1 = bm.pin(fakeContent1);
        bm.unpin(buff1);
        Buffer buff2 = bm.pin(fakeContent2);
        bm.unpin(buff2);
        Buffer buff3 = bm.pin(fakeContent3);
        bm.unpin(buff3);
        Buffer buff4 = bm.pin(fakeContent4);
        bm.unpin(buff4);

        //System.setOut(originalStream);
        return bm.getHeader() == 0;
    }

    private static Boolean clearAndCheckRefBit() {
        //System.setOut(dummyStream);

        ClockBufferManager bm = new ClockBufferManager(4);
        ClockBuffer buff1 = bm.pin(fakeContent1);
        bm.unpin(buff1);
        ClockBuffer buff2 = bm.pin(fakeContent2);
        bm.unpin(buff2);
        ClockBuffer buff3 = bm.pin(fakeContent3);
        bm.unpin(buff3);
        ClockBuffer buff4 = bm.pin(fakeContent4);
        bm.unpin(buff4);
        ClockBuffer buff5 = bm.pin(fakeContent5);
        bm.unpin(buff5);

        //System.setOut(originalStream);
        return buff5.isRef() && !buff2.isRef() && !buff3.isRef() && !buff4.isRef();
    }

    private static Boolean pickLRUBuffer() {
        //System.setOut(dummyStream);

        TestLRUBufferManager bm = new TestLRUBufferManager(4);
        LRUBuffer buff1 = bm.pin(fakeContent1);
        bm.unpin(buff1);
        LRUBuffer buff2 = bm.pin(fakeContent2);
        bm.unpin(buff2);
        LRUBuffer buff3 = bm.pin(fakeContent3);
        bm.unpin(buff3);
        LRUBuffer buff4 = bm.pin(fakeContent4);
        bm.unpin(buff4);
        buff1.getInt(0);

        //System.setOut(originalStream);
        return bm.chooseUnpinnedBuffer() == 1;
    }

    private static Boolean LRUFailWhenFull () {
        //System.setOut(dummyStream);
        TestLRUBufferManager bm = new TestLRUBufferManager(4);
        bm.pin(fakeContent1);
        bm.pin(fakeContent2);
        bm.pin(fakeContent3);
        bm.pin(fakeContent4);
        Integer next = bm.chooseUnpinnedBuffer();
        //System.setOut(originalStream);

        return next == null;
    }

    private static Boolean clockFailWhenFull () {
        //System.setOut(dummyStream);
        TestClockBufferManager bm = new TestClockBufferManager(4);
        bm.pin(fakeContent1);
        bm.pin(fakeContent2);
        bm.pin(fakeContent3);
        bm.pin(fakeContent4);
        Integer next = bm.chooseUnpinnedBuffer();
        //System.setOut(originalStream);

        return next == null;
    }

    private static Boolean clockFullTest() {
        //System.setOut(dummyStream);
        TestClockBufferManager bm = new TestClockBufferManager(4);
        Buffer buff1 = bm.pin(fakeContent1);
        Buffer buff2 = bm.pin(fakeContent2);
        bm.unpin(buff2);
        Buffer buff3 = bm.pin(fakeContent3);
        Buffer buff4 = bm.pin(fakeContent4);
        bm.unpin(buff4);
        Buffer buff5 = bm.pin(fakeContent5);
        bm.unpin(buff5);
        buff4.getInt(0);
        Buffer buff6 = bm.pin(fakeContent2);
        bm.unpin(buff6);
        Buffer buff7 = bm.pin(fakeContent6);
        Buffer buff8 = bm.pin(fakeContent7);
        bm.unpin(buff8);
        bm.unpin(buff1);
        bm.unpin(buff3);
        bm.unpin(buff7);
        //System.setOut(originalStream);
        Buffer[] bp = bm.getpool();
        return bp[0].block().number() == 1 && bp[1].block().number() == 6 &&
                bp[2].block().number() == 3 && bp[3].block().number() == 7;
    }

    private static Boolean LRUFullTest() {
        //System.setOut(dummyStream);
        TestLRUBufferManager bm = new TestLRUBufferManager(4);
        Buffer buff1 = bm.pin(fakeContent1);
        Buffer buff2 = bm.pin(fakeContent2);
        bm.unpin(buff2);
        Buffer buff3 = bm.pin(fakeContent3);
        Buffer buff4 = bm.pin(fakeContent4);
        bm.unpin(buff4);
        Buffer buff5 = bm.pin(fakeContent5);
        bm.unpin(buff5);
        buff4.getInt(0);
        Buffer buff6 = bm.pin(fakeContent2);
        bm.unpin(buff6);
        Buffer buff7 = bm.pin(fakeContent6);
        Buffer buff8 = bm.pin(fakeContent7);
        bm.unpin(buff8);
        bm.unpin(buff1);
        bm.unpin(buff3);
        bm.unpin(buff7);
        //System.setOut(originalStream);
        Buffer[] bp = bm.getpool();
        return bp[0].block().number() == 1 && bp[1].block().number() == 7 &&
                bp[2].block().number() == 3 && bp[3].block().number() == 6;
    }
}
