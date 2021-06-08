package iobp.execution;

import iobp.contracts.ContractCollaborationManager;
import iobp.contracts.ContractEventLog;
import iobp.LogToXES;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tuples.generated.Tuple6;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Organization extends Trigger implements Runnable {
    // TASK: 0, AND: 1, OR: 2, XOR: 3, START: 4, END: 5
    BigInteger TASK_TYPE = BigInteger.ZERO, AND_TYPE = BigInteger.ONE, OR_TYPE = BigInteger.TWO ,XOR_TYPE = new BigInteger("3",10), STARTEVENT_TYPE= new BigInteger("4",10), END_EVENTTYPE = new BigInteger("5", 10);

    private ContractCollaborationManager contractCollaborationManager;
    private ContractEventLog contractEventLog;

    private HashMap<BigInteger, ArrayList<BigInteger>> XORconditions = new HashMap<>(); // XOR id task -> list of conditions
    private ArrayList<BigInteger> Tasks = new ArrayList<>(); // id tasks WHERe task.resource==MY public key
    private ArrayList<BigInteger> FinalTasks = new ArrayList<>(); // ids of final taks
    public BigInteger IOBPTaskCount, IOBPNoConditionValue;

    BigInteger CaseId;

    private int taskWithNoise;
    private int traceAverageSize;
    private AtomicInteger eventsInCurrentTraceWithNoise;

    public Organization(String name, String privateKey, int nInstances, int noisePercentage, int traceAverageSize, ArrayList<BigInteger> finalTasks, AtomicInteger eventsInCurrentTraceWithNoise ) {
        super(name, privateKey);
        this.taskWithNoise = (int) Math.round(nInstances * 1.0f * (noisePercentage / 100.0));
        this.traceAverageSize = traceAverageSize;
        this.FinalTasks = finalTasks;
        this.CaseId = BigInteger.ONE;
        this.eventsInCurrentTraceWithNoise = eventsInCurrentTraceWithNoise;
    }

    public void setCollaborationContract(String address) throws Exception {
        contractCollaborationManager = loadCollaborationContract(address);
        IOBPTaskCount = contractCollaborationManager.getTaskCount().send();
        IOBPNoConditionValue = contractCollaborationManager.getNoConditionValue().send();
        identifyTasks();
    }

    public void setEventLogContract(String address) throws Exception {
        contractEventLog = loadEventLogContract(address);
    }

    public void setCaseId(BigInteger caseid){
        this.CaseId = caseid;
    }

    private void identifyTasks() throws Exception {
        Tasks.clear();
        XORconditions.clear();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(IOBPTaskCount) == -1; i = i.add(BigInteger.ONE)) {
            Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> task = contractCollaborationManager.getTaskById(i).send();//Component 3 is collab address
            if (CREDENTIALS.getAddress().equals(task.component2())) {
                Tasks.add(i);
                //Identify XOR conditions : in order to make arbitrary IOBP executions (choose arbitrary XOR paths)
                if (task.component3().equals(XOR_TYPE) && task.component4().size() == 1) { //Yes it is an XOR
                    //Get XOR paths' conditions
                    for (BigInteger j = BigInteger.ZERO; j.compareTo(IOBPTaskCount) == -1; j = j.add(BigInteger.ONE)) {
                        if (i.compareTo(j) != 1) {
                            Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> xtask = contractCollaborationManager.getTaskById(j).send();
                            if (xtask.component4().contains(i) && !xtask.component5().equals(IOBPNoConditionValue)) { //xtask is the beginning of Xortask path, it means it is a condition
                                if (XORconditions.containsKey(i)) {
                                    XORconditions.get(i).add(xtask.component5());
                                } else {
                                    ArrayList<BigInteger> conditions = new ArrayList<>();
                                    conditions.add(xtask.component5());
                                    XORconditions.put(i, conditions);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean verifyRequirements(BigInteger taskId) throws Exception {
        int reqsVerified = 0;
        Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> task = contractCollaborationManager.getTaskById(taskId).send();
        for (BigInteger req : task.component4()) {
            if (contractCollaborationManager.isTaskCompletedById( CaseId, req).send()) {
                //Is the requirement an XOR?
                Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> requirement = contractCollaborationManager.getTaskById(req).send(); // Get requirement details
                if (requirement.component3().equals(XOR_TYPE) && requirement.component4().size() == 1) { //XOR SPLIT
                    //Verify there is no other paths completed
                    int pathsInitiated = 0;
                    for (BigInteger j = BigInteger.ZERO; j.compareTo(IOBPTaskCount) == -1; j = j.add(BigInteger.ONE)) {
                        Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> xtask = contractCollaborationManager.getTaskById(j).send();

                        if(j.compareTo(taskId) != 0){
                            if (xtask.component4().contains(req) && contractCollaborationManager.isTaskCompletedById(CaseId, j).send() ) { //*xtask.component1()
                                pathsInitiated++;
                            }
                        }

                    }
                    if (pathsInitiated == 0) //If there is no path completed, we can count as valid requirement
                        reqsVerified++;
                } else { //It is not a Xor, we can count as valid requirement
                    reqsVerified++;
                }
            }
        }
        //We know how many requirements are completed of the task, in function of its type, we decide if the task can be completed

        BigInteger X = contractCollaborationManager.getX(CaseId).send(); //We have to know what is the value of X
        int numberOfRequirements = task.component4().size();

        if (task.component3().equals(TASK_TYPE) || task.component3().equals(STARTEVENT_TYPE) || task.component3().equals(END_EVENTTYPE)) { //Task
            //return (reqsVerified == numberOfRequirements && task.component6().equals(IOBPNoConditionValue)) || (reqsVerified == numberOfRequirements && X.equals(task.component6()));
            return reqsVerified == numberOfRequirements && (task.component5().equals(IOBPNoConditionValue) || X.equals(task.component5()));
        } else if (task.component3().equals(AND_TYPE)) {//And
            //return (reqsVerified == numberOfRequirements && task.component6().equals(IOBPNoConditionValue)) || (reqsVerified == numberOfRequirements && X.equals(task.component6()));
            return reqsVerified == numberOfRequirements && (task.component5().equals(IOBPNoConditionValue) || X.equals(task.component5()));
        } else if (task.component3().equals(OR_TYPE)) { //Or
            //return (reqsVerified > 0 && task.component6().equals(IOBPNoConditionValue)) || (reqsVerified > 0 && X.equals(task.component6()));
            return reqsVerified > 0 && (task.component5().equals(IOBPNoConditionValue) || X.equals(task.component5()));
        } else if (task.component3().equals(XOR_TYPE)) { //Xor

            if(task.component4().size()  > 1){ //Join
                int isAReqATaskCompleted = 0;
                int reqsCompleted = 0;
                for (BigInteger req : task.component4()) {
                    if (contractCollaborationManager.isTaskCompletedById(CaseId, req).send()) {
                        reqsCompleted++;
                        Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> requirement = contractCollaborationManager.getTaskById(req).send(); // Get requirement details
                        if(requirement.component3().equals(TASK_TYPE)||requirement.component3().equals(END_EVENTTYPE) || requirement.component3().equals(STARTEVENT_TYPE)){
                            isAReqATaskCompleted=1;
                            break;
                        }
                    }
                }
                return ( (reqsCompleted>=1 && isAReqATaskCompleted == 1) || (reqsVerified >= 1 && X.equals(task.component5())));
            }else{ //Split
                //return (reqsVerified >= 1 && task.component6().equals(IOBPNoConditionValue)) || (reqsVerified >= 1 && X.equals(task.component6()));
                return  reqsVerified >= 1 && (task.component5().equals(IOBPNoConditionValue) || X.equals(task.component5()));
            }


        }
        return false;
    }

    public void executeTask(BigInteger taskid, BigInteger caseid) throws Exception { //execute Start_Event
        //Get timestamp
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = new Date(ts.getTime());
        DateFormat dateformat = new SimpleDateFormat(LogToXES.DATE_TIME_FORMAT_EXPECTED);
        String timestamp = dateformat.format(date);

        Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> task = contractCollaborationManager.getTaskById(taskid).send();

        EventFromTrigger e = new EventFromTrigger(caseid, taskid, task.component1(), timestamp, IOBPNoConditionValue );

        contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
    }

    private void executeTask(BigInteger taskId, BigInteger tokenValue, String taskName, BigInteger taskType) throws Exception {
        //System.out.println("\t[ " + NAME + " ] : Executing task with eventsInCurrentTraceWithNoise: " + eventsInCurrentTraceWithNoise.get() );

        //Get timestamp
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = new Date(ts.getTime());
        DateFormat dateformat = new SimpleDateFormat(LogToXES.DATE_TIME_FORMAT_EXPECTED);
        String timestamp = dateformat.format(date); //Get timestamp

        EventFromTrigger e = new EventFromTrigger(CaseId, taskId, taskName, timestamp, tokenValue);

        if(taskType.equals(TASK_TYPE)){

            if(CaseId.intValue() <= taskWithNoise){ //could inject noise

                //flip a coin
                Random r = new Random();
                int coin = r.nextInt(2); // [0, 2)

                if(coin == 1){ //yes inject noise
                    if(eventsInCurrentTraceWithNoise.get() < (traceAverageSize * 0.5f)){ // if noise on trace is less than 50%, inject noise
                        int option = r.nextInt(3);
                        if(option == 0){
                            //Inyect noise on event
                            Noise noise = new Noise();
                            //Noise on, CaseId, TaskName, Timestamp
                            e = noise.noiseOnEvent(e);
                            contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
                        }else if(option == 1){ //Duplicate event
                            contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
                            contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
                            System.out.print("-D");
                        }else if(option == 2){ //Form-based event capture
                            contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
                            contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName + " - form " + new BigInteger(40, new SecureRandom()), e.timestamp, e.tokenValue).send();
                            System.out.print("-F");
                        }
                        eventsInCurrentTraceWithNoise.incrementAndGet();
                    }else{ //without noise
                        contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
                    }
                }else{ // without noise
                    contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
                }
            }else{ //without noise
                contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
            }
        }else { //without noise
            contractCollaborationManager.setTaskOnCompleted(e.caseId, e.taskId, e.taskName, e.timestamp, e.tokenValue).send();
        }
    }

    private void monitoring() throws Exception {
        int finaltaskscompleted = 0;
        while(finaltaskscompleted == 0){
            for(BigInteger i : Tasks){
                if (!contractCollaborationManager.isTaskCompletedById(CaseId, i).send()) {
                    if (verifyRequirements(i)) {
                        Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> task = contractCollaborationManager.getTaskById(i).send();
                        if (task.component3().equals(XOR_TYPE)) {//XOR
                            if (XORconditions.containsKey(i)) {
                                ArrayList<BigInteger> tConditions = XORconditions.get(i);
                                if (tConditions.size() > 1) {
                                    executeTask(i, tConditions.get((int) (tConditions.size() * Math.random())), task.component1(), task.component3());
                                } else {
                                    executeTask(i, tConditions.get(0), task.component1(), task.component3());
                                }
                            } else {
                                executeTask(i, IOBPNoConditionValue, task.component1(), task.component3());
                            }
                            //System.out.println("[ " + NAME + " ] : " + ((contractCollaborationManager.isTaskCompletedById(CaseId, i).send()) ? task.component1() + " completed" : "Error while trying to complete task " + task.component1()));
                        } else {
                            executeTask(i, IOBPNoConditionValue, task.component1(), task.component3());
                            //System.out.println("[ " + NAME + " ] : " + ((contractCollaborationManager.isTaskCompletedById(CaseId, i).send()) ? task.component1() + " completed" : "Error while trying to complete task " + task.component1()));
                        }
                    }
                }
            }
            //Verify final tasks
            for(BigInteger t : FinalTasks){
                if (contractCollaborationManager.isTaskCompletedById(CaseId, t).send())
                    finaltaskscompleted++;
            }
            Thread.sleep(100);
        }
    }

    @Override
    public void run() {
        try {
            monitoring();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
