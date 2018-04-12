# Not-so-SimpleDB
## Authors

* Haofan Zhang
* Yuan Wang

## Installation Instructions

1. Install the Java SDK, level 1.6 or higher.
2. Import project into Eclipse/Intellij and follow the steps in the course instructions 
(Intellij: https://canvas.wpi.edu/courses/6652/files/1128816/download?download_frd=1, 
Eclipse: https://canvas.wpi.edu/courses/6652/files/1095108/download?download_frd=1), 
except no need to create `src` folder and drag stuffs in.
3. Mark `tests` folder as Test Sources Root.


## Running the Server
1. Go to `src/simpledb/server/Startup.java` and right click it.
2. Click on `Run Startup.main()`.
3. You will get an exception: `invalid command arguments: incorrect number of arguments`
4. Set run configuration's first argument to `cs4432DB`. 
5. Choose which replacement policy to use:
   * To run with basic/slow policy, no need to add anything.
   * To run with clock replacement policy, add a second argument `-c`.
   * To run with LRU policy, add a second argument `-l`.
6. Then run the server again.
7. `cs4432DB` directory will be created in `/home` (Linux) or `C:\Users\%USERNAME%\` (Windows)
 
If everything is working correctly, when you run the server with a
new database folder the following will be printed in the server's
window:

```
creating new database
new transaction: 1
transaction 1 committed
database server ready
```

If you run the server with an existing database folder, the following
will be printed instead:

```
recovering existing database
database server ready
```

## Running Tests/Client Programs
1. Go to `tests` folder
2. Run SimpleDB server. Then right click DatabaseQueryTest.java and click on
`Run DatabaseQueryTest.main()` to run SQL query test.
3. Close SimpleDB server. 
4. Right click ReplacementPolicyTest.java and click on
`Run ReplacementPolicyTest.main()` to run replacement policy test.
5. Delete `cs4432DB` directory