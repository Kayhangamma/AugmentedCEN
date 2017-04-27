'''
Modified on Jul 2, 2015

@author: mohark1
'''



import csv
import re

watchDict = {"IM02090513","IM02091798","IM02087388","IM02117735","IM02144017","IM02150310","IM02170213","IM02198952","IM02197532","IM02233030","IM02238256","IM02243499","IM02239138","IM02257487","IM02274724","IM02276140","IM02281649","IM02284040","IM02285082","IM02284096","IM02289998","IM02320405","IM02324746","IM02355970","IM02369557","IM02391426","IM02377882","IM02406602","IM02414885"}


class readwrite:
    
    def remove_non_ascii(self, text):
        return re.sub(r'[^\x00-\x7F]+',' ', text)
        
    def readcsv(self, targetPath):
        cr = csv.reader(open(targetPath,"rb"))
        FullData = []
        counter = 0
        for row in cr:
            if counter == 0:
                counter +=1
                continue
            
            if row[0] in watchDict:
                continue
            ticketID = row[0]
            ticketDesc = self.remove_non_ascii(row[1])
            path = row[7]
            
            
            
            
            dataRow = [ticketID, ticketDesc, path]
            FullData.append(dataRow)
            
        return FullData
            
        
        