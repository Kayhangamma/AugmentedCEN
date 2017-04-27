'''
Created on Jan 5, 2017

@author: US
'''

def main():
    
    import csv 
    
    inputFilePath = "./files/Path_length.csv"
    
    cr = csv.reader(open(inputFilePath,"rb"))
    
    RoutineGroups = ["SG-WINS-AES","NI-ICA-RAPID-RESPONSE-TEAM","NI-OPERATIONS-FIRE","NSC-CCS-DAAS","NI-BAT-NBP","NI-THI-PRODUCTION-CONTROL","NI-PRODUCTION-SOLUTIONS-CLAIM-DSM","NSC-AGENCY-INFRASTRUCTURE","NI-COMMERCIAL-E-FILE","NSC-NET-BACKUP","NI-DATAWH-ATT","NI-CLAIMS-CLAIMCENTER-LVL2","NSC-CCS-MPS" ,"NSC-ITSD-ENTERPRISE-ECHO","CA-FINANCE-EFIN","NI-UNDERWRITING-CMH-REPORT-SERVICES","NI-AGENCY-DOCVAULT","CA-RAPID-RESPONSE","NSC-ITSD-ENTERPRISE-PSWD","CA-BPS","NI-NBPWEB-BAM","NI-AWD-DSM","NI-OPERATIONS-NAPS","NI-CLAIMS-DESKTOP","NSC-IDADMIN-AGENCY","NSC-DIR-SERVICES","NSC-DESK-RUN","NSC-VIRUS","NSC-IDADMIN-CORE","NSC-WEB-MIDDLEWARE","NI-NBPWEB-BAM-BUSINESS", "NSC-IDADMIN-RACF"]
    #OutputFile
    RoutineGroupsDic = {}
    for rg in RoutineGroups:
        RoutineGroupsDic[rg] = 1    

    
    index = 0
    coverageSum = 0.00
    totalSum = 0.00
    totalRows = 0.00
    for row in cr:
        if index!=0:

            
            this_path = row[1].split(",")
                        
            totalSum+=len(this_path)
            totalRows+=1
            for el in this_path:
                if el in RoutineGroups:
                    #coverageSum += float(1.00/len(this_path))
                    coverageSum += 1.00
                    continue
                
            
        index+=1
    
    print coverageSum
    print totalRows
    print coverageSum/totalRows

if __name__ == '__main__':
    main()