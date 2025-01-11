*Before Run:
Check all needed files are in the same directory:
	1) reviews folder
	2) small folder
	3) imdb.vocab
	4) movie_review_BOWsmall.NB (preprocess file, output from pre-process.py)
	5) movie_review_BOWtest.NB (preprocess file, output from pre-process.py)
	6) movie_review_BOWtrain.NB (preprocess file, output from pre-process.py)
	7) NB.py
	8) pre-process.py
	9) small-test.txt

*How to Run:
	1) Run python3 or python NB.py in the terminal from the directory.
	2) User input is needed to choose for test which data set.
		1. test movie review corpus
		2. test small corpus
		0. exit program



*Output:
	1) File "movie-review.NB" contains the parameters of the movie review corpus test.
	2) File "movie-review-smalll.NB" contains the parameter of the small corpus test.
	3) File "output.txt", output all test cases and accuracy of the user test choice.


*all_output folder(include in tar, contains all output files):
	1) File "movie-review.NB" contains the parameters of the movie review corpus test.
	2) File "movie-review-smalll.NB" contains the parameter of the small corpus test.
	3) File "movie-review-small-part.NB" contains the test corpus parameter only.
	4) "output1.txt", output file of all cases and accuracy for moview review corpus test.
	5) "output2.txt", output file for small corpus test.
	