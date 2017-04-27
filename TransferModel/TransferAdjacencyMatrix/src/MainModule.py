'''
Created on Dec 11, 2014

@author: US
'''
import csv
import operator

def main ():
    
    inputFilePath = "files\\transferHistory.csv"
    outputFilePath = "C:\\Users\\US\\workspaceProfRitter\\TransferAdjacencyMatrix\\src\\transferMatrix.csv" 
    cr = csv.reader(open(inputFilePath,"rb"))
    
    fullInput = []
    index = 0 
    for row in cr:
        if index !=0:
            fullInput.append(row)
        index +=1    
        
    TransfersFreq = {}
    unqGroups = {}
    for i in range(len(fullInput)-1):

            
        if fullInput[i][0] == fullInput [i+1][0]:
            
            
            if fullInput[i][1] in unqGroups: 
                unqGroups[fullInput[i][1]] +=1
                
            else:
                unqGroups[fullInput[i][1]] =1
            
            if fullInput[i+1][1] not in unqGroups: 
                unqGroups[fullInput[i+1][1]] =1
             
            
            
            
            key = fullInput[i][1]+"#"+fullInput[i+1][1]
            if key in TransfersFreq:
                TransfersFreq [key] +=1
            else:
                TransfersFreq [key] =1
    
    
 
    """Sorting unqGroups for headers"""
    sorted_unqGroups = sorted(unqGroups.items(), key=operator.itemgetter(1), reverse=True)
    #print sorted_unqGroups
    
    header = [" "]
    for x in sorted_unqGroups:
        header.append(x[0]) 
    print header

        
                
    #Output Printing
    c = csv.writer(open(outputFilePath, "wb"))
    c.writerow(header)
    for x in sorted_unqGroups:
        currentRow = [x[0]]
        for y in sorted_unqGroups:
            
            subj = x[0]+"#"+y[0]
            if subj in TransfersFreq:
                currentRow.append(TransfersFreq[subj])
            else:
                currentRow.append('0')        
        c.writerow(currentRow)
    
    
    
        
    
if __name__ == '__main__':
    main ()
    

