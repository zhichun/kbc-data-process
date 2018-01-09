#!/usr/bin/python
# -*- coding: UTF-8 -*-
def date(str):
	yymmdd=str.split('-')
	if len(yymmdd)==3:
		yy=yymmdd[0]
		mm=yymmdd[1]
		dd=yymmdd[2]
		return (int(yy)-1900)*365+(int(mm)*30)+int(dd)


def yearMotnth(str):
	yymmdd=str.split('-')
	if len(yymmdd)==2:
		yy=yymmdd[0]
		mm=yymmdd[1]
		return (int(yy)-1900)*365+(int(mm)*30)
		
def year(str):
	return (int(str)-1900)*365

def datetime(str):
	index=str.find('T')
	if index>0:
		return date(str[0:index-1])			

def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        pass
    return False
#peocess data, time
#eliminate "
#true or false

f1 = open("fb15k_all_literal_facts_numbers.txt",'w')
f2 = open("fb15k_all_literal_facts_others.txt",'w')
f3 = open("fb15k_all_literal_facts_dates.txt",'w')
for line in open('fb15k_all_literal_facts_cleaned_newformat.txt'):
	try:
		notconverted = True
		a = line.split('\t')
		if len(a)==3:
			value = a[2]
			value=value.replace('\"','')
			value=value.replace('\n','')
			index = a[2].find('^^<http://www.w3.org/2001/XMLSchema#date>')
			if (notconverted and index>=0):
				tempdate = a[2][1:index-1]
				value = date(tempdate)
				notconverted = False
				f3.write(a[0]+'\t'+a[1]+'\t'+str(value)+'\n')
				continue
			index = a[2].find('^^<http://www.w3.org/2001/XMLSchema#gYearMonth>')
			if (notconverted and index>=0):
				tempdate = a[2][1:index-1]
				value=yearMotnth(tempdate)
				notconverted = False
				f3.write(a[0]+'\t'+a[1]+'\t'+str(value)+'\n')
				continue
			index = a[2].find('^^<http://www.w3.org/2001/XMLSchema#gYear')
			if (notconverted and index>=0):
				tempdate = a[2][1:index-1]
				value=year(tempdate)
				notconverted = False
				f3.write(a[0]+'\t'+a[1]+'\t'+str(value)+'\n')
				continue
			index = a[2].find('^^<http://www.w3.org/2001/XMLSchema#dateTime>')
			if (notconverted and index>=0):
				tempdate = a[2][1:index-1]
				value=datetime(tempdate)
				notconverted = False
			if a[2].find('true')>=0:
				value = 1
			if a[2].find('false')>=0:
				value = 0

			if is_number(value):
				f1.write(a[0]+'\t'+a[1]+'\t'+str(value)+'\n')
			else:
				f2.write(a[0]+'\t'+a[1]+'\t'+str(value)+'\n')
	except ValueError:
		continue
	except TypeError:
		print value
		continue


f1.close()
f2.close()