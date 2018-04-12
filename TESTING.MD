# Testing
## SQL query test

### To run: 
1. delete `cs4432DB` folder in `/home` (Linux) or `C:\Users\%USERNAME%\` (Windows)
2. start SimpleDB server
3. run DatabaseQueryTest.main()


### Pseudo SQL input:
1. create STUDENT, DEPT, COURSE, SECTION and ENROLL tables and insert them with data.
2. show all CS students using select.
3. change all CS students' major to drama using update.
4. select all CS students again, should return nothing.
5. select all drama students, former CS students should be there.
6. delete a student called lee.
7. select all students, lee should not exist.


### Expected Output:
1. 
```
Table STUDENT created.
STUDENT records inserted.
Table DEPT created.
DEPT records inserted.
Table COURSE created.
COURSE records inserted.
Table SECTION created.
SECTION records inserted.
Table ENROLL created.
ENROLL records inserted.
```

2. 
```
select name and graduation year from students in cs major.
joe	2004
max	2005
lee	2004
```

3. 
```
change cs majors to drama major.
```

4. 
```
select name and graduation year from students in cs major.
```

5. 
```
select name and graduation year from students in drama major.
joe	2004
max	2005
bob	2003
art	2004
lee	2004
```

6. 
```
delete student lee
```

7. 
```
select name and graduation year from students.
joe	2004
amy	2004
max	2005
sue	2005
bob	2003
kim	2001
art	2004
pat	2001
```

### Design:
All 4 supported SQL commands used: CREATE TABLE, INSERT, UPDATE AND DELETE. 
Based on `studentClient/simpledb/CreateStudentDB.java`, `ChangeMajor.java`, 
`FindMajors.java`.


## Replacement policy test

I manually managed buffer pin/unpin and access to show that clock replacement
policy and LRU replacement policy are working as intended. The buffer size is
set to 4 in these tests.

### To run: 
1. Run SimpleDB server at least once (and don't delete `cs4432DB` folder)
2. Close SimpleDB server
3. Run ReplacementPolicyTest.main()


### Sequence for each test
#### Clock replacement policy
header wraps around:
* pin b1, unpin b1, pin b2, unpin b2, pin b3, unpin b3, pin b4, unpin b4
* check if header is at buffer 0

Expected output:
```
Clock replacement test


header at 0 found
header at 1
0 ref
0 pin
Pool header: 1
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: null ref: 0
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

0 unpin
Pool header: 1
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: null ref: 0
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 1 found
header at 2
1 ref
1 pin
Pool header: 2
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

1 unpin
Pool header: 2
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 2 found
header at 3
2 ref
2 pin
Pool header: 3
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: null ref: 0

2 unpin
Pool header: 3
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: null ref: 0

header at 3 found
header at 0
3 ref
3 pin
Pool header: 0
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 1 dirty: false block: [file rptest, block 4] ref: 1

3 unpin
Pool header: 0
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 4] ref: 1

header wraps around: true

```
clear and check ref bit
* pin b1, pin b2, pin b3, pin b4, pin b5
* check if buffer 0-3's ref bit is set to 1 when pinning b1-b4
* check if when pinning b5, header loops around buffer 0-3, set their ref to 0, 
and pick buffer 0.

Expected output:
```
header at 0 found
header at 1
0 ref
0 pin
Pool header: 1
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: null ref: 0
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

0 unpin
Pool header: 1
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: null ref: 0
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 1 found
header at 2
1 ref
1 pin
Pool header: 2
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

1 unpin
Pool header: 2
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 2 found
header at 3
2 ref
2 pin
Pool header: 3
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: null ref: 0

2 unpin
Pool header: 3
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: null ref: 0

header at 3 found
header at 0
3 ref
3 pin
Pool header: 0
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 1 dirty: false block: [file rptest, block 4] ref: 1

3 unpin
Pool header: 0
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 4] ref: 1

