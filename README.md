# Project 4 - BTree

Authors: Phillip Bruce, Wesley Brown

CS321 - Dr. Yeh

## Overview

This is an implementation of a BTree data structure. It is meant to store a file containing gene bank information (i.e. strands of DNA) in an efficient manner.

## BTreeFile Layout:

Our BTreeFile has two major parts in which data is stored.

The first section of our file holds the basic data which is needed by the program, i.e. k, degree, root node pointer, and total number of nodes, these are all stored within the first four lines as well as using 44 bytes for all the data.

The second section of our file holds the nodes which store all the data read in from the .gbk file. For this we set up an outline for how to write each node to the file.
```
Self-pointer (Number of Bytes deep)
Is Leaf (1 or 0)
Parent Pointer (Number of Bytes) (if this is root node use -1)
Number of Objects Stored
Number of children
degree + 1 child pointers (These are all the pointer as given above)
 |
 |
 |
 |
 |
\/
degree (t) possible stored objects/values 
(the data is stored as frequency value)
 |
 |
 |
 |
 |
\/

An Example of the File made by CreateBTree using k=7 degree=7 as below(this is only part of it):
         7
         7
        44
      1555
        44
0
        -1
         1
         2
       696
      1348
        -1
        -1
        -1
        -1
        -1
        -1
         2                                                 01110101001100
         9                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
       696
0
        44
         1
         2
      2000
      2652
        -1
        -1
        -1
        -1
        -1
        -1
         2                                                 00110101110101
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
      1348
0
        44
         1
         2
      3304
      3956
        -1
        -1
        -1
        -1
        -1
        -1
         2                                                 11000100000110
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
        -1                                                             -1
```
## Runtimes

Additionally, here are some runtimes given different circumstances:

```
k = 7
degree = 48 (optimal degree)
gbk file = test3.gbk
```

GeneBankCreateBTree:
- without cache: 96922ms
- with cache of size 100: 93501ms
- with cache of size 500: 69025ms
- with cache of size 1000: 61693ms

GeneBankSearch:
- without cache: 13073ms
- with cache of size 100: 370ms
- with cache of size 500: 365ms
- with cache of size 1000: 356ms
