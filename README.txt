EXAMPLE OF HOW TO RUN:

Phase 1:
> cd src
> javac DataPrep.java
> cat ../res/10.txt | java DataPrep
// this creates the four files needed: reviews.txt, pterms.txt, rterms.txt, scores.txt

Phase 2:
// still in src folder
// if sorted files already exist, delete them first and then run this.
> sort --unique pterms.txt > sorted_pterms.txt
> sort --unique rterms.txt > sorted_rterms.txt
> sort --unique scores.txt > sorted_scores.txt
// reviews.txt is already sorted

// use Perl script provided from eclass
// if temp.txt already exists, try removing it and running the command again if it complains about file already existing
> chmod +x break.pl  // you may only need to do this once?
> cat sorted_scores.txt | ./break.pl > temp.txt
> db_load -c duplicates=1 -f temp.txt -T -t btree sc.idx

// to check index file
> db_dump -p sc.idx
 
Phase 3:
