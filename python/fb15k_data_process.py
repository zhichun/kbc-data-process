#!/usr/bin/python
# -*- coding: UTF-8 -*-

entities = set()
nb = 0
for line in open('fb15k_all_relational_facts.txt'):
	a = line.split('\t')
	nb=nb+1
	if len(a)==3:
		entities.add(a[0])
		entities.add(a[2])
 
f3 = open("fb15k_types.tsv",'w')
for line2 in open('../freebase-types-rdf.tsv'):
	a = line2.split('\t')
	if a[0] in entities:
		f3.write(line2)
f3.close()
