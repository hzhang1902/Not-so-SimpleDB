## Not-so-SimpleDB Design

### Structural changes
BasicBufferManager, ClockBufferManager and LRUBufferManager are the three buffer managers that
each uses a different replacement policy. 

In order to easily select which policy to use, `simpledb/server/Startup.main()` is modified to accept a second argument: `-c` or `-l`, as well as `SimpleDB.init()` and `initFileLogAndBufferMgr()` to pass down the argument for `simpledb/buffer/BufferMgr` to setup basic, clock or LRU replacement policy.

Because there are three buffer managers now and they all need to be accessible by BufferMgr,
BufferManager Interface is created with 5 basic function definitions required by BufferMgr.
All the comments for the 5 functions are also there.

### BasicBufferManager
BasicBufferManager is modified to get unpinned buffer faster by implementing a hashmap called `freeBuffer`. `freeBuffer` stores all __indexes of free buffer__ in the buffer pool as keys, and the values are all null and never used. Whenever a free buffer is needed, it grabs the first index it gets from `freeBuffer` and removes it, and BasicBufferManager use that index to find the free buffer in the buffer pool. Whenever a buffer is unpinned, it checks if the buffer pin = 0, if it is, the buffer's index is put into the hashmap. This way, finding a free buffer takes constant time.

The reason to use a hashmap but not a stack is because of an edge case. Pinning an existing but unpinned buffer will require this buffer's index to be removed from `freeBuffer`. Pop cannot be used, because this existing buffer is not neccessarily going to be the one that will get popped. Removing a specific entry in a stack is costy (requires search), so hashmap is used for constant time removal.

BasicBufferManager is also modified to get an existing buffer faster by implementing another hashmap called `usedBuffer`. `usedBuffer` uses the __hashcode of a block__ as the key and stores the __index of a buffer__ assigned to that block. Each time a buffer is assigned, put (block.hashcode, buffer.index) pair into `usedBuffer`. Each time a buffer is searched, look for the block's hashcode in `usedBuffer`, and find the buffer in buffer pool with the index result. This way, finding an existing buffer takes constant time as well.

### ClockBufferManager
ClockBufferManager also uses `usedBuffer`, but not `freeBuffer`, because the size of `freeBuffer` is variable, therefore a header can't live on it.

### LRUBufferManager
LRUBufferManager uses both `usedBuffer` and `freeBuffer`. However, since LRU is implemented, we can't simply get a random index from `freeBuffer`. We still put it in when a buffer is freed, but __search__ for the LRU buffer from `freeBuffer`, __retrieve__ it and __remove__ it. It is still faster than searching in the entire buffer pool, but it's no longer constant time.

### Dirty bit, ref bit, access time
Buffer already came with dirty bit (modifiedBy), but doesn't come with ref bit or last access time. Therefore ClockBuffer is created, extending Buffer for ref manipulation. LRUBuffer is created, extending Buffer to keep track of last access time.

### BufferManagers for testing
TestClockBufferManager and TestLRUBufferManagers can access protected fields and methods of
ClockBufferManager and LRUBufferManager, which is useful for testing.
