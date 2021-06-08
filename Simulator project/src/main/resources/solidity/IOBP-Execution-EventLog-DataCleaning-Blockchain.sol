pragma solidity ^0.7.0;
contract ContractEventLog {

    struct Event {
        uint id;
        uint caseId;
        string taskname;
        address resource;
        string timestamp;
        uint cost;
    }

    struct Trace {
        uint id;
        mapping(uint => Event) events;
        uint eventsCount;
    }

    uint CurrentCaseId;
    uint CurrentEventId;
    mapping(uint => Trace) eventlog; //set of traces


    constructor() {
        CurrentCaseId = 0;
        CurrentEventId = 0;
        eventlog[CurrentCaseId].id = CurrentCaseId;
        eventlog[CurrentCaseId].eventsCount = 0;
    }


    function recordEvent(uint _caseId, string memory _taskName, string memory _timestamp, address _resource, uint _cost) public {
        Event storage e = eventlog[_caseId].events[eventlog[_caseId].eventsCount];
        e.id = CurrentEventId;
        e.caseId = _caseId;
        e.taskname = _taskName;
        e.resource = _resource;
        e.timestamp = _timestamp;
        e.cost = _cost;
        eventlog[_caseId].eventsCount++;
        CurrentEventId++;
    }

    function newInstance(uint _caseid) external {
        CurrentCaseId++;
        eventlog[_caseid].id = _caseid;
        eventlog[_caseid].eventsCount = 0;
    }

    function getEventsCount(uint _caseId) public view returns (uint eventsCount){//Get number of events by trace
        return eventlog[_caseId].eventsCount;
    }

    function getEvent(uint _caseId, uint _eventId) public view returns (uint eventId, uint caseId, string memory taskName, address resource, string memory timestamp, uint cost){
        return (eventlog[_caseId].events[_eventId].id, eventlog[_caseId].events[_eventId].caseId, eventlog[_caseId].events[_eventId].taskname, eventlog[_caseId].events[_eventId].resource, eventlog[_caseId].events[_eventId].timestamp, eventlog[_caseId].events[_eventId].cost);
    }

    function getNumberOfTraces() public view returns (uint){// Returns the number of traces in the eventlog
        return CurrentCaseId + 1;
    }

    function getCurrentCaseId() public view returns (uint){
        return CurrentCaseId;
    }

}
contract ContractEventLogCleaner {

    ContractEventLog cEventLog;

    bool perform;

    constructor(address eventlogContractAddress, bool performDataCleaning){
        cEventLog = ContractEventLog(eventlogContractAddress);
        perform = performDataCleaning;
    }

    function clearEvent(uint _caseId, string memory _taskName, string memory _timestamp, address _resource, uint _cost) public {

        if(perform){
            if(!checkMandatoryColumns(_caseId, _taskName, _timestamp)){

                string memory timestamp_cleaned = unanchoredEvent(_timestamp);

                if(!checkDuplicateEvents(_caseId, _taskName, timestamp_cleaned, _resource, _cost)){
                    if(!checkFormBasedEventCapture(_caseId, _taskName, timestamp_cleaned, _resource, _cost)){ //Form-based event capture. If there is an Event with same timestamp and resource, the event is discarded
                        cEventLog.recordEvent(_caseId, _taskName, timestamp_cleaned, _resource, _cost);
                    }
                }
            }
        }else{
            if(!checkMandatoryColumns(_caseId, _taskName, _timestamp)){ //if there are missing mandatory columns, the event is missing
                cEventLog.recordEvent(_caseId, _taskName, _timestamp, _resource, _cost);
            }
        }
    }

    function checkMandatoryColumns(uint _caseId, string memory _taskName, string memory _timestamp) internal pure returns (bool success){
        //Check mandatory columns
        if (_caseId == 0 || (keccak256(abi.encodePacked((_taskName))) == keccak256(abi.encodePacked(("")))) ||
            (keccak256(abi.encodePacked((_timestamp))) == keccak256(abi.encodePacked((""))))) {//CaseId 0: empty
            return true;
        }
        return false;

    }

    function checkDuplicateEvents(uint _caseId, string memory _taskName, string memory _timestamp, address _resource, uint _cost) internal view returns (bool success){
        //Check mandatory columns
        uint eventsCount = cEventLog.getEventsCount(_caseId);

        for (uint i = 0; i < eventsCount; i++) {

            (uint eventId, uint eventCaseId, string memory eventTaskName, address eventResource, string memory eventTimestamp, uint eventCost) = cEventLog.getEvent(_caseId, i);

            if (
                (keccak256(abi.encodePacked((_taskName))) == keccak256(abi.encodePacked((eventTaskName)))) &&
                (keccak256(abi.encodePacked((_timestamp))) == keccak256(abi.encodePacked((eventTimestamp)))) &&
                _resource == eventResource &&
                _cost == eventCost
            ) {
                return true;
            }
        }

        return false;
    }

    function unanchoredEvent(string memory _timestamp) internal view returns(string memory result){ //pure: do not modify the state
        bytes memory stringBytes = bytes(_timestamp);
        uint length = stringBytes.length;

        if(length >= 19){ // MM/dd/yyyy HH:mm:ss (19 length FORMAT EXPECTED)                ASCII 49: 1
            if( (stringBytes[2] == "/" && stringBytes[5] == "/")){ //Verify separators between month,day,year
                if(stringBytes[13] == ":" && stringBytes[16] == ":"){ //Verify separators between hour,minute,seconds
                    if(stringBytes[0] <= 0x31 && stringBytes[1] <= 0x32){ // MM <= 12 [Month values are inside expected 1... 12]
                        return _timestamp;//Passed
                    }else{ //swap MM <-> dd
                        _timestamp = swap(_timestamp, 0, 3);//Swap M1 <-> d1
                        _timestamp = swap(_timestamp, 1, 4);//Swap M2 <-> d2
                        return _timestamp;
                    }
                }
            }
        }
        return uint2str(block.timestamp);

    }

    function checkFormBasedEventCapture(uint _caseId, string memory _taskName, string memory _timestamp, address _resource, uint _cost) internal view returns (bool success){
        uint eventsCount = cEventLog.getEventsCount(_caseId); //Analize caseId events
        for (uint i = 0; i < eventsCount; i++) {
            (uint eventId, uint eventCaseId, string memory eventTaskName, address eventResource, string memory eventTimestamp, uint eventCost) = cEventLog.getEvent(_caseId, i);
            if(_resource == eventResource){ //FormBasedEventCapture in IOBPs. The tx is created by one org_i, this problem only happens within the org_i tasks
                if(keccak256(abi.encodePacked((_timestamp))) == keccak256(abi.encodePacked((eventTimestamp))))
                    return true;
            }
        }

        return false;
    }


    function swap(string memory _s, uint _i, uint _j) internal pure returns(string memory result){ //pure: do not modify the state
        bytes memory stringBytes = bytes(_s);
        uint length = stringBytes.length;
        assert(_i <= length && _j <= length);
        bytes memory b = new bytes(1);
        b[0] = stringBytes[_i];
        stringBytes[_i] = stringBytes[_j];
        stringBytes[_j] = b[0];
        return string(stringBytes);
    }

    /* https://stackoverflow.com/questions/47129173/how-to-convert-uint-to-string-in-solidity */
    function uint2str(uint _i) internal pure returns (string memory _uintAsString) {
        if (_i == 0) {
            return "0";
        }
        uint j = _i;
        uint len;
        while (j != 0) {
            len++;
            j /= 10;
        }
        bytes memory bstr = new bytes(len);
        uint k = len;
        while (_i != 0) {
            k = k-1;
            uint8 temp = (48 + uint8(_i - _i / 10 * 10));
            bytes1 b1 = bytes1(temp);
            bstr[k] = b1;
            _i /= 10;
        }
        return string(bstr);
    }
}


