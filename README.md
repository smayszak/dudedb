# DudeDB
*It's not much, but at least it's housebroken*

---
DudeDB is about as simple of a database as you can make. Simplicity aside, it actually works and performs fairly well.  
It lacks even basic durabilty features but that is the point. I wanted to create a very simple database for developers who
want to learn advanced concepts by implementing them. For example, DudeDB uses a very rigid file format that uses way more data
than it really needs to, so fix it!  I created DudeDB with extensibility in mind.  Big DB's always partition out data, in DudeDB, there
is a partition manager that always returns a single partition - so extend it and implement your own partitioing schema!.  This is the point
of DudeDB, a very simple db that has had all of the boring plumbing code written, leaving you with a project where you can do the fun stuff, 
like migrate it to use a b-tree!

The following are some ideas of how I would evolve dudedb, feel free to follow the list or come up with your own!

* Data is in a very rigid and inefficient format. Project 1, make the storage match the data size. This means coming up with a new binary file format, and adjusting all the reads and writes. We essentially have a key value store, but after doing this
you can imagine all the work it would take to get a dynamic columnar database implemented.

* In a big database, data is partitioned. So research and introduce a partitioning scheme, you can pretend like this single db is a big system (thik of the DB class as a service interface)- introduce into the partition manager some way to split data into different files.
Put an arbitrary limit on the data file sizes  and ensure the incoming data is well distributed amoung your data files

* Data is always retrived from the disk in this DB.  That is pretty inefficient.  There is  some golden rule about recently accessed data being accessed again.  Implement a caching schema over the data to avoid data being retrieved from disk. It's up to you how you want to
do this - but remember to invalidate if when data changes and come up with some eviction process and make sure you dont run out of memory.   Make sure your caching and disk access are entirely indepednant because we will be changing the storage format soon, so don't couple these!.

* Time to dive into the big problem.  Dudedb  requires that all the indexes fit into memory.  This could be considered a design over a limitation as many modern in-memory databases use this architecutre  and solve scaling by simply partition out the fleet, which we
are not going to do in this project (although we sort of pretented that we did when we took step 2 to partition our data).  But this is for learning, and we are going to go with a non-in-memory database simply to develop the algorithms.  So what does that mean?  Well
first, we are treating this db very similar to a WAL, we simply write the most recent data to the tail of the file and when we update the index postition to the most recent  When new data comes in, we add it to the end of the file and dont bother deleting the old values.
So, what you want to do here is up to you.

Want to do a B-tree?  Cool - you will have to develop data pages, fit your data into pages and then rearrange the layout on disk
Want to do a SSTABLe or LSM Tree?  Cool, checkout how to keep an in-memory memtable with a flushing mecahnism that writes in the proper format.

one tip - do you have to undo the current storage?  Not necessarily.  If you think about it, this current schema is a log.  In Cassandra, the data flow is - write to the log, write to the memtable, eventually flush to sstable.
In a future project when we start worrying about consistency, you will need the log.  so keep that in mind.

* If you have made it this far, you have gone deeper into db's that most programmers will ever go.  So now what? Well, we dont have a way to delete data.  And depending on how you are storing your data it will vary in complexity.
If you did a b-tree, then go ahead and delete the data on disk. If you did an LSM tree, look into tombstones.
- - -
## DudeDB Default structure 
The App file is a simple REPL that tells you the commands and accepts your input. 

When App fires up, it creates a DB object. DB Object by default will create a dudedb.data and dudedb.index file in your home directory. 
You can change this by passing a new root path to the constructor.

DudeDB will get a singleton instance of the PartitionManager.  The PartitionManager creates a Partition object, the partition class accepts
A path to the data files, it checks if they exist and if they do not, it creates them. If the index exists, it reads it into memory

The index file contains a primary key to long value. The long value represents the byte offset in the data file where that particular value exists. 

When you call get, the paritition looks up the key in the index, if it finds it, it then streams the data file, opening at the offset, gets a fixed sized
byte array and passes it into the DBFileFromat deserialize class, which constructs the key value back into strings.  

Similarly, when a write occurs the data gets serialized into a byte array and appended to the end of the file. 

That is pretty much it!  Have fun extending the application to make it use the disk more efficiently, layout the data on disk for faster looksup, consider how
you may delete data, run compaction, or filter results.  I recommend reading the book database internals or desinging data intensive applications for ideas of where you may want to take the project
