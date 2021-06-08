package iobp.execution;

import iobp.contracts.ContractCollaborationManager;
import iobp.contracts.ContractEventLog;
import iobp.LogToXES;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tuples.generated.Tuple6;
import java.sql.Timestamp;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IOBP {

    ContractCollaborationManager contractCollaborationManager;
    ContractEventLog contractEventLog;
    Supervisor supervisor;
    ArrayList<Organization> collaborators;
    ArrayList<BigInteger> finalTasks;
    ArrayList<String> startTasksCollabs;
    ArrayList<BigInteger> startTasksIds;
    AtomicInteger eventsInCurrentTraceWithNoise; //shared variable
    int n;


    public IOBP() {
        contractCollaborationManager = null;
        contractEventLog = null;
        supervisor = null;
        collaborators = new ArrayList<>();
        finalTasks = new ArrayList<>();
        startTasksCollabs = new ArrayList<>();
        startTasksIds = new ArrayList<>();
        eventsInCurrentTraceWithNoise = new AtomicInteger(0);
        n = 0;
    }

    public void setup(ArrayList<String> functionsCalls, HashMap<String, String> orgsAddresess, String supervisorPrivateKey, int nInstances, int noisePercentage, int traceAverageSize, boolean performDataCleaning) throws Exception {

        //Deploy smart contracts and register IOBP logic
        supervisor = new Supervisor("SUPERVISOR", supervisorPrivateKey);//Create supervisor instance
        String contractCollaborationAddress = supervisor.createContractInstance(functionsCalls, orgsAddresess, performDataCleaning); //Generate Collaboration, EventLog, and EventLogCleaner contract. Returned CollaborationContract address
        String contractEventLogAddress = supervisor.eventLogContractAddress; //Get EventLogContract address
        contractCollaborationManager = supervisor.loadCollaborationContract(contractCollaborationAddress); //Load contract
        contractEventLog = supervisor.loadEventLogContract(contractEventLogAddress);

        //Seting up the final and start tasks
        finalTasks.clear();
        BigInteger taskcount = contractCollaborationManager.getTaskCount().send();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(taskcount) == -1; i = i.add(BigInteger.ONE)) {
            Tuple5<String, String, BigInteger, List<BigInteger>, BigInteger> task = contractCollaborationManager.getTaskById(i).send();//Component 3 is collab address
            if(task.component3().equals(new BigInteger("5",10))){ //End events
                finalTasks.add(i);
            }
            if(task.component3().equals(new BigInteger("4",10))){ //Start tasks
                //startTasks.put(task.component2(), i);
                startTasksCollabs.add(task.component2());
                startTasksIds.add(i);
            }
        }

        //Deploy triggers
        collaborators.clear();
        for (Map.Entry<String, String> entry : orgsAddresess.entrySet()) {
            Organization o = new Organization(entry.getKey(), entry.getValue().split(":")[1], nInstances, noisePercentage, traceAverageSize, finalTasks, eventsInCurrentTraceWithNoise);
            o.setCollaborationContract(contractCollaborationAddress);
            o.setEventLogContract(contractEventLogAddress);
            collaborators.add(o);
        }

        //Creating n instances
        this.n = nInstances;
        int ran = new Random().nextInt(startTasksCollabs.size()); // select one pseudorandom start tasks
        for(Organization o : collaborators){
            if(o.CREDENTIALS.getAddress().equals(startTasksCollabs.get(ran))){
                for(int i = 0; i < this.n; i++)
                    o.executeTask(startTasksIds.get(ran), new BigInteger(String.valueOf(i+1), 10));
                break;
            }
        }
        //At this moment, the execution could be done
    }


    public void run() throws Exception {
        for(int i = 0 ; i < this.n; i++){
            ArrayList<Thread> threads = new ArrayList<>();
            for(Organization o : collaborators ){
                o.setCaseId(new BigInteger(String.valueOf(i+1), 10));
                Thread t = new Thread(o);
                t.start();
                threads.add(t);
            }

            for(Thread t : threads){
                t.join();
            }
            eventsInCurrentTraceWithNoise.set(0);
            System.out.print(": "  + (i+1) + ": " + new Timestamp(System.currentTimeMillis()) +"\n");
        }
    }

    public String extractEventLog() throws Exception { //Trace extractor: get last trace generated in the blockchain in csv format
        // caseID , Event ID, Activity name, Timestamp, Resource
        String csvTrace = "";
        BigInteger tracesInEventLog = new BigInteger(String.valueOf(n), 10);
        for(BigInteger i = BigInteger.ZERO; i.compareTo(tracesInEventLog) == -1; i = i.add(BigInteger.ONE)){
            //Get events count from i+1 trace
            BigInteger eventsCount = contractEventLog.getEventsCount(i.add(BigInteger.ONE)).send();
            for (BigInteger j = BigInteger.ZERO; j.compareTo(eventsCount) == -1; j = j.add(BigInteger.ONE)) {
                //Get event i from currentCase Id
                Tuple6<BigInteger, BigInteger, String, String, String, BigInteger> event = contractEventLog.getEvent(i.add(BigInteger.ONE), j).send();
                csvTrace += event.component2() + ", "; //Get caseId (current trace Id)
                csvTrace += event.component1() + ", "; //Get eventId
                csvTrace += event.component3() + ", "; //Get activity name
                //Get Timestamp

                //Timestamp could be block.timestamp(UINT FORMAT), trying to convert long to TIMESTAMP
                String timestamp = "";
                try{
                    Timestamp ts=new Timestamp( new BigInteger(event.component5(), 10).longValue() );
                    Date date = new Date(ts.getTime() * 1000L);
                    DateFormat dateformat = new SimpleDateFormat(LogToXES.DATE_TIME_FORMAT_EXPECTED);
                    timestamp = dateformat.format(date); //Get timestamp
                }catch (Exception e){//Timestamp could be in DATE_TIME_FORMAT_EXPECTED
                    timestamp = event.component5();
                }
                csvTrace += timestamp + ", ";
                csvTrace += event.component4() + ", "; //Get resource
                csvTrace += event.component6() + "\n"; //Get cost and break line
            }
        }
        return csvTrace;
    }


}
