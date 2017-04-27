'''
Created on Sep 3, 2014

@author: US
'''

import csv


import matplotlib.pyplot as plt

import matplotlib.lines as mlines
import matplotlib.pyplot as plt

import matplotlib

matplotlib.rc('font', family='Times New Roman') 
matplotlib.rc('font', serif='Helvetica Neue') 
matplotlib.rc('text', usetex='false') 
matplotlib.rcParams.update({'font.size': 30})
axisfontsize = 40

matplotlib.rcParams['figure.figsize'] = 15, 11
matplotlib.rcParams['xtick.major.pad']='10'
from matplotlib.ticker import MultipleLocator, FormatStrFormatter



def main ():
    



    
    filename = "Precision.csv"
    
    cr = csv.reader(open(filename,"rb"))
    
    vector = []
    index = 0
    for row in cr:
        if index != 0:    
            vector.append(row)
        index +=1
        
    print vector
    
    
    fig, ax = plt.subplots()
    ax.set_ylabel('Overall R-Precision', fontsize = axisfontsize)
    ax.set_xlabel('Percentage of Total Training Instances', fontsize = axisfontsize)
    
    ax.xaxis.set_major_locator(MultipleLocator(10))
    ax.xaxis.set_major_formatter(FormatStrFormatter('%d'))
    
    ax.yaxis.set_major_locator(MultipleLocator(0.1))
    ax.yaxis.set_major_formatter(FormatStrFormatter('%.1f'))
    ax.tick_params('both', length=10, width=1, which='major')
    
    
    
    colorSet = ["g","b","r"]
    markerSet = ["v","o","*"]
    LabelSet = ["Flexible R-TRS Prediction","Strict R-TRS Prediction","Generative Greedy R-TRS Prediction"]

    for k in range(3):
        f1 = []
        f2 = []
        for point in vector:
            
            f1.append(point[3])
            f2.append(point[k])
            
        #plt.plot(f1, f2, label = LabelSet[k] ,color=colorSet[k], marker = markerSet[k])
        if k ==0:
            line1 = plt.plot(f1, f2, label = LabelSet[k] ,color=colorSet[k], marker = markerSet[k], linewidth=6.0, ms=15)
        elif k==1:
            line2 = plt.plot(f1, f2, label = LabelSet[k] ,color=colorSet[k], marker = markerSet[k], linewidth=6.0, ms=15)
        else:
            line3 = plt.plot(f1, f2, label = LabelSet[k] ,color=colorSet[k], marker = markerSet[k], linewidth=6.0, ms=23)
        
        
        
        plt.ylim([0,1.1])
        #plt.xlim([0,12000])
    
    #plt.legend(ncol=1, loc = 'upper right', fontsize=15)
    plt.legend( (line1[0], line2[0], line3[0]), ('Flexible R-TRS Prediction', 'Strict R-TRS Prediction', 'Generative Greedy R-TRS Prediction'), loc='upper center', bbox_to_anchor=(0.5, 1.01), fancybox=True, shadow=True, ncol=1, prop={'size':30})

    
    plt.show()
   
    
    


    
if __name__ == '__main__':
    main()
    
    


