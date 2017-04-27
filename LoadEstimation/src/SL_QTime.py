'''
Created on May 5, 2015

@author: mohark1 and velidk1
'''
import datetime
import time
from time import mktime
#from copy import deepcopy

SL_start = datetime.datetime(1988,1,1,6,0,0)
SL_end = datetime.datetime(1988,1,1,17,30,0)


def time_in_range(t):            
        if t.weekday() == 5 or t.weekday() == 6:
            return False
        elif SL_start <= SL_end:
            return SL_start.time() <= t.time() <= SL_end.time()
        else:
            return SL_start.time() <= t.time() or t.time()<= SL_end.time()
        
def Round(t1):
    if not time_in_range(t1):
        if t1.time() < SL_start.time():
                t1 = t1.replace(hour = 6, minute = 0)
        elif t1.time() > SL_end.time():
                t1 = t1.replace(hour=17,minute=30)
                #t1 = t1 + datetime.timedelta(1)  made a change which rounds greater value to 5:30p and lesser value to 6:00a
        if t1.weekday() == 5:
                t1 = t1 - datetime.timedelta(1)
                t1 = t1.replace(hour=17,minute=30)
        elif t1.weekday() == 6:
                t1 = t1 - datetime.timedelta(2)
                t1 = t1.replace(hour=17,minute=30)   
    return t1

def calculate_delta(t3,t4):     
    if t3 > t4:
        return -1*calculate_delta(t4,t3)
    else:
        if not time_in_range(t3):
            t3 = Round(t3)
        if not time_in_range(t4):
            t4 = Round(t4)
        if not ((t3.day == t4.day) and (t3.month == t4.month) and (t3.year == t4.year)):
            end1 = SL_end
            end1 = end1.replace(year=t3.year,month=t3.month,day= t3.day)
            part1 = end1 - t3
            start1 = SL_start
            start1 = start1.replace(year=t4.year,month=t4.month,day= t4.day)
            part2 = t4 - start1
            fromdate = datetime.date(t3.year,t3.month,t3.day)
            todate = datetime.date(t4.year,t4.month,t4.day)
            daygenerator = (fromdate + datetime.timedelta(x+1) for x in range((todate-fromdate).days))
            part3 = sum(1 for day in daygenerator if day.weekday() < 5) # number of days
            
            p1min = part1.total_seconds()/60
            p2min = part2.total_seconds()/60
            p3min = (part3 - 1) * 690
            
            totalmin = p1min+p2min+p3min
            return totalmin
        else:   
            delta = t4-t3
            return float(delta.seconds/60)  



def SL_timeDifference(TimeString1, TimeString2 ):

    first_time_object = time.strptime(TimeString1, "%m/%d/%y %H:%M")
    second_time_object = time.strptime(TimeString2, "%m/%d/%y %H:%M")
    
    dt_first = datetime.datetime.fromtimestamp(mktime(first_time_object))
    dt_second = datetime.datetime.fromtimestamp(mktime(second_time_object))
    
    result = calculate_delta(dt_second,dt_first)
    return result    
"""
def main():
    x = SL_timeDifference("5/5/15 09:11", "4/30/15 13:01" )
    #t1 = datetime.datetime(2015,5,4,5,30,0)
    #t2 =datetime.datetime(2015,5,5,9,11,0)
    
    #print calculate_delta(t1,t2)
    print x
    
if __name__ == '__main__':
    main()
"""