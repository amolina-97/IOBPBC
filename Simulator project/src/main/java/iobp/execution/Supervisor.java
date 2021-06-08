package iobp.execution;

import iobp.contracts.ContractCollaborationManager;
import iobp.contracts.ContractEventLog;
import iobp.contracts.ContractEventLogCleaner;
import iobp.Translator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Supervisor extends Trigger {

    String eventLogContractAddress = "";
    String eventLogCleanerContractAddress = "";
    String collaborationContractAddress = "";

    public Supervisor(String name, String privateKey) {
        super(name, privateKey);
    }

    /* Create a new CollaborationManager contract instance on Blockchain, return contract's address */
    private String deployContracts(boolean performDataCleaning) {
        try {
            ContractEventLog eventLog = ContractEventLog.deploy(web3j, transactionManager, GAS_PRICE, GAS_LIMIT).send();
            ContractEventLogCleaner eventLogCleaner = ContractEventLogCleaner.deploy(web3j, transactionManager, GAS_PRICE, GAS_LIMIT, eventLog.getContractAddress(), performDataCleaning).send();
            ContractCollaborationManager collaborationManager = ContractCollaborationManager.deploy(web3j, transactionManager, GAS_PRICE, GAS_LIMIT, eventLogCleaner.getContractAddress(), eventLog.getContractAddress()).send();

            eventLogContractAddress = eventLog.getContractAddress();
            eventLogCleanerContractAddress = eventLogCleaner.getContractAddress();
            collaborationContractAddress = collaborationManager.getContractAddress();

            /*System.out.println("Deployed EventLogContract: " + eventLogContractAddress);
            System.out.println("Deployed EventLogCleanerContract: " + eventLogCleanerContractAddress);
            System.out.println("Deployed CollaborationContract: " + collaborationContractAddress);*/

            return collaborationContractAddress;
        } catch (Exception e) {
            System.out.println("Error while trying to deploy contract. " + e.getMessage());
            return "";
        }
    }

    public String createContractInstance(String modelPath, HashMap<String, String> orgsPublicAddresess, boolean performDataCleaning) throws Exception {


        //Get init configuration
        Translator t = new Translator();
        ArrayList<String> functionCalls = t.parse(modelPath);

        //Deploy contract
        String contractAddress = deployContracts(performDataCleaning);

        //Load contract
        ContractCollaborationManager contract = loadCollaborationContract(contractAddress);

        //Perform init configuration to the smart contract
        for(String function : functionCalls){
            String[] line = function.split(":");
            if(function.contains("addCollaborator")){ //add collaborators
                String collabName = line[2];
                String address = orgsPublicAddresess.get(collabName);
                if(address != null)
                    contract.addCollaborator(address.split(":")[0], collabName).send();

            }else if(function.contains("addTask")){ //add tasks
                String taskname = line[1];
                String collabadd = orgsPublicAddresess.get(line[2]).split(":")[0];
                BigInteger tasktype = new BigInteger(line[3], 10);
                String[] reqsString = line[4].replace("[", "").replace("]", "").split(",");
                BigInteger condition = new BigInteger(line[5], 10);

                List<BigInteger> reqsInteger = new ArrayList<BigInteger>();
                for(int j = 0; j < reqsString.length; j++){
                    reqsInteger.add(new BigInteger(reqsString[j].trim(), 10));
                }
                //System.out.println("Sending:\tTaskName: " + taskname+ "\tCollabadd: " + collabadd + "\tTasktype: " + tasktype + "\treqsInteger: " + reqsInteger.toString());
                contract.createTask(taskname,collabadd,tasktype,reqsInteger, condition).send();
            }
        }
        return contractAddress;
    }

    public String createContractInstance(ArrayList<String> functionCalls, HashMap<String, String> orgsPublicAddresess, boolean performDataCleaning) throws Exception {
        //Deploy Collaboration, EventLog and EventLogCleaner contracts
        String contractAddress = deployContracts(performDataCleaning);

        //Load contract
        ContractCollaborationManager contract = loadCollaborationContract(contractAddress);

        //Perform init configuration to the smart contract
        for(String function : functionCalls){
            String[] line = function.split("\t");
            if(function.contains("addCollaborator")){ //add collaborators
                String collabName = line[2];
                String address = orgsPublicAddresess.get(collabName);
                if(address != null)
                    contract.addCollaborator(address.split(":")[0], collabName).send();

            }else if(function.contains("addTask")){ //add tasks
                String taskname = line[1];
                String collabadd = orgsPublicAddresess.get(line[2]).split(":")[0];
                BigInteger tasktype = new BigInteger(line[3], 10);
                String[] reqsString = line[4].replace("[", "").replace("]", "").split(",");
                BigInteger condition = new BigInteger(line[5], 10);

                List<BigInteger> reqsInteger = new ArrayList<BigInteger>();
                for(int j = 0; j < reqsString.length; j++){
                    reqsInteger.add(new BigInteger(reqsString[j].trim(), 10));
                }
                contract.createTask(taskname,collabadd,tasktype,reqsInteger, condition).send();
            }
        }
        return contractAddress;
    }

}
