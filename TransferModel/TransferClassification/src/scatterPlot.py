'''
Created on Aug 26, 2014

@author: US
'''

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
matplotlib.rcParams.update({'font.size': 45})
matplotlib.rcParams['xtick.major.pad']='10'
axisfontsize = 40
matplotlib.rcParams['figure.figsize'] = 25, 18


class scatter:

    binSize = 0.20
    inc_points = []
    X_points_met = []
    Y_points_met = []
    X_points_breached = []
    Y_points_breached = []
    def __init__(self, incident_paths, routinenessPathDic, inc_content_routineness, incident_SLAs):
        errorCount =0
        for inc in incident_SLAs:
        #for inc in incident_paths:
            #if inc in inc_content_routineness:
            contentRoutineness = inc_content_routineness [inc]
            path = tuple(incident_paths[inc])
            if path in routinenessPathDic:
                freq = routinenessPathDic[path]
                self.inc_points.append(inc)
                if incident_SLAs[inc] =="FALSE":
                    self.Y_points_met.append(contentRoutineness)
                    self.X_points_met.append(freq)
                else:
                    self.Y_points_breached.append(contentRoutineness)
                    self.X_points_breached.append(freq)
            else:
                errorCount +=1
                continue

                
        print errorCount
        #print errorCount2

    def regulizer(self):
        
        c = csv.writer(open("regulization.csv", "wb"))
        c.writerow(["Alpha","NR-Volume","R-Volume","R-Volume-Ratio","NR-mode", "R-mode","Mode-difference"])
        alpha = 3
        while (alpha < 2500):
            
            (NR, R, NRVolume, RVolume) = self.split(alpha)
            Rmode = self.modeCalculation (R)
            NRmode = self.modeCalculation (NR)
            
            modeDifference = Rmode - NRmode
            print " ALPHA: "+str(alpha) + " Dif: " + str(modeDifference)
            RVolumeRatio =  float(RVolume)/float(RVolume+NRVolume)
            c.writerow([alpha, NRVolume, RVolume, RVolumeRatio, NRmode, Rmode, modeDifference])
            
            
        
            alpha +=1
        
        
    def split (self, alpha):
        
        leftChain = []
        rightChain = []
        index = 0
        while index<len(self.X_points):
            if self.X_points[index]<alpha:
                leftChain.append((self.X_points[index],self.Y_points[index]))
            else:
                rightChain.append((self.X_points[index],self.Y_points[index]))
        
            index +=1
            
        return (leftChain, rightChain, len(leftChain), len(rightChain))
        
    
    def modeCalculation (self, chain):
        buckets = {}
        for pair in chain:
            dataPoint = pair[1]
            indexOfBucket = int (math.floor (dataPoint/(-1*self.binSize) ))
            
            if indexOfBucket in buckets:
                buckets [indexOfBucket] +=1
            else:
                buckets [indexOfBucket] =1
                
        #key associated with the maximum value
        maxKey = max(buckets.iteritems(),key= operator.itemgetter(1))[0]        
        
        modCenter = (( maxKey*(-1*self.binSize) )  + ( (maxKey+1)*(-1*self.binSize)  ) ) / 2.00                                              
        return modCenter     
            
             
        
    def plotBothSplit(self,PathThreshold):
        alpha = PathThreshold
        (leftChain, rightChain, lenleftChain, lenrightChain) = self.split (alpha)
        leftXset = []
        leftYset = []
        rightXset = []
        rightYset = []
        for pair in leftChain:
            leftXset.append(pair[0])
            leftYset.append(pair[1])
            
        self.plot(leftXset, leftYset)
        for spair in rightChain:
            rightXset.append(spair[0])
            rightYset.append(spair[1]) 
            
        self.plot(rightXset, rightYset)   
            
    def plot (self, xSetMet, ySetMet, xSetBreached, ySetBreached):
    
        nullfmt   = NullFormatter()         # no labels
       
        # the random data
        xMet = np.asarray(xSetMet)
        #x = np.random.randn(1000)
        yMet = np.asarray(ySetMet)
        #y = np.random.randn(1000)
        xBreached = np.asarray(xSetBreached)
        yBreached = np.asarray(ySetBreached)
        
        x = np.asarray (xSetMet + xSetBreached)
        y = np.asarray (ySetMet + ySetBreached)
        
        # definitions for the axes
        ##left, width = 0.1, 0.65
        ##bottom, height = 0.1, 0.65
        #bottom_h = left_h = left+width+0.02
        
        ##rect_scatter = [left, bottom, width, height]
        #rect_histx = [left, bottom_h, width, 0.2]
        #rect_histy = [left_h, bottom, 0.2, height]
        
        #plt.figure(1, figsize=(18,9))
        
        
        axScatter = plt.axes()
        axScatter.tick_params('both', length=10, width=1, which='major')
        axScatter.tick_params('both', length=5, width=0.5, which='minor')
        #axHistx = plt.axes(rect_histx)
        #axHisty = plt.axes(rect_histy)
        
        # no labels
        #axHistx.xaxis.set_major_formatter(nullfmt)
        #axHisty.yaxis.set_major_formatter(nullfmt)
        
        # the scatter plot:
        axScatter.scatter(xMet, yMet, alpha=0.7, edgecolor='black', facecolor="b", linewidth=0.7, s=25)
        axScatter.scatter(xBreached, yBreached, alpha=0.7,  edgecolor='black', facecolor="b", linewidth=0.7, s=25)

        
        # now determine nice limits by hand:
        binwidth = self.binSize
        
        xymax = np.max( [np.max(np.fabs(x)), np.max(np.fabs(y)) ] )
        lim = ( int(xymax/binwidth) + 1) * binwidth
        
        axScatter.set_xscale('log')
        #axScatter.set_title("Tickets projection in the content - TRS space")
        axScatter.set_xlabel(r'$\alpha$' + " to be selected")
        axScatter.set_ylabel("Normalized Log-likelihood of the Ticket Content")
        axScatter.set_xlim( (0, 3000) )
        axScatter.set_ylim( (-12,-4) )
        #axScatter.plot([670, -4.00], [670, -12.00], 'k-',lw=4)
        
        
        
        bins = np.arange(-lim, lim + binwidth, binwidth)
        
        #axHistx.hist(x, bins=bins)
        #axHisty.hist(y, bins=bins, orientation='horizontal')
        
        #axHistx.set_xscale('log')
        #axHistx.set_xlim( axScatter.get_xlim() )
        #axHisty.set_ylim( axScatter.get_ylim() )
        
        plt.xticks(fontsize = axisfontsize)
        plt.yticks(fontsize = axisfontsize)
        
        plt.show()