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
    
    filename = "./files/LoadChart.csv"
    #filename = "trade.csv"
    
    cr = csv.reader(open(filename,"rb"))
    
    vector = []
    index = 0
    for row in cr:
        if index != 0:    
            vector.append(row)
        index +=1
        
    print vector
    
    
    fig, ax = plt.subplots()
    ax.tick_params('both', length=10, width=1, which='major')
    ax.set_xlabel(r'Incident Load', fontsize = axisfontsize)
    
    ax.set_ylabel(" ", fontsize = axisfontsize)
    
    ax.xaxis.set_major_locator(MultipleLocator(4))
    ax.xaxis.set_major_formatter(FormatStrFormatter('%.0f'))
    
    ax.set_ylabel(r'', fontsize = axisfontsize)
    
    ax.yaxis.set_major_locator(MultipleLocator(0.1))
    ax.yaxis.set_major_formatter(FormatStrFormatter('%.1f'))
    
    
    
    ax.set_ylim([0,1])
    ax.set_xlim([1,50])
    
    colorSet = ["black", "gray"]
    markerSet = ["v", "o"]
    LabelSet = ["Coverage of Full DataSet", "frequency"]


    f1 = [2*x+1 for x in range(49)]
        
    f2 = []
    f22= []
        
    for point in vector:
                            
        #f1.append((point[0]))                
        f2.append(float(point[1]))
              
        #f1.append(int(point[0]))
        f22.append(float(point[2]))
            
                
    line1 = plt.plot(f1, f2, label = LabelSet[0] ,color=colorSet[0], marker = markerSet[0], linestyle='--', linewidth=3.0, ms=15)
    line2 = plt.plot(f1, f22, label = LabelSet[1] ,color=colorSet[1], marker = markerSet[1], linestyle='--', linewidth=3.0, ms=8)
                   
      
            
                
    plt.ylim([-0.03,0.7])
    plt.xlim([0.83,100])
    
    plt.legend( (line1[0], line2[0]), ('Average Time to Respond (Resolve/Transfer)', 'Relative Frequency of Response'), loc='upper center', bbox_to_anchor=(0.50, 1.15), fancybox=True, shadow=True, ncol=1, prop={'size':25})
     
    plt.show()
   
    


if __name__ == '__main__':
    main()