# Result

98/100.

## Solution

The overall idea comes from [here](https://www-cs-faculty.stanford.edu/~zelenski/boggle/).
It's a `Board-driven search` with a few optimizations. Implementation steps:

1. Create trie for dictionary.
2. Run DFS starting from each cell and for each word with length >= 3 to check if
   current letters form existing prefixes in the dictionary. If a word already is in
   a dictionary, put the word into the result set.

P.S. I created modified trie where short keys are in R-way
tree and long in the ternary search tree. According to my measurements, it reduces memory
usage by ~30% for the default dictionary. But it's not really about making code
faster, so it's not really used.

## How to make 100/100

The idea here is in using DFS along with trie. The current code always looks up
for `contains`/`prefix` from the beginning of the trie, while it's evident that
if we'll continue from the node we made the previous iteration, then subsequent
check will use O(1) instead of O(n) where n is the current depth. I managed timing/most
of correctness tests to pass, but not all, so feel free to fix it.