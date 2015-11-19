# CMPUT291 Mini Project 2

If ssh-ing into ohaton, then don't forget to enter this:
```
export CLASSPATH=$CLASSPATH:.:/usr/share/java/db.jar
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/oracle/lib
```

OR you can run a script in the src file (this script can have more setup stuff if needed)
```
cd src
chmod +x init.sh
./init.sh
```

## Phase 1:
```
cd src
javac DataPrep.java
cat ../res/10.txt | java DataPrep
```
This creates the four files needed: reviews.txt, pterms.txt, rterms.txt, scores.txt

## Phase 2:
We will be using our Phase 2 script. This generates the index files (rw.idx, pt.idx, rt.idx, sc.idx) and cleans up all temporary files used!
```
chmod +x phase2.sh
./phase2.sh
```

To check any of the index files
```
db_dump -p [xx].idx
```

To test retrieving records from the database using a search key, run Phase2Test with [xx] being the name of the database index file you want to use. It will print out all the records from the database as Key | Data and then it will ask you for a search key. E to exit.

```
java Phase2Test [xx].idx
```
 
## Phase 3:
