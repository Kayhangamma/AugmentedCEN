'''
Created on Jul 11, 2016

@author: moosas1
'''
import Ticket
import SL_QTime

def createIndexOnTicketTransfers():
    #Create a null dictionary
    expertsToIncidents = {}
    
    #Read the input file 
    with open('files/Transfer Time Intervals Converted.txt') as f:
        content = f.readlines()
    content.remove(content[0])
    #Parse the file and fill the dictionary
    for line in content:
        parts = line.split()
        if parts[7] == 'null':
            continue
        incidents = []
        if expertsToIncidents.has_key(parts[1]):
            incidents = expertsToIncidents.get(parts[1])
        t = Ticket.Ticket(line)
        incidents.append(t)
        expertsToIncidents[parts[1]] = incidents
    return expertsToIncidents
  
def loadLogicalToActualConversion():
    #Create a null dictionary
    logicalToActual = {}
    
    #Read the input file 
    with open('files/convertedActualToLogicalTime.txt') as f:
        content = f.readlines()
        
    #Parse the file and fill the dictionary
    for line in content:
        parts = line.split()
        time = parts[0] + " " + parts[1]
        logicalToActual[int(parts[2])] = time
        
    return logicalToActual    
    
def calcualteExpectedNumberOfProcessedTickets(expertsToIncidents, logicalToActualTime, G, t1, t2, pr):
    load = 0.0
    incidents = expertsToIncidents.get(G)
    t1_actual = logicalToActualTime.get(t1)
    t2_actual = logicalToActualTime.get(t2)
    t2_t1 = SL_QTime.SL_timeDifference(t2_actual, t1_actual)
    
    if float(t2_t1)==0.00:
        return 0.00
    
    for t in incidents:
        if t.pr != pr:
            continue
        
        t_t1_actual = logicalToActualTime.get(t.t1)
        t_t2_actual = logicalToActualTime.get(t.t2)
        
        #Following are different rules to determine the overlap between time intervals
        if (t.t1 <= t1 and t.t2 > t1 and t.t2 <= t2):
            #load += (t.t2-t1)/float(t2-t1)
            load += SL_QTime.SL_timeDifference(t_t2_actual, t1_actual)/float(t2_t1)
        elif (t.t1 >= t1 and t.t2 <= t2):
            #load += (t.t2-t.t1)/float(t2-t1)
            load += SL_QTime.SL_timeDifference(t_t2_actual, t_t1_actual)/float(t2_t1)
        elif(t.t1 >= t1 and t.t1 < t2 and t.t2 >= t2):
            #load += (t2-t.t1)/float(t2-t1)
            load += SL_QTime.SL_timeDifference(t2_actual, t_t1_actual)/float(t2_t1)
        elif(t.t1 < t1 and t.t2 > t2):
            load += 1.0;
            
    return load




def main():
    import csv
    
    inputfilePath= "./files/input.csv"
    cr = csv.reader(open(inputfilePath,"rb"))
    outputPath = "./files/output.csv"
    
    #Sample run of program
    #1. Load converted time
    logicalToActualTime = loadLogicalToActualConversion()
    #2. Create Index
    expertsToIncidents = createIndexOnTicketTransfers()
    #calcualteExpectedNumberOfProcessedTickets(expertsToIncidents, logicalToActualTime, "NI-AGENCY-DESKTOP", 34463, 35535, 3)

    index = 0
    c = csv.writer(open(outputPath, "wb"))
    c.writerow(["Transfer ID","Incident ID", "Transfer Group", "Q Load P1","Q Load P2","Q Load P3","Q Load P4"])
    
    
    
    for row in cr:    
        if index !=0:
            print index 
            transID = row[0]
            IncID = row[1]
            group = row[2]
            startTime = int(row[6])
            endTime = int(row[7])

            #3. Get estimated load
            #calcualteExpectedNumberOfProcessedTickets(expertsToIncidents, logicalToActualTime, "NI-CLAIMS-CLAIMCENTER-LVL3", 206537, 206873, 3)
            
            result = []
            result.append(transID)
            result.append(IncID)
            result.append(group)
            for i in range(4):
                result.append(calcualteExpectedNumberOfProcessedTickets(expertsToIncidents, logicalToActualTime, group, startTime, endTime, i+1))
            
            c.writerow(result)   
    
        
        index+=1

if __name__ == '__main__':
    main()

