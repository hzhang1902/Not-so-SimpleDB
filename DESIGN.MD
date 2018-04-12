## Not-so-SimpleDB Design

BasicBufferManager, ClockBufferManager and LRUBufferManager are the three buffer managers that
each uses a different replacement policy. In order to easily select which policy to use,
`simpledb/server/Startup.main()` is modified to accept a second argument: `-c` or `-l`, as well
as `SimpleDB.init()` and `initFileLogAndBufferMgr()` to pass down the argument for `simpledb/buffer/BufferMgr` 
to setup basic, clock or LRU replacement policy.


Because there are three buffer managers now and they all need to be accessible by BufferMgr,
BufferManager Interface is created with 5 basic function definitions required by BufferMgr.
All the comments for the 5 functions are also there.


BasicBufferManager is modified to get unpinned buffer faster by implementing a stack. The stack
stores all __indexes of free buffer__ in the buffer pool. Whenever a free buffer is needed, it pops
an index, and BasicBufferManager use that index to find the free buffer in the buffer pool.
Whenever a buffer is unpinned, it checks if the buffer pin = 0, if it is, the buffer's index is
pushed into the stack. This way, finding a free buffer takes constant time with no
overhead in managing the stack.


BasicBufferManager is also modified to get an existing buffer faster by implementing a hashmap.
The hashmap uses the __hashcode of a block__ as the key and stores the __index of a buffer__ assigned
to that block. Each time a buffer is assigned, put (block.hashcode, buffer.index) pair into
the hashmap. Each time a buffer is searched, look for the block's hashcode in the hashmap,
and find the buffer in buffer pool with the index result. This way, finding an existing buffer
takes constant time as well.


ClockBufferManager also uses hashmap for existing buffer, but no stack for free buffer,
because the size of free-buffer-stack's is variable, therefore a header can't live on it.


LRUBufferManager uses both hashmap and free-buffer-stack. However, since LRU is implemented,
we can't simply pop any free buffer and use it. We still push when a buffer is freed, but __search__
for the LRU buffer on the stack, __retrieve__ it and __remove__ it. It is still faster than searching
in the entire buffer pool, but it's no longer constant time.


Buffer already came with dirty bit and modifiedBy bit, but doesn't come with ref bit or last
access time. Therefore ClockBuffer is created, extending Buffer for ref manipulation. LRUBuffer
is created, extending Buffer to keep track of last access time.


TestClockBufferManager and TestLRUBufferManagers can access protected fields and methods of
ClockBufferManager and LRUBufferManager, which is useful for testing.