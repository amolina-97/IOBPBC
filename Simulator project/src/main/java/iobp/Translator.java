package iobp;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class Translator {

    ArrayList<String> arrayOrganizations = new ArrayList<>();
    Map<String, ArrayList<Integer>> requirements = new HashMap<>();
    Map<String, BigInteger> conditions = new HashMap<>();

    Map<String, Integer> tasksIds = new HashMap<>();
    Map<Integer, ArrayList<String>> tasks_by_org = new HashMap<>();
    int task_id = 0;

    public Translator() {
    }

    private void pushTask(String id) {
        if (!tasksIds.containsKey(id)) {
            tasksIds.put(id, task_id);
            task_id++;
        }
    }

    private void pushRequirement(String idTask, String req) {
        pushTask(req);
        if (requirements.containsKey(idTask)) {
            ArrayList<Integer> row = requirements.get(idTask);
            row.add(tasksIds.get(req));
        } else {
            ArrayList<Integer> row = new ArrayList<>();
            row.add(tasksIds.get(req));
            requirements.put(idTask, row);
        }
    }

    private void pushCondition(String idTask, BigInteger condition) {
        if(!conditions.containsKey(idTask)){
            conditions.put(idTask, condition);
        }

    }

    private void pushTaskByOrg(int orgId, String idTask) {
        if (tasks_by_org.containsKey(orgId)) {
            ArrayList<String> row = tasks_by_org.get(orgId);
            row.add(idTask);
        } else {
            ArrayList<String> row = new ArrayList<>();
            row.add(idTask);
            tasks_by_org.put(orgId, row);
        }
    }

    public void addTask(ModelElementInstance element, int orgId) {

        if (element.getElementType().getInstanceType().equals(Task.class)) {
            //Task
            Task t = (Task) element;
            pushTask(t.getId());
            pushTaskByOrg(orgId, t.getId());

            List<FlowNode> prevs = t.getPreviousNodes().list();
            for (FlowNode fn : prevs) {
                pushRequirement(t.getId(), fn.getId());
            }

        } else if (element.getElementType().getInstanceType().equals(ExclusiveGateway.class)) {
            //XOR
            ExclusiveGateway g = (ExclusiveGateway) element;
            pushTask(g.getId());
            pushTaskByOrg(orgId, g.getId());

            Collection<SequenceFlow> sfs = g.getOutgoing();

            if(sfs.size()>1){ //Get conditions of Xors split
                for(SequenceFlow sf : sfs){
                    String name = sf.getName(); //considering condition as Integer!!
                    BigInteger condition;
                    try{
                        condition  = new BigInteger(name, 10);
                    }catch (Exception e){
                        condition = new BigInteger(255, new SecureRandom()); //Condition in SC is an uint: 256 bits... We generate a random number of 255 bits with a secure random source. This random number is assigned when no condition is specified in the XOR path
                    }

                    pushCondition(sf.getTarget().getId(), condition);
                }
            }

            List<FlowNode> prevs = g.getPreviousNodes().list();
            for (FlowNode fn : prevs) {
                pushRequirement(g.getId(), fn.getId());
            }

        } else if (element.getElementType().getInstanceType().equals(InclusiveGateway.class)) {
            //OR
            InclusiveGateway g = (InclusiveGateway) element;
            pushTask(g.getId());
            pushTaskByOrg(orgId, g.getId());

            List<FlowNode> prevs = g.getPreviousNodes().list();
            for (FlowNode fn : prevs) {
                pushRequirement(g.getId(), fn.getId());
            }

        } else if (element.getElementType().getInstanceType().equals(ParallelGateway.class)) {
            //AND
            ParallelGateway g = (ParallelGateway) element;
            pushTask(g.getId());
            pushTaskByOrg(orgId, g.getId());

            List<FlowNode> prevs = g.getPreviousNodes().list();
            for (FlowNode fn : prevs) {
                pushRequirement(g.getId(), fn.getId());
            }

        } else if (element.getElementType().getInstanceType().equals(StartEvent.class)) {
            //Start Event
            StartEvent se = (StartEvent) element;
            pushTask(se.getId());
            pushTaskByOrg(orgId, se.getId());

            List<FlowNode> prevs = se.getPreviousNodes().list();
            for(FlowNode fn : prevs){
                pushRequirement(se.getId(), fn.getId());
            }

        } else if (element.getElementType().getInstanceType().equals(EndEvent.class)) {
            //End Event
            EndEvent ee = (EndEvent) element;
            pushTask(ee.getId());
            pushTaskByOrg(orgId, ee.getId());

            List<FlowNode> prevs = ee.getPreviousNodes().list();
            for (FlowNode fn : prevs) {
                pushRequirement(ee.getId(), fn.getId());
            }
        }

    }

    private String getRealName(ModelElementInstance element) {
        if (element.getElementType().getInstanceType().equals(Task.class)) {
            Task t = (Task) element;
            return ((t.getName() == null) ? t.getElementType().getTypeName() : t.getName());
        } else if (element.getElementType().getInstanceType().equals(ExclusiveGateway.class)) {
            ExclusiveGateway g = (ExclusiveGateway) element;
            return g.getElementType().getTypeName() + ((g.getName() == null) ? "" : "_" + g.getName());
        } else if (element.getElementType().getInstanceType().equals(InclusiveGateway.class)) {
            InclusiveGateway g = (InclusiveGateway) element;
            return g.getElementType().getTypeName() + ((g.getName() == null) ? "" : "_" + g.getName());
        } else if (element.getElementType().getInstanceType().equals(ParallelGateway.class)) {
            ParallelGateway g = (ParallelGateway) element;
            return g.getElementType().getTypeName() + ((g.getName() == null) ? "" : "_" + g.getName());
        } else if (element.getElementType().getInstanceType().equals(StartEvent.class)) {
            StartEvent se = (StartEvent) element;
            return se.getElementType().getTypeName() + ((se.getName() == null) ? "" : "_" + se.getName());
        } else if (element.getElementType().getInstanceType().equals(EndEvent.class)) {
            EndEvent ee = (EndEvent) element;
            return ee.getElementType().getTypeName() + ((ee.getName() == null) ? "" : "_" + ee.getName());
        }
        return "Not supported";
    }

    private String getType(ModelElementInstance element) {
        if (element.getElementType().getInstanceType().equals(Task.class)) {
            return "0";
        } else if (element.getElementType().getInstanceType().equals(ExclusiveGateway.class)) {
            return "3";
        } else if (element.getElementType().getInstanceType().equals(InclusiveGateway.class)) {
            return "2";
        } else if (element.getElementType().getInstanceType().equals(ParallelGateway.class)) {
            return "1";
        } else if (element.getElementType().getInstanceType().equals(StartEvent.class)) {
            return "4";
        } else if (element.getElementType().getInstanceType().equals(EndEvent.class)) {
            return "5";
        }
        return "Not supported";
    }

    private void extractTasksFromProcess(BpmnModelInstance modelInstance, Process p, int orgId) {
        //Get start event
        Collection<StartEvent> startEvent = p.getChildElementsByType(StartEvent.class);
        for (StartEvent se : startEvent) {
            addTask(se, orgId);
        }

        //Get end event
        Collection<EndEvent> endEvent = p.getChildElementsByType(EndEvent.class);
        for (EndEvent ee : endEvent) {
            addTask(ee, orgId);
        }

        //Get tasks
        Collection<Task> p_tasks = p.getChildElementsByType(Task.class);
        for (Task t : p_tasks) {
            addTask(t, orgId);
        }

        //Get gateways (xor, and, or)
        Collection<Gateway> gates = p.getChildElementsByType(Gateway.class);
        for (Gateway g : gates) {
            addTask(g, orgId);
        }
    }

    private void extractTasksFromLane(BpmnModelInstance modelInstance, Lane l, int orgId) {
        Collection<FlowNode> laneTasks = l.getFlowNodeRefs();
        for (FlowNode fn : laneTasks) {
            addTask(modelInstance.getModelElementById(fn.getId()), orgId);
        }
    }

    public ArrayList<String> parse(String path) {
        ArrayList<String> output = new ArrayList<>();

        //Read BPMN model from file
        File file = new File(path);
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);

        //Get participants
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);
        Collection<Process> processes = new ArrayList<>(); //get participants' processes (its fragment of the bpmn model)
        if (participants.isEmpty()) {
            processes = modelInstance.getModelElementsByType(Process.class); //if there are not processes added: there are not participants, so get the root process
            arrayOrganizations.add("Org without name"); //add the single organization
        } else { //there are participants, add it as organizations
            for (Participant p : participants) {
                processes.add(p.getProcess());
                arrayOrganizations.add(((p.getName() == null) ? "Org without name" : p.getName()));
            }
        }
        //Extract tasks from process (1 Process to 1 Participant)
        int orgId = 0;
        for (Process p : processes) { //1 Process to 1 Participant
            //Verify if the process has lanes
            Collection<LaneSet> p_laneSets = p.getLaneSets();
            if (p_laneSets.isEmpty()) { //it has no lanes
                extractTasksFromProcess(modelInstance, p, orgId);
                orgId++;
            } else { //it has lanes - add as participants
                orgId++;
                for (LaneSet ls : p_laneSets) {
                    Collection<Lane> p_lanes = ls.getLanes();
                    for (Lane l : p_lanes) {
                        arrayOrganizations.add(l.getName());
                        extractTasksFromLane(modelInstance, l, orgId);
                        orgId++;
                    }
                }
            }
        }

        //Get Messages Flow : edges between two participants
        Collection<MessageFlow> messagesFlow = modelInstance.getModelElementsByType(MessageFlow.class); //messages btw participants
        for (MessageFlow mf : messagesFlow) {
            pushRequirement(mf.getTarget().getId(), mf.getSource().getId());
        }


        //Generate SC calls function
        //1. Add collaborators
        ArrayList<String> orgsAddresses = new ArrayList<>();
        for (int x = 0; x < arrayOrganizations.size(); x++) //simulation of adresses
            orgsAddresses.add(arrayOrganizations.get(x));

        //2. Add tasks
        String[] tasksCode = new String[task_id];
        int k = 0;
        for (String s : arrayOrganizations) {
            output.add("addCollaborator\t" + orgsAddresses.get(k) + "\t" + s);

            //Add tasks
            ArrayList<String> tasks_org = tasks_by_org.get(k);
            if (tasks_org != null) {
                for (String t : tasks_org) {
                    tasksCode[tasksIds.get(t)] =
                            "addTask\t"
                                    + getRealName(modelInstance.getModelElementById(t)) + "\t"
                                    + orgsAddresses.get(k) + "\t"
                                    + getType(modelInstance.getModelElementById(t)) + "\t"
                                    + ((requirements.containsKey(t)) ? requirements.get(t) : "[115792089237316195423570985008687907853269984665640564039457584007913129639935]") + "\t"
                                    + ((conditions.containsKey(t)) ? conditions.get(t) : "115792089237316195423570985008687907853269984665640564039457584007913129639935"// 0 default flow, otherwise is a real condition flow
                            );
                }
            }
            k++;
        }
        int j = 0;
        for (String s : tasksCode) {
            output.add(s);
            j++;
        }

        return output;

    }

}

