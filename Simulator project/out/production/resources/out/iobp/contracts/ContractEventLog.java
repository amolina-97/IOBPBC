package iobp.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class ContractEventLog extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506000808055600181905580805260026020527fac33ff75c19e70fe83507db0d683fd3465c996598dc972688b7ace676c89077b8190557fac33ff75c19e70fe83507db0d683fd3465c996598dc972688b7ace676c89077d55610650806100786000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80633246955f146100675780637da28629146101ab578063888fa979146101da578063b617d320146101e2578063bd37d8b9146101ff578063ee11f1a014610207575b600080fd5b6101a9600480360360a081101561007d57600080fd5b8135919081019060408101602082013564010000000081111561009f57600080fd5b8201836020820111156100b157600080fd5b803590602001918460018302840111640100000000831117156100d357600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929594936020810193503591505064010000000081111561012657600080fd5b82018360208201111561013857600080fd5b8035906020019184600183028401116401000000008311171561015a57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550506001600160a01b03833516935050506020013561032d565b005b6101c8600480360360208110156101c157600080fd5b50356103c7565b60408051918252519081900360200190f35b6101c86103dd565b6101a9600480360360208110156101f857600080fd5b50356103e6565b6101c8610407565b61022a6004803603604081101561021d57600080fd5b508035906020013561040d565b6040518087815260200186815260200180602001856001600160a01b0316815260200180602001848152602001838103835287818151815260200191508051906020019080838360005b8381101561028c578181015183820152602001610274565b50505050905090810190601f1680156102b95780820380516001836020036101000a031916815260200191505b50838103825285518152855160209182019187019080838360005b838110156102ec5781810151838201526020016102d4565b50505050905090810190601f1680156103195780820380516001836020036101000a031916815260200191505b509850505050505050505060405180910390f35b600085815260026020818152604080842080840154855260019081018352932083548155928301889055865161036892840191880190610587565b506003810180546001600160a01b0319166001600160a01b038516179055835161039b9060048301906020870190610587565b506005015550505060009081526002602081905260409091200180546001908101909155805481019055565b6000908152600260208190526040909120015490565b60005460010190565b60008054600101815581815260026020819052604082209283559190910155565b60005490565b60008281526002602081815260408084208585526001908101835281852080548183015460038301546005840154848901805488516101009882161598909802600019011699909904601f810189900489028701890190975286865289986060988a988a988a9893956001600160a01b03169460049094019392909186918301828280156104dc5780601f106104b1576101008083540402835291602001916104dc565b820191906000526020600020905b8154815290600101906020018083116104bf57829003601f168201915b5050855460408051602060026001851615610100026000190190941693909304601f81018490048402820184019092528181529599508794509250840190508282801561056a5780601f1061053f5761010080835404028352916020019161056a565b820191906000526020600020905b81548152906001019060200180831161054d57829003601f168201915b505050505091509550955095509550955095509295509295509295565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106105c857805160ff19168380011785556105f5565b828001600101855582156105f5579182015b828111156105f55782518255916020019190600101906105da565b50610601929150610605565b5090565b5b80821115610601576000815560010161060656fea264697066735822122070322e7a1c6aaae8c84f0bc3741a26ebac1ffe179e5ff6d3b59ecbc8f73ef16664736f6c63430007000033";

    public static final String FUNC_GETCURRENTCASEID = "getCurrentCaseId";

    public static final String FUNC_GETEVENT = "getEvent";

    public static final String FUNC_GETEVENTSCOUNT = "getEventsCount";

    public static final String FUNC_GETNUMBEROFTRACES = "getNumberOfTraces";

    public static final String FUNC_NEWINSTANCE = "newInstance";

    public static final String FUNC_RECORDEVENT = "recordEvent";

    @Deprecated
    protected ContractEventLog(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ContractEventLog(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ContractEventLog(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ContractEventLog(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> getCurrentCaseId() {
        final Function function = new Function(FUNC_GETCURRENTCASEID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple6<BigInteger, BigInteger, String, String, String, BigInteger>> getEvent(BigInteger _caseId, BigInteger _eventId) {
        final Function function = new Function(FUNC_GETEVENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_caseId), 
                new org.web3j.abi.datatypes.generated.Uint256(_eventId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple6<BigInteger, BigInteger, String, String, String, BigInteger>>(function,
                new Callable<Tuple6<BigInteger, BigInteger, String, String, String, BigInteger>>() {
                    @Override
                    public Tuple6<BigInteger, BigInteger, String, String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<BigInteger, BigInteger, String, String, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getEventsCount(BigInteger _caseId) {
        final Function function = new Function(FUNC_GETEVENTSCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_caseId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getNumberOfTraces() {
        final Function function = new Function(FUNC_GETNUMBEROFTRACES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> newInstance(BigInteger _caseid) {
        final Function function = new Function(
                FUNC_NEWINSTANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_caseid)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> recordEvent(BigInteger _caseId, String _taskName, String _timestamp, String _resource, BigInteger _cost) {
        final Function function = new Function(
                FUNC_RECORDEVENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_caseId), 
                new org.web3j.abi.datatypes.Utf8String(_taskName), 
                new org.web3j.abi.datatypes.Utf8String(_timestamp), 
                new org.web3j.abi.datatypes.Address(160, _resource), 
                new org.web3j.abi.datatypes.generated.Uint256(_cost)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static ContractEventLog load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractEventLog(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ContractEventLog load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractEventLog(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ContractEventLog load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ContractEventLog(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ContractEventLog load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ContractEventLog(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ContractEventLog> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ContractEventLog.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<ContractEventLog> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ContractEventLog.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ContractEventLog> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ContractEventLog.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ContractEventLog> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ContractEventLog.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
