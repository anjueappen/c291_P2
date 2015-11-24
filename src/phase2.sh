#!/bin/bash

echo "This script implements Phase 2 of the project."

rm -f temp_reviews.txt
rm -f temp_pterms.txt
rm -f temp_rterms.txt
rm -f temp_scores.txt

rm -f rw.idx
rm -f pt.idx
rm -f rt.idx
rm -f sc.idx

chmod +x break.pl

cat reviews.txt | ./break.pl > temp_reviews.txt
db_load -f temp_reviews.txt -T -t hash rw.idx

sort -t, -k1,1 -k2,2n --unique pterms.txt > sorted_pterms.txt
cat sorted_pterms.txt | ./break.pl > temp_pterms.txt 
db_load -c duplicates=1 -f temp_pterms.txt -T -t btree pt.idx

sort -t, -k1,1 -k2,2n --unique rterms.txt > sorted_rterms.txt
cat sorted_rterms.txt | ./break.pl > temp_rterms.txt
db_load -c duplicates=1 -f temp_rterms.txt -T -t btree rt.idx

sort -t, -k1,1n -k2,2n --unique scores.txt > sorted_scores.txt
cat sorted_scores.txt | ./break.pl > temp_scores.txt
db_load -c duplicates=1 -f temp_scores.txt -T -t btree sc.idx

echo "Deleting temporary files to clean up."
rm -f temp_reviews.txt
rm -f temp_pterms.txt
rm -f temp_rterms.txt
rm -f temp_scores.txt

rm -f sorted_pterms.txt
rm -f sorted_rterms.txt
rm -f sorted_scores.txt

echo "Phase 2 done. Index files generated."
