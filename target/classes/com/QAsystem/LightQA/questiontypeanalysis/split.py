#!/usr/bin/python3
#coding:utf-8'

import random

if __name__ == "__main__":
    idxSet = set()
    while 1:
        a = random.randomint(1,6000)
        if a not in idxSet:
            idxSet.add(a)
        if len(idxSet) == 100:
            break;
    