header at 0 ref=1
0 unref
header at 1 ref=1
1 unref
header at 2 ref=1
2 unref
header at 3 ref=1
3 unref
header at 0 found
replace: 0
header at 1
0 ref
0 pin
Pool header: 1
0: pin: 1 dirty: false block: [file rptest, block 5] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 0
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 0
3: pin: 0 dirty: false block: [file rptest, block 4] ref: 0

0 unpin
Pool header: 1
0: pin: 0 dirty: false block: [file rptest, block 5] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 0
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 0
3: pin: 0 dirty: false block: [file rptest, block 4] ref: 0

clear and check ref bit: true
```
clock fails when full
* pin b1, pin b2, pin b3, pin b4
* check if chooseUnpinnedBuffer() returns null

Expected output:
```
header at 0 found
header at 1
0 ref
0 pin
Pool header: 1
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: null ref: 0
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 1 found
header at 2
1 ref
1 pin
Pool header: 2
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 2 found
header at 3
2 ref
2 pin
Pool header: 3
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: null ref: 0

header at 3 found
header at 0
3 ref
3 pin
Pool header: 0
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 1 dirty: false block: [file rptest, block 4] ref: 1

header at 0 pinned
header at 1 pinned
header at 2 pinned
header at 3 pinned
fail when full: true
```

clock full test
* pin b1, pin b2, unpin b2, pin b3, pin b4, unpin b4,
* pin b5, unpin b5, access b4, pin b2, unpin b2, pin b6, pin b7,
* unpin b7, unpin b1, unpin b3, unpin b6

Expected output:
```
header at 0 found
header at 1
0 ref
0 pin
Pool header: 1
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: null ref: 0
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 1 found
header at 2
1 ref
1 pin
Pool header: 2
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

1 unpin
Pool header: 2
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 0 dirty: false block: null ref: 0
3: pin: 0 dirty: false block: null ref: 0

header at 2 found
header at 3
2 ref
2 pin
Pool header: 3
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: null ref: 0

header at 3 found
header at 0
3 ref
3 pin
Pool header: 0
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 1 dirty: false block: [file rptest, block 4] ref: 1

3 unpin
Pool header: 0
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 2] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 4] ref: 1

header at 0 pinned
header at 1 ref=1
1 unref
header at 2 pinned
header at 3 ref=1
3 unref
header at 0 pinned
header at 1 found
replace: 1
header at 2
1 ref
1 pin
Pool header: 2
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 5] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 4] ref: 0

1 unpin
Pool header: 2
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 5] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 4] ref: 0

3 ref
header at 2 pinned
header at 3 ref=1
3 unref
header at 0 pinned
header at 1 ref=1
1 unref
header at 2 pinned
header at 3 found
replace: 3
header at 0
3 ref
3 pin
Pool header: 0
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 5] ref: 0
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 1 dirty: false block: [file rptest, block 2] ref: 1

3 unpin
Pool header: 0
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 5] ref: 0
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 2] ref: 1

header at 0 pinned
header at 1 found
replace: 1
header at 2
1 ref
1 pin
Pool header: 2
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 6] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 2] ref: 1

header at 2 pinned
header at 3 ref=1
3 unref
header at 0 pinned
header at 1 pinned
header at 2 pinned
header at 3 found
replace: 3
header at 0
3 ref
3 pin
Pool header: 0
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 6] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 1 dirty: false block: [file rptest, block 7] ref: 1

3 unpin
Pool header: 0
0: pin: 1 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 6] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 7] ref: 1

0 unpin
Pool header: 0
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 6] ref: 1
2: pin: 1 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 7] ref: 1

2 unpin
Pool header: 0
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 1 dirty: false block: [file rptest, block 6] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 7] ref: 1

1 unpin
Pool header: 0
0: pin: 0 dirty: false block: [file rptest, block 1] ref: 1
1: pin: 0 dirty: false block: [file rptest, block 6] ref: 1
2: pin: 0 dirty: false block: [file rptest, block 3] ref: 1
3: pin: 0 dirty: false block: [file rptest, block 7] ref: 1

