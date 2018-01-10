#!/usr/bin/python
# -*- coding: UTF-8 -*-
import csv
import numpy as np
from scipy import stats

def column(matrix, i):
    return [row[i] for row in matrix]


data = list(csv.reader(open('fb15k_all_literal_facts_numbers.txt', 'rb'), delimiter='\t'))
values = column(data,2)
a=np.array(values).astype(np.float)
b=stats.zscore(a)

for i in range(0,len(data)-1):
	data[i][2]=str(b[i])

with open('fb15k_normalized_date.csv', 'w') as file:
    file.writelines('\t'.join(i) + '\n' for i in data)
