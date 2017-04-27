'''
Created on Jun 30, 2014

@author: mohark1
'''
import csv

class fileWriter:
    
    def csvWrite (self, headings, data, filePath):
        c = csv.writer(open(filePath, "wb"))
        c.writerow(headings)
        for li in data:
            c.writerow(li)
            
    
    def expandedCsvWrite(self, group_headings, incident_order,incident_path, time_intevals, FileOutPath):
        c = csv.writer(open(FileOutPath, "wb"))
        c.writerow(group_headings)
        ordered_groups = group_headings[2:]
        for inc in incident_order:
            currentRow = []
            currentRow.append(inc)
            currentRow.append(','.join(incident_path[inc]))
            
            #per each incident per each group accumate time
            accumulator = {}
            for i in range(len(time_intevals[inc])):
                curr_group = incident_path[inc][i]
                if curr_group not in accumulator:
                    accumulator[curr_group] = time_intevals[inc][i]
                else:
                    accumulator[curr_group] += time_intevals[inc][i]
                
            for g in ordered_groups:
                if g in accumulator:
                    currentRow.append(accumulator[g])
                else: 
                    currentRow.append('0')
            
            c.writerow(currentRow)
                          
            
        
        
        