full test: true
```

#### LRU replacement policy
pick LRU buffer
* pin b1, unpin b1, pin b2, unpin b2, pin b3, unpin b3, pin b4, unpin b4
* access buffer 0
* check if chooseUnpinnedBuffer() returns buffer 1

Expected output:
```
LRU replacement test
0 refresh access time
1 refresh access time
2 refresh access time
3 refresh access time
replace: 0
0 pin
0 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 0 dirty: false block: null accessed: 9da9f274eec1
2: pin: 0 dirty: false block: null accessed: 9da9f275843d
3: pin: 0 dirty: false block: null accessed: 9da9f275fdb0

0 unpin
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 0 dirty: false block: null accessed: 9da9f274eec1
2: pin: 0 dirty: false block: null accessed: 9da9f275843d
3: pin: 0 dirty: false block: null accessed: 9da9f275fdb0

replace: 1
1 pin
1 refresh access time
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 1 dirty: false block: [file rptest, block 2] accessed: 9da9f314176b
2: pin: 0 dirty: false block: null accessed: 9da9f275843d
3: pin: 0 dirty: false block: null accessed: 9da9f275fdb0

1 unpin
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f314176b
2: pin: 0 dirty: false block: null accessed: 9da9f275843d
3: pin: 0 dirty: false block: null accessed: 9da9f275fdb0

replace: 2
2 pin
2 refresh access time
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f314176b
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f3171587
3: pin: 0 dirty: false block: null accessed: 9da9f275fdb0

2 unpin
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f314176b
2: pin: 0 dirty: false block: [file rptest, block 3] accessed: 9da9f3171587
3: pin: 0 dirty: false block: null accessed: 9da9f275fdb0

replace: 3
3 pin
3 refresh access time
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f314176b
2: pin: 0 dirty: false block: [file rptest, block 3] accessed: 9da9f3171587
3: pin: 1 dirty: false block: [file rptest, block 4] accessed: 9da9f31a5afa

3 unpin
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f285bf11
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f314176b
2: pin: 0 dirty: false block: [file rptest, block 3] accessed: 9da9f3171587
3: pin: 0 dirty: false block: [file rptest, block 4] accessed: 9da9f31a5afa

0 refresh access time
pick LRU buffer: true
```
LRU fails when full
* pin b1, pin b2, pin b3, pin b4
* check if chooseUnpinnedBuffer() returns null

Expected output:
```
0 refresh access time
1 refresh access time
2 refresh access time
3 refresh access time
replace: 0
0 pin
0 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f326d03e
1: pin: 0 dirty: false block: null accessed: 9da9f3249229
2: pin: 0 dirty: false block: null accessed: 9da9f324fa4d
3: pin: 0 dirty: false block: null accessed: 9da9f32565fa

replace: 1
1 pin
1 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f326d03e
1: pin: 1 dirty: false block: [file rptest, block 2] accessed: 9da9f32905cb
2: pin: 0 dirty: false block: null accessed: 9da9f324fa4d
3: pin: 0 dirty: false block: null accessed: 9da9f32565fa

replace: 2
2 pin
2 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f326d03e
1: pin: 1 dirty: false block: [file rptest, block 2] accessed: 9da9f32905cb
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f32b0fee
3: pin: 0 dirty: false block: null accessed: 9da9f32565fa

replace: 3
3 pin
3 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f326d03e
1: pin: 1 dirty: false block: [file rptest, block 2] accessed: 9da9f32905cb
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f32b0fee
3: pin: 1 dirty: false block: [file rptest, block 4] accessed: 9da9f32d11fd

fail when full: true
```

LRU full test
* pin b1, pin b2, unpin b2, pin b3, pin b4, unpin b4,
* pin b5, unpin b5, access b4, pin b2, unpin b2, pin b6, pin b7,
* unpin b7, unpin b1, unpin b3, unpin b6

Expected output:
```
0 refresh access time
1 refresh access time
2 refresh access time
3 refresh access time
replace: 0
0 pin
0 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: null accessed: 9da9f3352144
2: pin: 0 dirty: false block: null accessed: 9da9f3359e8f
3: pin: 0 dirty: false block: null accessed: 9da9f336174c

