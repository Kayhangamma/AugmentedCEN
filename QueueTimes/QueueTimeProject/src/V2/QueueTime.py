'''
Created on Jul 25, 2014

@author: velidk1
'''
import datetime
import csv
class QueueTime:
    start = datetime.datetime(1991,1,1,6,0,0)
    end = datetime.datetime(1991,1,1,17,30,0)
    
    
    def time_in_range(self,t):            
            if t.weekday == 5 or t.weekday == 6:
                return False
            elif self.start <= self.end:
                return self.start.time() <= t.time() <= self.end.time()
            else:
                return self.start.time() <= t.time() or t.time()<= self.end.time()
    def round(self,t1):
            if not self.time_in_range(t1):
                if t1.time() < self.start.time():
                        t1 = t1.replace(hour = 6, minute = 0)
                elif t1.time() > self.end.time():
                        t1 = t1.replace(hour=17,minute=30)
                        #t1 = t1 + datetime.timedelta(1)  made a change which rounds greater value to 5:30p and lesser value to 6:00a
                if t1.weekday() == 5:
                        t1 = t1 - datetime.timedelta(1)
                        t1 = t1.replace(hour=17,minute=30)
                elif t1.weekday() == 6:
                        t1 = t1 - datetime.timedelta(2)
                        t1 = t1.replace(hour=17,minute=30) 
                else:
                    pass
                return t1
    
    def calculate_delta(self,t3,t4):     
            if t3 > t4:
                return float(0)
            else:
                if not self.time_in_range(t3):
                    t3 = round(t3)
                if not self.time_in_range(t4):
                    t4 = round(t4)
                if not ((t3.day == t4.day) and (t3.month == t4.month) and (t3.year == t4.year)):
                    end1 = self.end
                    end1 = end1.replace(year=t3.year,month=t3.month,day= t3.day)
                    part1 = end1 - t3
                    start1 = self.start
                    start1 = start1.replace(year=t4.year,month=t4.month,day= t4.day)
                    part2 = t4 - start1
                    fromdate = datetime.date(t3.year,t3.month,t3.day)
                    todate = datetime.date(t4.year,t4.month,t4.day)
                    daygenerator = (fromdate + datetime.timedelta(x+1) for x in range((todate-fromdate).days))
                    part3 = sum(1 for day in daygenerator if day.weekday() < 5) # number of days
                    
                    p1min = part1.total_seconds()/60
                    p2min = part2.total_seconds()/60
                    p3min = (part3 - 1) * 510
                    
                    totalmin = p1min+p2min+p3min
                    return totalmin
                else:   
                    delta = t4-t3
                    return float(delta.seconds/60)  
                
        
    
    def bigfunc(self,paths):
        line = []
        line2 = []
        
        i=0
        
        timeDict = {}
        grpDict = {}
        
        sample = open(paths[0],'r');
        resultsFile = open("1st_half.csv","wb")
        wrobj = csv.writer(resultsFile,dialect='excel')
        sv1 = csv.reader(sample,delimiter=',');
                
        for each in sv1:
            line.append(each)
        print len(line)
        
        
        error = []
        for i in range(len(line)):
            if i >= 1:
                try:            
                    line[i][3] = datetime.datetime.strptime(line[i][3],"%m/%d/%y %H:%M")
                    line[i][4] = datetime.datetime.strptime(line[i][4],"%m/%d/%y %H:%M") 
                except ValueError:            
                    try:
                        line[i][3] = datetime.datetime.strptime(line[i][3],"%m/%d/%Y %H:%M")
                        line[i][4] = datetime.datetime.strptime(line[i][4],"%m/%d/%Y %H:%M")
                    except ValueError:
                        print 'error'+ str(line[i][4])
                        error.appself.end(i)
                
        currentInc = line[1][0]
        
        
        
        
        
        
        
              
        
        
        p1 = line[1][3]
        currentGrp = line[1][1]
        tempGrp = []
        tempGrp.appself.end(currentInc)
        currIncP = 0
        for i in range(len(line)):
            if i >=1:                        
                    if line[i][0] != currentInc:
                        if currIncP >= 2:
                            p2 = line[i-2][3]
                            if p1 == p2:
                                p2 = line[i-3][3]
                            else:
                                p2 = line[i-2][3]
                                delta = self.calculate_delta(p1, p2)
                                tempGrp.appself.end(currentGrp)
                                tempGrp.appself.end(delta)
                            #tempGrp.appself.end([currentGrp,p1,p2,delta])   
                        else:
                            print 'true'
                            p2= line[i-1][3]
                            delta1 = self.calculate_delta(p1, p2)
                            tempGrp.appself.end(currentGrp)
                            tempGrp.appself.end(delta1)
                            #tempGrp.appself.end([currentGrp,p1,p2,delta1])
                        wrobj.writerow(tempGrp)
                        #ticketArray.appself.end([currentInc,tempGrp])
                        currentInc = line[i][0]
                        tempGrp = []
                        # added this line
                        tempGrp.appself.end(currentInc)
                        # added this line
                        currIncP = 1
                        p1 = line[i][3]
                        currentGrp = line[i][1]
                    else:
                        currIncP += 1 
                    if line[i][1] != currentGrp and line[i][0] == currentInc:
                        p2 = line[i-1][3]
                        if p1 == p2:
                            p2 = line[i][3]
                        delta2 = self.calculate_delta(p1, p2)
                        tempGrp.appself.end(currentGrp)
                        tempGrp.appself.end(delta2)
                        #tempGrp.appself.end([currentGrp,p1,p2,delta2])
                        currentGrp = line[i][1]
                        p1 = line[i][3]
                    else:
                        pass
              
        
        
        # next step
        
        fileopen = open("1st_half.csv",'r')
        sv2 = csv.reader(fileopen,delimiter=',')
        
        for each in sv2:
            line2.appself.end(each)
            
        for i in range(len(line2)):
            temp = []
            temp2 = []
            for j in range(len(line2[i])-1):
                if j == 0:
                    temp.appself.end(line2[i][2])
                    if line2[i][j] not in temp2:
                        temp2.appself.end(line2[i][j])
                    else:
                        pass
                elif j*2 <= len(line2[i]-1):
                    j *= 2
                    temp.appself.end(line2[i][j])
                    j += 1
                    if line2[i][j] not in temp2:
                        temp2.appself.end(line2[i][j])
                    else:
                        pass
        
            timeDict[line2[i][0]] = temp
            grpDict[line2[i][0]] = temp2


    def __init__(self):
        '''
        Constructor
        '''
        