'''
Created on Jan 3, 2017

@author: US
'''
import csv


def mainBodyRun (inputFilePath, minimum_frequency, spread, minimum_expert_count):
    expert_content = {}
    
    cr = csv.reader(open(inputFilePath,"rb"))
    
    index = 0
    condcount = 0
    for row in cr:
      
        if index!=0:   
            ResCount = int(row[1])+int(row[2])
            TransCount = int(row[3])+int(row[4])
            if ResCount < minimum_frequency:
                index+=1
                continue
            if TransCount < minimum_frequency:
                index+=1
                continue
            ResClassProb = float(ResCount/float(ResCount+TransCount))
            TransClassProb = 1.00-ResClassProb
            
            if abs((TransClassProb - ResClassProb))>spread:
                index+=1
                continue
            
            condcount+=1
            expert_content[row[0]] = {"TP":int(row[1]), "FN":int(row[2]), "FP":int(row[3]), "TN":int(row[4])}  
            
        index+=1
 
    overall_tp = 0
    overall_fn = 0
    overall_fp = 0
    overall_tn = 0
    
    if condcount< minimum_expert_count:
        return []
    
    for exp in expert_content:
        overall_tp += expert_content[exp]["TP"]
        overall_fn += expert_content[exp]["FN"]
        overall_fp += expert_content[exp]["FP"]
        overall_tn += expert_content[exp]["TN"]
        
    P1 = float(overall_tp)/(float(overall_tp+overall_fp))
    P2 = float(overall_tn)/(float(overall_tn+overall_fn))
    
    R1 = float(overall_tp)/(float(overall_tp+overall_fn))
    R2 = float(overall_tn)/(float(overall_tn+overall_fp))
    
    
    accuracy = float(overall_tp+overall_tn)/(float(overall_tp+overall_fp+overall_fn+overall_tn))
    overall_Precision = (P1+P2)/2.00
    overall_Recall = (R1+R2)/2.00
    
    F1 = 2.00*(1.00/((1.00/overall_Recall) + (1.00/overall_Precision)))
    
    

    
    result = [minimum_frequency, spread, condcount, F1, overall_Recall, overall_Precision, accuracy, overall_tp, overall_fn, overall_fp, overall_tn]
    
    return result

def main():
    
    inputFilePath = "./files/ensembleBaseConfusionMatrices.csv"  
    FileBaseName = inputFilePath.split("/")[-1].split(".")[-2]
    outputThresholdFilePath = "./files/output/"+FileBaseName+"_minFreqThresholdAnalysis.csv"
    outputClassBalanceRangeFilePath = "./files/output/"+FileBaseName+"_classBalanceAnalysis.csv"
    outputBothFilePath = "./files/output/"+FileBaseName+"_BothAnalysis.csv"
    
    
    default_minimum_frequency = 100
    default_spread = 1
    #default_spread = 0.24
    
    class_balance_threshold_range = [0.02, 1.02, 0.02]
    minimum_frequency_range = [100, 1000, 2]
    
    minimum_expert_count = 30
    
    studyMinimumFreq(minimum_frequency_range, outputThresholdFilePath, inputFilePath, default_spread, 1)
    studyClassBalanceRange (class_balance_threshold_range, outputClassBalanceRangeFilePath, inputFilePath, default_minimum_frequency, 1)
    
    studyBoth (minimum_frequency_range, class_balance_threshold_range, outputBothFilePath, inputFilePath, minimum_expert_count)
    
    
def studyBoth (minimum_frequency_range, class_balance_threshold_range, outputBothFilePath, inputFilePath, minimum_expert_count): 
    min_val = minimum_frequency_range[0]
    max_val = minimum_frequency_range[1]
    step_val = minimum_frequency_range[2]
    
    min_val2 = class_balance_threshold_range[0]
    max_val2 = class_balance_threshold_range[1]
    step_val2 = class_balance_threshold_range[2]
    
    c = csv.writer(open(outputBothFilePath, "wb"))
    c.writerow(["Minimum Frequency threshold","Maximum Allowable Class Spread", "Count of Experts", "F1","Overall Recall","Overall Precision","Accuracy","TP", "FN", "FP", "TN"])
    
    index = min_val
    while index<=max_val:
        
        index2 = min_val2
        while index2<=max_val2:
        
            result = mainBodyRun (inputFilePath, index , index2, minimum_expert_count)
            if len(result)!=0:
                c.writerow(result)
            
            index2+= step_val2
        
        index+= step_val
    
    
    
    
def studyMinimumFreq(minimum_frequency_range, outputThresholdFilePath, inputFilePath, default_spread, minimum_expert_count):
    min_val = minimum_frequency_range[0]
    max_val = minimum_frequency_range[1]
    step_val = minimum_frequency_range[2]
    
    c = csv.writer(open(outputThresholdFilePath, "wb"))
    c.writerow(["Minimum Frequency threshold","Maximum Allowable Class Spread", "Count of Experts", "F1","Overall Recall","Overall Precision","Accuracy","TP", "FN", "FP", "TN"])
    
    index = min_val
    while index<=max_val:
        
        result = mainBodyRun (inputFilePath, index, default_spread, minimum_expert_count)
        if len(result)!=0:
            c.writerow(result)
        
        
        index+= step_val
    
    
    
def studyClassBalanceRange(class_balance_threshold_range, outputClassBalanceRangeFilePath, inputFilePath, default_minimum_frequency, minimum_expert_count):
    min_val = class_balance_threshold_range[0]
    max_val = class_balance_threshold_range[1]
    step_val = class_balance_threshold_range[2]
    
    c = csv.writer(open(outputClassBalanceRangeFilePath, "wb"))
    c.writerow(["Minimum Frequency threshold", "Maximum Allowable Class Spread","Count of Experts", "F1","Overall Recall","Overall Precision","Accuracy","TP", "FN", "FP", "TN"])
    
    index = min_val
    while index<=max_val:
        
        result = mainBodyRun (inputFilePath, default_minimum_frequency , index, minimum_expert_count)
        if len(result)!=0:
            c.writerow(result)
        
        index+= step_val
    

    
        
    
    
if __name__ == '__main__':
    main()