replace: 1
1 pin
1 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 1 dirty: false block: [file rptest, block 2] accessed: 9da9f339e928
2: pin: 0 dirty: false block: null accessed: 9da9f3359e8f
3: pin: 0 dirty: false block: null accessed: 9da9f336174c

1 unpin
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f339e928
2: pin: 0 dirty: false block: null accessed: 9da9f3359e8f
3: pin: 0 dirty: false block: null accessed: 9da9f336174c

replace: 2
2 pin
2 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f339e928
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 0 dirty: false block: null accessed: 9da9f336174c

replace: 3
3 pin
3 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f339e928
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 1 dirty: false block: [file rptest, block 4] accessed: 9da9f34225e6

3 unpin
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f339e928
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 0 dirty: false block: [file rptest, block 4] accessed: 9da9f34225e6

replace: 1
1 pin
1 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 1 dirty: false block: [file rptest, block 5] accessed: 9da9f3458b0f
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 0 dirty: false block: [file rptest, block 4] accessed: 9da9f34225e6

1 unpin
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 5] accessed: 9da9f3458b0f
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 0 dirty: false block: [file rptest, block 4] accessed: 9da9f34225e6

3 refresh access time
replace: 1
1 pin
1 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 1 dirty: false block: [file rptest, block 2] accessed: 9da9f348cdec
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 0 dirty: false block: [file rptest, block 4] accessed: 9da9f3479130

1 unpin
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f348cdec
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 0 dirty: false block: [file rptest, block 4] accessed: 9da9f3479130

replace: 3
3 pin
3 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 2] accessed: 9da9f348cdec
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 1 dirty: false block: [file rptest, block 6] accessed: 9da9f34bef5a

replace: 1
1 pin
1 refresh access time
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 1 dirty: false block: [file rptest, block 7] accessed: 9da9f34f4fe1
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 1 dirty: false block: [file rptest, block 6] accessed: 9da9f34bef5a

1 unpin
Pool
0: pin: 1 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 7] accessed: 9da9f34f4fe1
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 1 dirty: false block: [file rptest, block 6] accessed: 9da9f34bef5a

0 unpin
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 7] accessed: 9da9f34f4fe1
2: pin: 1 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 1 dirty: false block: [file rptest, block 6] accessed: 9da9f34bef5a

2 unpin
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 7] accessed: 9da9f34f4fe1
2: pin: 0 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 1 dirty: false block: [file rptest, block 6] accessed: 9da9f34bef5a

3 unpin
Pool
0: pin: 0 dirty: false block: [file rptest, block 1] accessed: 9da9f337802f
1: pin: 0 dirty: false block: [file rptest, block 7] accessed: 9da9f34f4fe1
2: pin: 0 dirty: false block: [file rptest, block 3] accessed: 9da9f33f8bcf
3: pin: 0 dirty: false block: [file rptest, block 6] accessed: 9da9f34bef5a

full test: true
```

The above result shows that clock replacement policy has a header, it
increments and wraps around to check for unused buffers. It sets ref bit
checks ref bit, and clears ref bit.

LRU replacement policy keeps track of last accessed time of each buffer,
and pick the least recently used one.

Neither of them replaces pinned buffers, both of them return null/fail
properly when trying to insert to a fully pinned buffer pool.


## Design
The tests are designed to be independent and test one functionality each. 
All functionalities of each replacement policy are tested with multiples of 
these tests, and a full test that simulates real life usage is done to 
make sure they are stable.

The full test focused on letting two policies get different 
results. This is where that access b4 in the middle came from. The header 
is before buffer 3 but after 1, so buffer 3's ref bit will be set to 0 before 
buffer 1's, and buffer 3 will be replaced. For LRU, since buffer 
3 is accessed more recently than buffer 1, so buffer 1 will be replaced.
