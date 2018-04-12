## Bugs

1. Due to the design of SimpleDB, there is no way of testing or using buffer manager manually and independently 
without initializing SimpleDB server. So when running ReplacementPolicyTest, SimpleDB is initialized, its own 
buffer manager is never used, but test buffer managers still writes to it (and the file system as well). 
This causes the tests to create table files while actually no tables are created. 

    So, In case of weird things happen, delete the database local storage folder after running ReplacementPolicyTest.

2. The database does not check duplicate entries. For example, 
`insert into STUDENT(SId, SName, MajorId, GradYear) values (1, 'joe', 10, 2004)`
twice will create two joes. Better, it doesn't support DROP TABLE.

    So delete the database local storage folder every time after running DatabaseQueryTest.

3. ReplacementPolicyTest can run only after SimpleDB server is at least run once (or `cs4432DB` 
folder exists). Remember that we have to call SimpleDB.init()? We also have to wait for file system 
to be ready. Unfortunately that happens asynchronously, so we might start running the tests 
without it being ready and cause bufferUnderflowException. 
	
	So run SimpleDB server, close it, and then run ReplacementPolicyTest.