'''
Created on Jan 5, 2017

@author: US
'''
import csv

def mainBodyRun (inputFilePath, RoutineGroupsDic):
    expert_content = {}
    
    cr = csv.reader(open(inputFilePath,"rb"))
    
    index = 0
    condcount = 0
    for row in cr:
      
        if index!=0:   
            
            if row[0] not in RoutineGroupsDic:
                index+=1
                continue
            
          
            
            
            condcount+=1
            expert_content[row[0]] = {"TP":int(row[1]), "FN":int(row[2]), "FP":int(row[3]), "TN":int(row[4])}  
            
        index+=1
 
    overall_tp = 0
    overall_fn = 0
    overall_fp = 0
    overall_tn = 0
    
    
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
    
    

    
    result = [("F1",round(F1, 5)), ("Recall", round(overall_Recall,5)), ("Precision", round(overall_Precision,5)) , ("Accuracy",round(accuracy,5)) , ("TP",round(overall_tp,5)) , ("FN",round(overall_fn,5)), ("FP",round(overall_fp,5)), ("TN",round(overall_tn,5))]
    
    return result


def main():
    
    #Provide your results paths in the below list
    inputFilePaths = ["./files/ensembleBaseConfusionMatrices.csv", "./files/LMBaseConfusionMatrices.csv" , "./files/CosineBaseConfusionMatrices.csv"]
    #List of 32 solid groups to do the validation on
    RoutineGroups = ["SG-WINS-AES","NI-ICA-RAPID-RESPONSE-TEAM","NI-OPERATIONS-FIRE","NSC-CCS-DAAS","NI-BAT-NBP","NI-THI-PRODUCTION-CONTROL","NI-PRODUCTION-SOLUTIONS-CLAIM-DSM","NSC-AGENCY-INFRASTRUCTURE","NI-COMMERCIAL-E-FILE","NSC-NET-BACKUP","NI-DATAWH-ATT","NI-CLAIMS-CLAIMCENTER-LVL2","NSC-CCS-MPS" ,"NSC-ITSD-ENTERPRISE-ECHO","CA-FINANCE-EFIN","NI-UNDERWRITING-CMH-REPORT-SERVICES","NI-AGENCY-DOCVAULT","CA-RAPID-RESPONSE","NSC-ITSD-ENTERPRISE-PSWD","CA-BPS","NI-NBPWEB-BAM","NI-AWD-DSM","NI-OPERATIONS-NAPS","NI-CLAIMS-DESKTOP","NSC-IDADMIN-AGENCY","NSC-DIR-SERVICES","NSC-DESK-RUN","NSC-VIRUS","NSC-IDADMIN-CORE","NSC-WEB-MIDDLEWARE","NI-NBPWEB-BAM-BUSINESS", "NSC-IDADMIN-RACF"]
    #OutputFile
    outputFilePath = "classifiers_results_output.csv"
    
    
    
    
    c = csv.writer(open(outputFilePath, "wb"))
    c.writerow(["Method", "F1","Overall Recall","Overall Precision","Accuracy","TP", "FN", "FP", "TN"])
    
   
    RoutineGroupsDic = {}
    for rg in RoutineGroups:
        RoutineGroupsDic[rg] = 1    
        
    for inputPath in inputFilePaths:
            
        result = mainBodyRun (inputPath, RoutineGroupsDic)
        
        file_name = inputPath.split("/")[-1].split(".")[-2]
        
        output_res = []
        output_res.append(file_name)
        for tup in result:
            output_res.append(tup[1])
        
        c.writerow(output_res)
        
        print file_name+": "
        print str(result)
    
    
    
if __name__ == '__main__':
    main()