contract ContractCollaborationManager {

    // TASK: 0, AND: 1, OR: 2, XOR: 3, START: 4, END: 5
    enum Tasktype {TASK, AND, OR, XOR, START, END}

    struct Collaborator {
        address resource;
        string organisation;
    }

    struct Task {
        address executor;
        string activity;
        Tasktype tasktype;
        uint[] requirements;
        uint condition; //This conditions is used to verify XOR gateways (X value)
    }

    struct ExecTrace{ // it represents an execution instance
        uint traceid; //case id
        uint X; // global token
        mapping(uint => bool) states; //states of the instance's tasks : completed or not

    }

    address supervisor;
    uint taskcount;
    uint collabcount;

    mapping(uint => Task) tasks;
    uint[] public tasksArray;

    mapping(uint => Collaborator) collaborators;
    uint[] public collaboratorArray;

    mapping(uint => ExecTrace) execTraces; //space of executions instances (tracking)
    uint tracescount; // number of execution instances created at this moment

    ContractEventLogCleaner cEventLogCleaner; //SC used to clean events when a collaborator completes a task
    ContractEventLog cEventLog; //SC used to store events
    uint NOCONDITION; // Token value by default (Used in XOR conditions)
    uint MAXUINT = 115792089237316195423570985008687907853269984665640564039457584007913129639935;

    /*
    * Initialise the contract with 0 tasks and saves the creator as owner
    */
    constructor(address eventlogCleanerContractAddress, address eventLogContractAddress) {
        supervisor = msg.sender;
        cEventLogCleaner = ContractEventLogCleaner(eventlogCleanerContractAddress);
        cEventLog = ContractEventLog(eventLogContractAddress);
        NOCONDITION = MAXUINT;
        taskcount = 0;
        tracescount = 0;
        execTraces[tracescount].traceid = tracescount;
        execTraces[tracescount].X = MAXUINT;
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
        task.activity = _activity;
        task.tasktype = _tasktype;
        task.requirements = _requirements;
        task.condition = condition;
        tasksArray.push(tasksArray.length);
    }

    /*
    * @Param: sets a Task on completed
    */
    function setTaskOnCompleted(uint _caseid, uint _taskId, string memory _taskName, string memory _timestamp, uint _token) public returns (bool success){

        require(tasks[_taskId].executor == msg.sender); //permission system

        if(tasks[_taskId].tasktype == Tasktype.START){ //if start task is trying to be executed, create a new exec trace AND a new case in the eventLog
            tracescount++;
            execTraces[_caseid].traceid = _caseid;
            execTraces[_caseid].X = MAXUINT;
            for (uint it = 0; it < taskcount; it++) {
                execTraces[_caseid].states[it] = false;
            }
            cEventLog.newInstance(_caseid);
        }

        uint tempcount = 0;
        uint j = 0;
        uint i = 0;

        uint[] storage temprequire = tasks[_taskId].requirements;

        if (tasks[_taskId].tasktype == Tasktype.TASK || tasks[_taskId].tasktype == Tasktype.START || tasks[_taskId].tasktype == Tasktype.END) {//TASK

            if ( isTaskCompletedById(_caseid, temprequire[0]) && (tasks[_taskId].condition == NOCONDITION || execTraces[_caseid].X == tasks[_taskId].condition)) {
                execTraces[_caseid].states[_taskId] = true;
                if (tasks[_taskId].tasktype == Tasktype.TASK) {// Only tasks are recorded in the event log
                    cEventLogCleaner.clearEvent(_caseid, _taskName, _timestamp, msg.sender, tx.gasprice);// {caseId, taskName, timestamp} UNION {resource, cost}
                }
                return true;
            }
        }

        else if (tasks[_taskId].tasktype == Tasktype.AND) {// AND gateway
            for (i = 0; i < temprequire.length; i++) {
                if (isTaskCompletedById(_caseid, temprequire[i])) {
                    tempcount++;
                }
            }
            if ( tempcount == temprequire.length && (tasks[_taskId].condition == NOCONDITION || execTraces[_caseid].X == tasks[_taskId].condition)) {//No condition
                execTraces[_caseid].states[_taskId] = true;
                return true;
            }
        }
        else if (tasks[_taskId].tasktype == Tasktype.OR) {// OR gateway
            for (j = 0; j < temprequire.length; j++) {
                if (isTaskCompletedById(_caseid, temprequire[j])) {
                    tempcount++;
                }
            }
            if ( tempcount > 0 && (tasks[_taskId].condition == NOCONDITION || execTraces[_caseid].X == tasks[_taskId].condition)) {
                execTraces[_caseid].states[_taskId] = true;
                return true;
            }
        }
        else if (tasks[_taskId].tasktype == Tasktype.XOR) {// XOR gateway
            uint isReqATaskComlpeted = 0;
            for (j = 0; j < temprequire.length; j++) {
                if (isTaskCompletedById(_caseid, temprequire[j])) {
                    tempcount++;
                    if(tasks[temprequire[j]].tasktype == Tasktype.TASK || tasks[temprequire[j]].tasktype == Tasktype.START){
                        isReqATaskComlpeted = 1; //if a requerimient of a join XOR is a tasks and it is completed, the path is completed
                    }
                }
            }

            if(temprequire.length > 1){ //XOR Join, verify paths: path by condition or path by task
                if ( tempcount >= 1 && (tasks[_taskId].condition == execTraces[_caseid].X || isReqATaskComlpeted == 1)) {
                    execTraces[_caseid].states[_taskId] = true;
                    return true;
                }
            }else{ //XOR split
                if ( tempcount == 1 && (tasks[_taskId].condition == NOCONDITION || execTraces[_caseid].X == tasks[_taskId].condition)) {//No condition
                    execTraces[_caseid].states[_taskId] = true;
                    execTraces[_caseid].X = _token; //Update token X when a XOR split is completed
                    return true;
                }
            }
        }
        return false;
    }

    function isTaskCompletedById(uint _caseid, uint _taskid) public view returns (bool success){
        if(_taskid == MAXUINT) //task id = maxuint represents an empty set of reqs
            return true;
        return execTraces[_caseid].states[_taskid];
    }

    function getTaskById(uint _id) public view returns (string memory description, address stateowner, Tasktype tasktype, uint[] memory requirements, uint condition){
        return (tasks[_id].activity, tasks[_id].executor, tasks[_id].tasktype, tasks[_id].requirements, tasks[_id].condition);
    }

    function getCollaboratorCount() public view returns (uint){
        return collabcount;
    }

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

    function getTraceCount() public view returns (uint){ //Get tracescount or current case id
        return tracescount;
    }

    function getX(uint _caseid) public view returns (uint){//Get global variable X (used in XOR conditions) of execution instance _caseid
        return execTraces[_caseid].X;
    }

    function getNoConditionValue() public view returns (uint){//Get No condition value (used in XOR conditions)
        return NOCONDITION;
    }

}