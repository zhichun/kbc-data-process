#!/usr/bin/python
# -*- coding: UTF-8 -*-

f = open("fb15k_all_literal_facts_dates_normalized.txt",'w')
max=0
min=0
for line in open('fb15k_all_literal_facts_dates.txt'):
	a = line.split('\t')
	if len(a)==3:
		value = float(a[2].replace('\n',''))
		if max<value:
			max=value
		if min>value:
			min=value
f = open("fb15k_all_literal_facts_dates_normalized.txt",'w')
for line in open('fb15k_all_literal_facts_dates.txt'):
	a = line.split('\t')
	if len(a)==3:
		value = a[2].replace('\n','')
		value2 = (float(value)-min)/(max-min)
		f.write(a[0]+'\t'+a[1]+'\t'+str(value2)+'\n')
f.close()
