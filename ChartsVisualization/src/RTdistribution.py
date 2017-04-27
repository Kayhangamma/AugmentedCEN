"""
========================================
Bayesian Methods for Hackers style sheet
========================================

This example demonstrates the style used in the Bayesian Methods for Hackers
[1]_ online book.

.. [1] http://camdavidsonpilon.github.io/Probabilistic-Programming-and-Bayesian-Methods-for-Hackers/

"""
from numpy.random import beta
import matplotlib.pyplot as plt
import csv
import numpy as np
from numpy import array
import matplotlib


plt.style.use('bmh')


filename1 = "./files/TransferData.csv"

cr = csv.reader(open(filename1,"rb"))


filename2 = "./files/ResolutionData.csv"

cr2 = csv.reader(open(filename2,"rb"))


matplotlib.rc('font', family='Times New Roman') 
matplotlib.rc('font', serif='Helvetica Neue') 
matplotlib.rc('text', usetex='false') 
matplotlib.rcParams.update({'font.size': 20})
axisfontsize = 30


matplotlib.rcParams['figure.figsize'] = 18, 28



vector1 = []
vector2 = []
vector3 = []

index = 0
for row in cr:
    if index != 0:    
        #print row
        print index
        vector1.append(row[0])
        vector2.append(float(row[1]))
        #vector2.append(float(row[4]))
    index +=1
    

index = 0
for row in cr2:
    if index != 0:    
        #print row
        print index
        vector1.append(row[0])
        vector3.append(float(row[1]))
        #vector2.append(float(row[4]))
    index +=1


plt.style.use('bmh')

a = array(vector2)
b = array(vector3)

fig = plt.figure()
ax = fig.add_subplot(111)
#n, bins, patches = ax.hist(vector2, bins=20, normed=1, facecolor='green', alpha=0.75)
#weights=np.zeros_like(a) + 1. / a.size
#weights=np.zeros_like(b) + 1. / b.size

n, bins, patches =ax.hist(a, bins=50, weights=np.zeros_like(a) + 1. / a.size, facecolor='darkblue', normed=False, alpha=0.8, label="Expert transfered in the log")

n, bins, patches =ax.hist(b, bins=50, weights=np.zeros_like(b) + 1. / b.size, facecolor='darksalmon', normed=False, alpha=0.8, label="Expert resolved in the log")

ax.set_xlabel('Estimated Probability of Resolution (Resolution Expertise w.r.t Ticket)')
ax.set_ylabel('Relative Frequency')





#ax.legend((rects1[0], rects2[0]), ('Empirical Probability of TRSs with Repeating Experts (given TRS Frequency)', 'Empirical Probability of TRSs without Repeating Experts (given TRS Frequency)'), loc='upper center', bbox_to_anchor=(0.53, 1.00), fancybox=True, shadow=True, ncol=1, prop={'size':20})
ax.legend(loc='upper center', bbox_to_anchor=(0.53, 1.00), fancybox=True, shadow=True, ncol=2, prop={'size':22})



'''
fig, ax = plt.subplots()

plt.hist(

plot_beta_hist(ax, 10, 10)
#plot_beta_hist(ax, 4, 12)
#plot_beta_hist(ax, 50, 12)
plot_beta_hist(ax, 6, 55)
ax.set_title("'bmh' style sheet")
'''
plt.show()