'''
Created on Sep 3, 2014

@author: US
'''
import csv
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.ticker import NullFormatter
import csv
import math
import operator
import matplotlib
from __builtin__ import True
from _sqlite3 import Row


matplotlib.rc('font', family='Times New Roman') 
matplotlib.rc('font', serif='Helvetica Neue') 
matplotlib.rc('text', usetex='false') 
matplotlib.rcParams.update({'font.size': 35})
axisfontsize = 45

matplotlib.rcParams['figure.figsize'] = 20, 13
matplotlib.rcParams['xtick.major.pad']='10'
from matplotlib.ticker import MultipleLocator, FormatStrFormatter



def main ():
    
    filename = "ChartA_data.csv"
    
    cr = csv.reader(open(filename,"rb"))
    
    vector = []
    index = 0
    for row in cr:
        if index != 0:    
            #print row
            vector.append([float(row[3]),float(row[15]),row[22]])
        index +=1
        
    #print vector
    
    #colorSet = ["r","y","b","g"]
    colorSet = ["red",'g']
    #markerSet = ["o","v","o","*", "h", "^"]
    markerSet = ["o", "^"]
    markerSize = [300,500]
    #markerSize = [300,800,300,1200,800,800]
    #markerSet = ["x","*",".","+"]



    fig, ax = plt.subplots()
    ax.set_xlabel('Squared Error of NETTR ('+r'$min^2$'+')', fontsize= axisfontsize)
    ax.set_ylabel('Cross Entropy of Ticket ('+r'$bits$'+')', fontsize= axisfontsize)
    ax.yaxis.set_major_locator(MultipleLocator(0.10))
    ax.yaxis.set_major_formatter(FormatStrFormatter('%.2f'))
    ax.xaxis.set_major_locator(MultipleLocator(1.00))
    ax.xaxis.set_major_formatter(FormatStrFormatter('%.2f'))
    plt.xticks(rotation='vertical')
    
    ax.tick_params('both', length=10, width=1, which='major')
    
    #vert_line = 1.0
    #plt.vlines(x=1.0, ymin=0, ymax=1.0, color='black', zorder=2, linewidth=4)
    #plt.hlines(y=1.0, xmin=0, xmax=1.0, color='black', zorder=2, linewidth=4)
    #plt.hlines(y=1.0, xmin=1.0, xmax=5.0, color='red', linestyle='dashed', zorder=2, linewidth=4)
    #plt.vlines(x=1.0, ymin=1.0 , ymax=2.0, color='red', linestyle='dashed', zorder=2, linewidth=4)
    
    plt.plot([0,8], [0.2903,0.6447], color='black', zorder=2, linewidth=4)
    #plt.plot([1,2], [1,2], color='black', zorder=2, linewidth=4)
    # linestyle='dashed'
    #y = 0.0516x + 0.1993
    #y=0.0443x+0.2903
    
    x = []
    for k in range(2):
        f1 = []
        f2 = []
        for point in vector:
            if k==0 and point[2] == "SLT Breached":
                f1.append(point[0])
                f2.append(point[1])
            if k==1 and point[2] == "SLT Met":
                f1.append(point[0])
                f2.append(point[1])
            
        x.append(plt.scatter(f1, f2, facecolors=colorSet[k], marker = markerSet[k], edgecolors='black', s=markerSize[k]))
        
        #x.append(plt.scatter(f1, f2, color=colorSet[k], marker = markerSet[k], s=380))
        
        plt.xlim([0.0,8])
        plt.ylim([0,0.8])
    

    plt.legend( tuple(x),('SLT Breached', 'SLT Met'), loc='upper center', bbox_to_anchor=(0.78, 0.3), fancybox=True, shadow=True, ncol=1, prop={'size':45})

        
    plt.show()
    
            
    
    
if __name__ == '__main__':
    main()