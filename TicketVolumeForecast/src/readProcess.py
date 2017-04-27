'''
Created on Dec 15, 2015

@author: mohark1
'''

import csv
import os
from sys import exit
import operator

headings = {0:"Date", 1:"FW requests",  2:"DNS requests", 3:"Server Prov Request"}
months = {'january':1, 'february':2, 'march':3, 'april':4 ,'may': 5,'june':6,'july':7,'august':8,'september':9,'october':10,'november':11,'december':12}

class Reader:
    
    
    def __init__(self, inputP):
        
        self.inputPath = inputP
        
    
    def errorOracle(self, errorCode, message):
        if errorCode>0 and errorCode<5:
            print "Error "+str(errorCode)+": Heading in the file is undefined: \'" +message[0]+ "\' doesn't match \'"+message[1]+"\'"
        elif errorCode==5:
            print "Error "+str(errorCode)+": Input file should have exactly four fields: \"Date\", \"FW requests\",  \"DNS requests\", \"Server Prov Request\" "
        elif errorCode==6:
            print "Error "+str(errorCode)+": Month names wrong: has to be among 'January', 'February', 'March', 'April','May','June','July','August','September','October','November','December' "
            print "\'"+message[0]+"\'"+" is wrong!!"  
        elif errorCode==7:
            print "Error "+str(errorCode)+": Months not in the increasing order."
            print message[0]+" & "+message[1]+" are in a wrong sequence"
        
        elif errorCode==8:
            print "Error "+str(errorCode)+": Date format is only accepted as Month-YY as in January-14"
            print "Error occurred around "+"\'"+message[0]+"\'"
        elif errorCode==9:
            print "Error "+str(errorCode)+": Wrong specification of the year near "+ "\'"+message[0]+"\'"
        elif errorCode==10:
            print "Error "+str(errorCode)+": wrong year sequence near "+message[0]+ " "+message[1] +"-"+message[2]
        
        elif errorCode==11:
            print "Error "+str(errorCode)+": wrong year sequence near "+"\'"+message[0]+"-"+message[1]+"\'"+" & "+"\'"+message[2]+"\'" 
        
        elif errorCode==12:
            print "Error "+str(errorCode)+": Empty line in the file"
        elif errorCode==13:
            print "Error "+str(errorCode)+": Occurred near \'"+ message[1]+"\'"+ "\nOnly digits or empty string are accepted for the numerical field: "+"\'"+message[0]+"\'"
        
        else:
            print "Generic Error: please fix the input file"
        
        os.system("pause")
        exit()
    
    def errorHanding(self):
        raw_data = []
        cr = csv.reader(open(self.inputPath,"rb"))
        index = 0
        monthStack = []
        yearStack= []
        
        for row in cr:
            index +=1
            if index ==1:
                if len(row)!=4:
                    self.errorOracle(5, [])
                if (row[0] == headings[0] and row[1] == headings[1] and row[2] ==headings[2] and row[3]==headings[3]):
                    continue
                else:
                    self.headingErrorHandler(row, headings)
            else:
                if not(len(row)):
                    self.errorOracle(12, [])
                    
                if len(row[0].split('-'))!=2:
                    message = [str(row[0])]
                    self.errorOracle(8, message)
                    
                month = row[0].split('-')[0].lower()
                year = row[0].split('-')[1]
                
                
                if month not in months:
                    message = [str(row[0])]
                    self.errorOracle(6, message)       
                
                if (not year.isdigit()) or int(year)>99:
                    message = [str(row[0])]
                    self.errorOracle(9, message)
                
                if len(monthStack)>1:
                    if (months[month]-months[monthStack[-1]] !=1) and (months[month]-months[monthStack[-1]] !=-11):
                        message= [str(monthStack[-1]), str(month)]
                        self.errorOracle(7, message)
                         
                    
                        
                    else:
                        if months[month] ==1 and months[monthStack[-1]] ==12:
                            if int(year)-int(yearStack[-1])!=1:
                                message = [str(row[0]), str(monthStack[-1]), str(yearStack[-1])]
                                self.errorOracle(10, message)
                        else:
                            if int(year)-int(yearStack[-1])!=0:
                                message = [str(monthStack[-1]), str(yearStack[-1]), str(row[0])]
                                self.errorOracle(11, message)
                         
                
                monthStack.append(month)
                yearStack.append(year)
                
                for i in range(3):
                    if (not row[i+1].isdigit()) and (row[i+1]!=""):
                        message = [headings[i+1], str(row[i+1])]
                        self.errorOracle(13, message)
                    
                
                raw_data.append((row[1],row[2], row[3], row[0]))
                
        print "Step 1: Input file is verified."        
        
        #Setting the month_to_predict
        thi_month = raw_data[-1][-1].split('-')[0].lower()
        sorted_months = sorted(months.items(), key=operator.itemgetter(1))
        ind = 0
        nex_month = ''
        while ind<len(sorted_months):
            if sorted_months[ind][0] == thi_month:
                nex_month = sorted_months[(ind+1)%12][0].title()
                break
            ind+=1
        
        if months[nex_month.lower()] == 1:
            month_to_predict = nex_month+'-'+str(int(raw_data[-1][-1].split('-')[1])+1)
        else:
            month_to_predict = nex_month+'-'+raw_data[-1][-1].split('-')[1]
            
        
        
        
        return raw_data, month_to_predict
        
    def headingErrorHandler(self, row, headings):
        message = []
        for i in range(4):
            if row[i] != headings[i]:
                message = [str(row[i]), str(headings[i])]
                self.errorOracle(i+1, message)
    