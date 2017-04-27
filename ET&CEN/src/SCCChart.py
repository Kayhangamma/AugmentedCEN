'''
Created on Jan 8, 2017

@author: US
'''

'''
Created on Sep 3, 2014

@author: US
'''
import matplotlib.pyplot as plt
import matplotlib
import csv

from matplotlib.ticker import MultipleLocator, FormatStrFormatter
matplotlib.rc('font', family='Times New Roman') 
matplotlib.rc('font', serif='Helvetica Neue') 
matplotlib.rc('text', usetex='false') 
matplotlib.rcParams.update({'font.size': 20})
axisfontsize = 25

matplotlib.rcParams['figure.figsize'] = 15, 11
matplotlib.rcParams['xtick.major.pad']='10'


def main ():
    
    filename = "resu.csv"
    #filename = "trade.csv"
    
    cr = csv.reader(open(filename,"rb"))
    
    vector = []
    x = []
    y1 = []
    y2 = []
     
    index = 0
    for row in cr:
        
        x.append(row[0])
        y1.append(row[1])
        y2.append(row[2])
        vector.append(row)
        index +=1
        
    print vector
    
    
    fig, ax = plt.subplots()
    ax.tick_params('both', length=10, width=1, which='major')
    ax.set_xlabel(r'Threshold for Minimum Transfer Occurrence', fontsize = axisfontsize)
    
    ax.set_ylabel(" ", fontsize = axisfontsize)
    
    ax.xaxis.set_major_locator(MultipleLocator(50))
    ax.xaxis.set_major_formatter(FormatStrFormatter('%.0f'))
    
    ax.set_ylabel(r'', fontsize = axisfontsize)
    
    ax.yaxis.set_major_locator(MultipleLocator(2000))
    ax.yaxis.set_major_formatter(FormatStrFormatter('%.1f'))
    
    
    
    ax.set_ylim([0,1])
    ax.set_xlim([1,18])
    
    colorSet = ["blue", "red"]
    markerSet = ["v", "o"]
    LabelSet = ["Coverage of Full DataSet", "frequency"]

    '''
    for k in range(2):
        f1 = []
        f2 = []
        for point in vector:
            if k==0:                
                f1.append(int(point[0]))
                f2.append(float(point[1]))
                line1 = plt.plot(f1, f2, label = LabelSet[0] ,color=colorSet[0], marker = markerSet[0], linestyle='-', linewidth=3.0, ms=0)
            else:            
                f1.append(int(point[0]))
                f2.append(float(point[2]))
                line2 = plt.plot(f1, f2, label = LabelSet[1] ,color=colorSet[1], marker = markerSet[1], linestyle='-', linewidth=3.0, ms=0)
                   
      
            
                
        plt.ylim([0,11000])
        plt.xlim([0,260])
    
    plt.legend( (line1[0], line2[0]), ('Empirical Probability of TRSs with Repeating Experts (given TRS Length)', 'Relative Frequency of TRS'), loc='upper center', bbox_to_anchor=(0.50, 1.15), fancybox=True, shadow=True, ncol=1, prop={'size':25})
    '''
    
    fig, ax1 = plt.subplots()
    
    
    #line1, = plt.plot(x, y1, linewidth=4.0)
    #line2, = plt.plot(x, y2, linewidth=4.0)
    
    
    ax2 = ax1.twinx()
    ax1.plot(x, y1, 'g-', linewidth=4.0)
    ax2.plot(x, y2, 'b-', linewidth=4.0)
    
    ax1.set_xlim([0,260])
    ax2.set_xlim([0,260])
    
    matplotlib.rcParams.update({'font.size': 20})
    
    
    matplotlib.rcParams['figure.figsize'] = 15, 11
    matplotlib.rcParams['xtick.major.pad']='10'
    
    
    
    ax1.set_xlabel('Threshold for Minimum Transfer Occurrence')
    ax1.set_ylabel('Average Diameter of SCCs', color='g')
    ax2.set_ylabel('Total Number of Edges', color='b')
    

    
    #plt.legend( (line1,line2 ), ('Average Diameter of SCCs', 'Total Edge Count'), loc='upper center', bbox_to_anchor=(0.50, 0.5), fancybox=True, shadow=True, ncol=2, prop={'size':25})
    
     
    plt.show()
   
    


if __name__ == '__main__':
    main()