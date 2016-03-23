# twitter-mining

1. Data Cleaning and Preprocessing.
==================================

You will be given a collection of raw data from Twitter
in the JSON format. This data is usually quite noisy, for example there are many copies of
a same tweet, while each tweet might contain text which is not relevant for our purposes.
After cleaning the data, you will build a graph representing the co-occurrences of relevant
“entities” in tweets.

2. Finding Dense Subgraphs
==========================

extracting dense subgraphs from the latter graph. To this end, you should adapt the
sequential greedy algorithm for finding dense subgraphs (which we presented during our
class on Wednesday) so as to 1) deal with weighted graphs, 2) find “small” subgraphs 3)
finding more than one dense subgraph, 4) deal with large graph, in particular try to give
a linear implementation (in the size of the input) of the algorithm.

3. Data Analysis
================

analyze the dense subgraphs that you found so as to find “interesting” information.
