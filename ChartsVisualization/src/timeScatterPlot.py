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


matplotlib.rc('font', family='Times New Roman') 
matplotlib.rc('font', serif='Helvetica Neue') 
matplotlib.rc('text', usetex='false') 
matplotlib.rcParams.update({'font.size': 35})
axisfontsize = 45

matplotlib.rcParams['figure.figsize'] = 15, 24
matplotlib.rcParams['xtick.major.pad']='10'
from matplotlib.ticker import MultipleLocator, FormatStrFormatter



def main ():
    
    filename = "MTTR.csv"
    
    cr = csv.reader(open(filename,"rb"))
    
    vector = []
    index = 0
    for row in cr:
        if index != 0:    
            vector.append(row)
        index +=1
        
    print vector
    
    colorSet = ["r","y","b","g"]
    markerSet = ["x","*",".","+"]



    fig, ax = plt.subplots()
    ax.set_xlabel('Expected Time to Resolve (ETTR)', fontsize= axisfontsize)
    ax.set_ylabel('Actual Time to Resolve', fontsize= axisfontsize)
    ax.yaxis.set_major_locator(MultipleLocator(1000))
    ax.yaxis.set_major_formatter(FormatStrFormatter('%d'))
    ax.tick_params('both', length=10, width=1, which='major')
    
    
    x = []
    for k in range(4):
        f1 = []
        f2 = []
        for point in vector:
            if point[2] == str(k+1):
                f1.append(point[1])
                f2.append(point[0])
        x.append(plt.scatter(f1, f2, color=colorSet[k], marker = markerSet[k], s=380))
        plt.ylim([0,12000])
        plt.xlim([0,7500])
        
    plt.legend( tuple(x),('P1', 'P2', 'P3', 'P4'), loc='upper center', bbox_to_anchor=(0.5, 1.12), fancybox=True, shadow=True, ncol=4, prop={'size':45})

        
    plt.show()
    
    
if __name__ == '__main__':
    main()