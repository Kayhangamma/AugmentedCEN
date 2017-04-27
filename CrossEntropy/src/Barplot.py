'''
Created on Oct 12, 2016

@author: US
'''

import numpy as np
import matplotlib.pyplot as plt
import csv
from matplotlib.ticker import NullFormatter
import matplotlib
    

   
matplotlib.rc('font', family='Times New Roman') 
matplotlib.rc('font', serif='Helvetica Neue') 
matplotlib.rc('text', usetex='false') 
matplotlib.rcParams.update({'font.size': 35})
axisfontsize = 45


matplotlib.rcParams['figure.figsize'] = 20, 13
matplotlib.rcParams['xtick.major.pad']='10'
from matplotlib.ticker import MultipleLocator, FormatStrFormatter



def main():
    
    #!/usr/bin/env python
    # a bar plot with errorbars


    filename = "C:\\Kayhan\\OS University\\Research\\Jay\\TransferGroups\\Data\\2016\\weekly-2016\\Full-Data-Integration\\Time Modeling Data\\R-NR\\scikitModels\\Second\\ce\\Bar_Chart_data.csv"
    
    cr = csv.reader(open(filename,"rb"))
    
    vector1 = []
    vector2 = []
    vector3 = []
    index = 0
    for row in cr:
        if index != 0:    
            #print row
            vector1.append(row[0])
            vector2.append(float(row[1]))
            vector3.append(float(row[2]))
        index +=1
    
    
    N = len(vector1)

    
    ind = np.arange(N)  # the x locations for the groups
    width = 0.35       # the width of the bars
    
    
    
    fig, ax = plt.subplots()
    rects1 = ax.bar(ind, vector2, width, color='lightgray')
    

    rects2 = ax.bar(ind + width, vector3 , width, color='dimgray')
    
    # add some text for labels, title and axes ticks
    #ax.set_ylabel('Scores')
    #ax.set_title('Normalized Cross Entropy')
    ax.set_xticks(ind + width)
    ax.set_xticklabels(tuple(vector1))
    
    ax.yaxis.set_major_locator(MultipleLocator(0.10))
    
    ax.legend((rects1[0], rects2[0]), ('Normalized Mean Squared Error', 'Breach Ratio'), loc='upper center', bbox_to_anchor=(0.38, 0.98), fancybox=True, shadow=True, ncol=1, prop={'size':45})
    
    #plt.legend( tuple(x),('SLT Breached', 'SLT Met'), loc='upper center', bbox_to_anchor=(0.78, 0.3), fancybox=True, shadow=True, ncol=1, prop={'size':45})
    
    
    def autolabel(rects):
        # attach some text labels
        for rect in rects:
            height = rect.get_height()
            #ax.text(rect.get_x() + rect.get_width()/2., 1.05*height,'%d' % int(height),ha='center', va='bottom')
    
    autolabel(rects1)
    autolabel(rects2)
    
    plt.ylim([0,1.03])
    
    #x = range(len(time))
    #plt.xticks(x,  time)
    locs, labels = plt.xticks()
    plt.setp(labels, rotation=90)
    #plt.plot(x, delay)
    
    #ax.set_xlabel('Normalized Cross Entropy', fontsize= axisfontsize)
    
    
    plt.show()


if __name__ == '__main__':
    main()