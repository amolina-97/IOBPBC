package iobp.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
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
public class ContractCollaborationManager extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506000600181905560098190556008819055600b819055808052600a6020527f13da86008ba1c6922daee3e07db95305ef49ebced9f5467a0b8613fcc6b343e38190556007805460ff1916905580546001600160a01b0319163317905561178c8061007c6000396000f3fe608060405234801561001057600080fd5b506004361061012c5760003560e01c80638ba223d0116100ad578063a242592711610071578063a2425927146105d5578063b60196aa146105dd578063c040622614610721578063c17a340e14610729578063ffad0fc6146107315761012c565b80638ba223d01461036e5780638d92706914610392578063976217cd1461048d5780639849cee914610595578063a163744d146105b25761012c565b806350586b81116100f457806350586b81146102945780635197c7aa146103395780635219cfa5146103415780637e0186f614610349578063888fa979146103665761012c565b806304a87e381461013157806307da68f5146101895780631457ffd6146101935780632014e5d11461024957806333251a3a14610265575b600080fd5b610139610739565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561017557818101518382015260200161015d565b505050509050019250505060405180910390f35b610191610791565b005b610191600480360360408110156101a957600080fd5b6001600160a01b0382351691908101906040810160208201356401000000008111156101d457600080fd5b8201836020820111156101e657600080fd5b8035906020019184600183028401116401000000008311171561020857600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506107b4945050505050565b610251610850565b604080519115158252519081900360200190f35b6102826004803603602081101561027b57600080fd5b5035610859565b60408051918252519081900360200190f35b6102b1600480360360208110156102aa57600080fd5b5035610877565b60405180836001600160a01b0316815260200180602001828103825283818151815260200191508051906020019080838360005b838110156102fd5781810151838201526020016102e5565b50505050905090810190601f16801561032a5780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b610282610934565b61019161093a565b6102826004803603602081101561035f57600080fd5b50356109b0565b6102826109bd565b6103766109c6565b604080516001600160a01b039092168252519081900360200190f35b6103af600480360360208110156103a857600080fd5b50356109d5565b60405180806020018060200180602001848103845287818151815260200191508051906020019060200280838360005b838110156103f75781810151838201526020016103df565b50505050905001848103835286818151815260200191508051906020019060200280838360005b8381101561043657818101518382015260200161041e565b50505050905001848103825285818151815260200191508051906020019060200280838360005b8381101561047557818101518382015260200161045d565b50505050905001965050505050505060405180910390f35b6104aa600480360360208110156104a357600080fd5b5035610af9565b60405180871515815260200180602001866001600160a01b031681526020018560038111156104d557fe5b815260200180602001848152602001838103835288818151815260200191508051906020019080838360005b83811015610519578181015183820152602001610501565b50505050905090810190601f1680156105465780820380516001836020036101000a031916815260200191505b508381038252855181528551602091820191808801910280838360005b8381101561057b578181015183820152602001610563565b505050509050019850505050505050505060405180910390f35b610251600480360360208110156105ab57600080fd5b5035610c38565b610251600480360360408110156105c857600080fd5b5080359060200135610c80565b610282611523565b610191600480360360a08110156105f357600080fd5b81019060208101813564010000000081111561060e57600080fd5b82018360208201111561062057600080fd5b8035906020019184600183028401116401000000008311171561064257600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092956001600160a01b038535169560ff602087013516959194509250606081019150604001356401000000008111156106ad57600080fd5b8201836020820111156106bf57600080fd5b803590602001918460208302840111640100000000831117156106e157600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295505091359250611529915050565b610191611607565b61028261162d565b610139611633565b6060600480548060200260200160405190810160405280929190818152602001828054801561078757602002820191906000526020600020905b815481526020019060010190808311610773575b5050505050905090565b6000546001600160a01b031633146107a857600080fd5b6007805460ff19169055565b6000546001600160a01b031633146107cb57600080fd5b600280546001808201909255600090815260056020908152604090912080546001600160a01b0319166001600160a01b03861617815583519092610816929084019190850190611689565b5050600680546001810182556000919091527ff652222313e28459528d920b65115c16c04f3efc82aaedc97be59f3f377c0d3f8101555050565b60075460ff1690565b6004818154811061086657fe5b600091825260209091200154905081565b60008181526005602090815260408083208054600191820180548451600261010095831615959095026000190190911693909304601f81018690048602840186019094528383526060946001600160a01b03909216939092918391908301828280156109245780601f106108f957610100808354040283529160200191610924565b820191906000526020600020905b81548152906001019060200180831161090757829003601f168201915b5050505050905091509150915091565b60085490565b6000546001600160a01b0316331461095157600080fd5b6007805460ff19169055600b8054600101908190556009546008556000818152600a6020526040812091909155600454905b818110156109ac576000818152600360205260409020805460ff60a01b19169055600101610983565b5050565b6006818154811061086657fe5b600b5460010190565b6000546001600160a01b031690565b6000818152600a602090815260409182902060018101805484518185028101850190955280855260609485948594600281019360039091019291859190830182828015610a4157602002820191906000526020600020905b815481526020019060010190808311610a2d575b5050505050925081805480602002602001604051908101604052809291908181526020018280548015610a9357602002820191906000526020600020905b815481526020019060010190808311610a7f575b5050505050915080805480602002602001604051908101604052809291908181526020018280548015610ae557602002820191906000526020600020905b815481526020019060010190808311610ad1575b505050505090509250925092509193909250565b6000818152600360208181526040808420805460028083015460048401546001808601805488516101009382161593909302600019011694909404601f81018990048902820189019097528681526060988a9889988b988a98600160a01b810460ff90811699986001600160a01b039092169791169591909401939290918791830182828015610bca5780601f10610b9f57610100808354040283529160200191610bca565b820191906000526020600020905b815481529060010190602001808311610bad57829003601f168201915b5050505050945081805480602002602001604051908101604052809291908181526020018280548015610c1c57602002820191906000526020600020905b815481526020019060010190808311610c08575b5050505050915095509550955095509550955091939550919395565b6000816000191415610c4c57506001610c7b565b600082815260036020526040902054600160a01b900460ff16151560011415610c7757506001610c7b565b5060005b919050565b6000610c8a610850565b610c9357600080fd5b6000838152600360205260409020546001600160a01b03163314610cb657600080fd5b60008381526003602081905260408220600281015483928392810191839160ff90911690811115610ce357fe5b1415610e80576000878152600360205260409020600401546000191415610dbb57610d2481600081548110610d1457fe5b9060005260206000200154610c38565b151560011415610db6575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a905581548652828620600201805480830182559087528487204291015590548552908420909201805480840182559084529083200191909155915061151d9050565b610e7b565b610dcb81600081548110610d1457fe5b15156001148015610dee5750600087815260036020526040902060040154600854145b15610e7b575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a905581548652828620600201805480830182559087528487204291015590548552908420909201805480840182559084529083200191909155915061151d9050565b611514565b600160008881526003602081905260409091206002015460ff1690811115610ea457fe5b141561103d57600091505b8054821015610ee357610ec7818381548110610d1457fe5b151560011415610ed8576001909301925b600190910190610eaf565b6000878152600360205260409020600401546000191415610f8f578054841415610db6575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a905581548652828620600201805480830182559087528487204291015590548552908420909201805480840182559084529083200191909155915061151d9050565b805484148015610dee57506000878152600360205260409020600401546008541415610e7b575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a905581548652828620600201805480830182559087528487204291015590548552908420909201805480840182559084529083200191909155915061151d9050565b600260008881526003602081905260409091206002015460ff169081111561106157fe5b14156111f757600092505b80548310156110a057611084818481548110610d1457fe5b151560011415611095576001909301925b60019092019161106c565b6000878152600360205260409020600401546000191415611149578315610db6575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a905581548652828620600201805480830182559087528487204291015590548552908420909201805480840182559084529083200191909155915061151d9050565b600084118015610dee57506000878152600360205260409020600401546008541415610e7b575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a905581548652828620600201805480830182559087528487204291015590548552908420909201805480840182559084529083200191909155915061151d9050565b60008781526003602081905260409091206002015460ff168181111561121957fe5b141561151457600092505b80548310156112585761123c818481548110610d1457fe5b15156001141561124d576001909301925b600190920191611224565b60008781526003602052604090206004015460001914156113ac578360011415611309575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a9055815486528286206002018054808301825590875284872042910155905485529084209092018054808401825590845290832001919091556008849055915061151d9050565b600184111561139f575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a9055815486528286206002018054808301825590875284872042910155905485529084209092018054808401825590845290832001919091556008849055915061151d9050565b600094505050505061151d565b8360011480156113ce5750600087815260036020526040902060040154600854145b15611460575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a9055815486528286206002018054808301825590875284872042910155905485529084209092018054808401825590845290832001919091556008849055915061151d9050565b6001841180156114825750600087815260036020526040902060040154600854145b1561139f575050506000848152600360208181526040808420805460ff60a01b1916600160a01b179055600b80548552600a8352818520600190810180548083018255908752848720018a9055815486528286206002018054808301825590875284872042910155905485529084209092018054808401825590845290832001919091556008849055915061151d9050565b60009450505050505b92915050565b60025490565b6000546001600160a01b0316331461154057600080fd5b600180548082018255600090815260036020908152604090912080546001600160a01b0319166001600160a01b0388161760ff60a01b191681558751909261158f929084019190890190611689565b5060028101805485919060ff191660018360038111156115ab57fe5b021790555082516115c59060038301906020860190611707565b5060049081019190915580546001810182556000919091527f8a35acfbc15ff81a39ae7d344fd709f28e8600b4aa8c65c6b64bfe7fe36bd19b81015550505050565b6000546001600160a01b0316331461161e57600080fd5b6007805460ff19166001179055565b60015490565b606060068054806020026020016040519081016040528092919081815260200182805480156107875760200282019190600052602060002090815481526020019060010190808311610773575050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106116ca57805160ff19168380011785556116f7565b828001600101855582156116f7579182015b828111156116f75782518255916020019190600101906116dc565b50611703929150611741565b5090565b8280548282559060005260206000209081019282156116f757916020028201828111156116f75782518255916020019190600101906116dc565b5b80821115611703576000815560010161174256fea264697066735822122078e2bf9a3c4bdf0d867fa4bf5165c91954f9a0f5285bc05522e38126378fa4af64736f6c63430007000033";

    public static final String FUNC_ADDCOLLABORATOR = "addCollaborator";

    public static final String FUNC_COLLABORATORARRAY = "collaboratorArray";

    public static final String FUNC_CREATETASK = "createTask";

    public static final String FUNC_GETCOLLABORATORBYID = "getCollaboratorById";

    public static final String FUNC_GETCOLLABORATORCOUNT = "getCollaboratorCount";

    public static final String FUNC_GETCOLLABORATORS = "getCollaborators";

    public static final String FUNC_GETNUMBEROFTRACES = "getNumberOfTraces";

    public static final String FUNC_GETSUPERVISOR = "getSupervisor";

    public static final String FUNC_GETTASKBYID = "getTaskById";

    public static final String FUNC_GETTASKCOUNT = "getTaskCount";

    public static final String FUNC_GETTASKS = "getTasks";

    public static final String FUNC_GETTRACE = "getTrace";

    public static final String FUNC_GETX = "getX";

    public static final String FUNC_ISRUNNING = "isRunning";

    public static final String FUNC_ISTASKCOMPLETEDBYID = "isTaskCompletedById";

    public static final String FUNC_NEWINSTANCE = "newInstance";

    public static final String FUNC_RUN = "run";

    public static final String FUNC_SETTASKONCOMPLETED = "setTaskOnCompleted";

    public static final String FUNC_STOP = "stop";

    public static final String FUNC_TASKSARRAY = "tasksArray";

    @Deprecated
    protected ContractCollaborationManager(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ContractCollaborationManager(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ContractCollaborationManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ContractCollaborationManager(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> addCollaborator(String _collaborator, String _organisation) {
        final Function function = new Function(
                FUNC_ADDCOLLABORATOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _collaborator), 
                new org.web3j.abi.datatypes.Utf8String(_organisation)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> collaboratorArray(BigInteger param0) {
        final Function function = new Function(FUNC_COLLABORATORARRAY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> createTask(String _activity, String _executor, BigInteger _tasktype, List<BigInteger> _requirements, BigInteger condition) {
        final Function function = new Function(
                FUNC_CREATETASK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_activity), 
                new org.web3j.abi.datatypes.Address(160, _executor), 
                new org.web3j.abi.datatypes.generated.Uint8(_tasktype), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_requirements, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(condition)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<String, String>> getCollaboratorById(BigInteger _id) {
        final Function function = new Function(FUNC_GETCOLLABORATORBYID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteFunctionCall<Tuple2<String, String>>(function,
                new Callable<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getCollaboratorCount() {
        final Function function = new Function(FUNC_GETCOLLABORATORCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getCollaborators() {
        final Function function = new Function(FUNC_GETCOLLABORATORS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getNumberOfTraces() {
        final Function function = new Function(FUNC_GETNUMBEROFTRACES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getSupervisor() {
        final Function function = new Function(FUNC_GETSUPERVISOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple6<Boolean, String, String, BigInteger, List<BigInteger>, BigInteger>> getTaskById(BigInteger _id) {
        final Function function = new Function(FUNC_GETTASKBYID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}, new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple6<Boolean, String, String, BigInteger, List<BigInteger>, BigInteger>>(function,
                new Callable<Tuple6<Boolean, String, String, BigInteger, List<BigInteger>, BigInteger>>() {
                    @Override
                    public Tuple6<Boolean, String, String, BigInteger, List<BigInteger>, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<Boolean, String, String, BigInteger, List<BigInteger>, BigInteger>(
                                (Boolean) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                convertToNative((List<Uint256>) results.get(4).getValue()), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getTaskCount() {
        final Function function = new Function(FUNC_GETTASKCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getTasks() {
        final Function function = new Function(FUNC_GETTASKS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<Tuple3<List<BigInteger>, List<BigInteger>, List<BigInteger>>> getTrace(BigInteger id) {
        final Function function = new Function(FUNC_GETTRACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple3<List<BigInteger>, List<BigInteger>, List<BigInteger>>>(function,
                new Callable<Tuple3<List<BigInteger>, List<BigInteger>, List<BigInteger>>>() {
                    @Override
                    public Tuple3<List<BigInteger>, List<BigInteger>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<BigInteger>, List<BigInteger>, List<BigInteger>>(
                                convertToNative((List<Uint256>) results.get(0).getValue()), 
                                convertToNative((List<Uint256>) results.get(1).getValue()), 
                                convertToNative((List<Uint256>) results.get(2).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getX() {
        final Function function = new Function(FUNC_GETX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> isRunning() {
        final Function function = new Function(FUNC_ISRUNNING, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isTaskCompletedById(BigInteger _id) {
        final Function function = new Function(FUNC_ISTASKCOMPLETEDBYID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> newInstance() {
        final Function function = new Function(
                FUNC_NEWINSTANCE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> run() {
        final Function function = new Function(
                FUNC_RUN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setTaskOnCompleted(BigInteger _id, BigInteger token) {
        final Function function = new Function(
                FUNC_SETTASKONCOMPLETED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id), 
                new org.web3j.abi.datatypes.generated.Uint256(token)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> stop() {
        final Function function = new Function(
                FUNC_STOP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> tasksArray(BigInteger param0) {
        final Function function = new Function(FUNC_TASKSARRAY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static ContractCollaborationManager load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractCollaborationManager(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ContractCollaborationManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractCollaborationManager(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ContractCollaborationManager load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ContractCollaborationManager(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ContractCollaborationManager load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ContractCollaborationManager(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ContractCollaborationManager> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ContractCollaborationManager.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<ContractCollaborationManager> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ContractCollaborationManager.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ContractCollaborationManager> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ContractCollaborationManager.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ContractCollaborationManager> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ContractCollaborationManager.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
