# CMPUT291 Mini Project 2

If ssh-ing into ohaton, then don't forget to enter this:
```
export CLASSPATH=$CLASSPATH:.:/usr/share/java/db.jar
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/oracle/lib
```

## Phase 1:
```
cd src
javac DataPrep.java
cat ../res/10.txt | java DataPrep
```
This creates the four files needed: reviews.txt, pterms.txt, rterms.txt, scores.txt

## Phase 2:
still in src folder
use Perl script from eclass
```
chmod +x break.pl
```

If sorted_[x].txt, temp_[x].txt or [xx].idx files already exist, delete them first and then run these commands.

For reviews
```
cat reviews.txt | ./break.pl > temp_reviews.txt
db_load -f temp_reviews.txt -T -t hash rw.idx
```

For pterms
```
sort -t, -k1,1 -k2,2n --unique pterms.txt > sorted_pterms.txt
cat sorted_pterms.txt | ./break.pl > temp_pterms.txt 
db_load -c duplicates=1 -f temp_pterms.txt -T -t btree pt.idx
```

For rterms
```
sort -t, -k1,1 -k2,2n --unique rterms.txt > sorted_rterms.txt
cat sorted_rterms.txt | ./break.pl > temp_rterms.txt
db_load -c duplicates=1 -f temp_rterms.txt -T -t btree rt.idx
```
For scores
```
sort -t, -k1,1n, -k2,2n --unique scores.txt > sorted_scores.txt
cat sorted_scores.txt | ./break.pl > temp_scores.txt
db_load -c duplicates=1 -f temp_scores.txt -T -t btree sc.idx
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
