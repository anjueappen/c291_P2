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


Phase 3:
