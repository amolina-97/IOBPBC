package iobp.execution;

import java.math.BigInteger;

public class EventFromTrigger {

    //caseId, taskId, taskName, timestamp, tokenValue
    public BigInteger caseId;
    public BigInteger taskId;
    public String taskName;
    public String timestamp;
    public BigInteger tokenValue;

    public EventFromTrigger(BigInteger caseId, BigInteger taskId, String taskName, String timestamp, BigInteger tokenValue){
        this.caseId = caseId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.timestamp = timestamp;
        this.tokenValue = tokenValue;
    }
}
