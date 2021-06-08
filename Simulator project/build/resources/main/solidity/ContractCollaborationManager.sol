pragma solidity ^0.7.0;

contract ContractCollaborationManager {

    address supervisor;

    uint taskcount;
    uint collabcount;

    enum Tasktype {TASK, AND, OR, XOR}

    struct Collaborator {
        address resource;
        string organisation;
    }

    struct Task {
        address executor;
        bool completed;
        string activity;
        Tasktype tasktype;
        uint[] requirements;
        /*Added */
        uint condition; //This conditions is used to verify XOR gateways (X value)
    }

    mapping(uint => Task) tasks;
    uint[] public tasksArray;

    mapping(uint => Collaborator) collaborators;
    uint[] public collaboratorArray;


    /*
        Added
    */

    struct Trace {
        uint id;
        uint[] tasks; //Ids of each Task: id=>name, id=>executor
        uint[] timestamps; //timestamp i is of task i
        uint[] costs; //cost i is of task i
    }

    bool running; //State of the IOBP
    uint X; // Token value
    uint NOCONDITION; // Token value by default
    mapping(uint => Trace) eventlog;
    uint traceId;

    /*
    * Initialise the contract with 0 tasks and saves the creator as owner
    */
    constructor() public {
        taskcount = 0;
        NOCONDITION = 115792089237316195423570985008687907853269984665640564039457584007913129639935;
        X = NOCONDITION;
        traceId = 0;
        eventlog[traceId].id = traceId;
        running = false;
        supervisor = msg.sender;

    }

    /*
    * @param: Address of the Collaborator and his Organisation
    */
    function addCollaborator(address _collaborator, string memory _organisation) public {
        //Only ContractOwner can add collaborators
        require(msg.sender == supervisor);

        Collaborator storage collaborator = collaborators[collabcount++];
        collaborator.resource = _collaborator;
        collaborator.organisation = _organisation;
        collaboratorArray.push(collaboratorArray.length);
    }


    /*
    * @Param: Creates a Task
    */
    function createTask(string memory _activity, address _executor, Tasktype _tasktype, uint[] memory _requirements, uint condition) public {
        require(msg.sender == supervisor);
        Task storage task = tasks[taskcount++];
        task.executor = _executor;
        task.completed = false;
        task.activity = _activity;
        task.tasktype = _tasktype;
        task.requirements = _requirements;
        task.condition = condition;
        tasksArray.push(tasksArray.length);
    }

    /*
    * @Param: sets a Task on completed
    */
    function setTaskOnCompleted(uint _id, uint token) public returns (bool success){
        require(isRunning());
        //verify the current trace is on execution
        require(tasks[_id].executor == msg.sender);
        //permission system

        uint tempcount = 0;
        uint j = 0;
        uint i = 0;
        uint[] storage temprequire = tasks[_id].requirements;

        if (tasks[_id].tasktype == Tasktype.TASK) {//TASK

            if ((tasks[_id].condition == NOCONDITION && isTaskCompletedById(temprequire[0]))
                || (isTaskCompletedById(temprequire[0]) && X == tasks[_id].condition)) {//No condition
                tasks[_id].completed = true;
                eventlog[traceId].tasks.push(_id);
                eventlog[traceId].timestamps.push(block.timestamp);
                eventlog[traceId].costs.push(0);
                return true;
            }
        }
        else if (tasks[_id].tasktype == Tasktype.AND) {// AND
            for (i = 0; i < temprequire.length; i++) {
                if (isTaskCompletedById(temprequire[i]) == true) {
                    tempcount++;
                }
            }
            if ((tasks[_id].condition == NOCONDITION && tempcount == temprequire.length)
                || (tempcount == temprequire.length && X == tasks[_id].condition)) {//No condition
                tasks[_id].completed = true;
                eventlog[traceId].tasks.push(_id);
                eventlog[traceId].timestamps.push(block.timestamp);
                eventlog[traceId].costs.push(0);
                return true;
            }
        }
        else if (tasks[_id].tasktype == Tasktype.OR) {// OR
            for (j = 0; j < temprequire.length; j++) {
                if (isTaskCompletedById(temprequire[j]) == true) {
                    tempcount++;
                }
            }
            if ((tasks[_id].condition == NOCONDITION && tempcount > 0) || (tempcount > 0 && X == tasks[_id].condition)) {//No condition
                tasks[_id].completed = true;
                eventlog[traceId].tasks.push(_id);
                eventlog[traceId].timestamps.push(block.timestamp);
                eventlog[traceId].costs.push(0);
                return true;
            }
        }
        else if (tasks[_id].tasktype == Tasktype.XOR) {// XOR
            // solo se requiere una tarea completada, se suman todas, el resultado debe ser 1 para marcarla, si es mayor que 1 hay un error en la lógica
            for (j = 0; j < temprequire.length; j++) {
                if (isTaskCompletedById(temprequire[j]) == true) {
                    tempcount++;
                }
            }

            if ((tasks[_id].condition == NOCONDITION && tempcount >= 1) || (tempcount >= 1 && X = tasks[_id].condition)) {//No condition
                tasks[_id].completed = true;
                eventlog[traceId].tasks.push(_id);
                eventlog[traceId].timestamps.push(block.timestamp);
                eventlog[traceId].costs.push(0);
                X = token;
                return true;

            }
        }

        return false;
    }
    /*
    * @param: ID of a Task
    * @returns: bool value if task is completed
    */
    function isTaskCompletedById(uint _id) public view returns (bool success){
        if (_id == 115792089237316195423570985008687907853269984665640564039457584007913129639935)
            return true;
        if (tasks[_id].completed == true) {
            return true;
        }
        else return false;
    }

    /*
    * @param: Id of a State
    * @returns: status and description of the Task
    */
    function getTaskById(uint _id) public view returns (bool status,
        string memory description, address stateowner, Tasktype tasktype,
        uint[] memory requirements, uint condition){
        return (tasks[_id].completed, tasks[_id].activity, tasks[_id].executor, tasks[_id].tasktype, tasks[_id].requirements, tasks[_id].condition);
    }

    function getCollaboratorCount() public view returns (uint){
        return collabcount;
    }

    /*
    * @Returns: Amout of States in the Collaboration
    */
    function getTaskCount() public view returns (uint){
        return taskcount;
    }

    function getCollaboratorById(uint _id) public view returns (address resource, string memory organisation){
        return (collaborators[_id].resource, collaborators[_id].organisation);
    }

    function getTasks() view public returns (uint[] memory){
        return tasksArray;
    }

    function getCollaborators() view public returns (uint[] memory){
        return collaboratorArray;
    }

    function getSupervisor() view public returns (address){
        return supervisor;
    }

    /* Added */

    function isRunning() public view returns (bool success){//IOBP is running?
        return running;
    }

    function stop() public {//Stop IOBP execution
        require(msg.sender == supervisor);
        running = false;
    }

    function run() public {//Start IOBP execution
        require(msg.sender == supervisor);
        running = true;
    }

    function getNumberOfTraces() public view returns (uint){// Returns the number of traces in the eventlog
        return traceId + 1;
    }

    function getTrace(uint id) public view returns (uint[] memory tasksIds, uint[] memory timestamps, uint[] memory costs){//Get trace by id
        return (eventlog[id].tasks, eventlog[id].timestamps, eventlog[id].costs);
    }

    function getX() public view returns (uint){
        return X;
    }


    function newInstance() public {//reset tasks
        require(msg.sender == supervisor);

        running = false;
        //stop execution

        traceId++;

        X = NOCONDITION;

        eventlog[traceId].id = traceId;

        uint sizeTasks = tasksArray.length;
        for (uint i = 0; i < sizeTasks; i++) {
            tasks[i].completed = false;
        }
    }
}