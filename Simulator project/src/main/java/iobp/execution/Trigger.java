package iobp.execution;

import iobp.contracts.ContractCollaborationManager;
import iobp.contracts.ContractEventLog;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;

public class Trigger {
    public final static String NODE_URL = "http://127.0.0.1:7545";
    protected final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    protected final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    protected String PRIVATE_KEY;
    protected Web3j web3j;
    protected TransactionManager transactionManager;
    protected Credentials CREDENTIALS;
    public  String NAME;

    public Trigger(String name, String privateKey) {
        web3j = Web3j.build(new HttpService(NODE_URL));
        NAME = name;
        PRIVATE_KEY = privateKey;
        CREDENTIALS = getCredentialsFromPrivateKey();
        transactionManager = new RawTransactionManager(
                web3j,
                CREDENTIALS
        );
        //System.out.println("\n* Trigger "+NAME+" initialized - Address " + CREDENTIALS.getAddress());
    }


    //generate credentials from private key
    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    public static Credentials getCredentialsFromPrivateKey(String pk) {
        return Credentials.create(pk);
    }

    protected ContractCollaborationManager loadCollaborationContract(String contractAddress) {
        return ContractCollaborationManager.load(contractAddress, web3j, transactionManager, GAS_PRICE, GAS_LIMIT);
    }
    protected ContractEventLog loadEventLogContract(String contractAddress) {
        return ContractEventLog.load(contractAddress, web3j, transactionManager, GAS_PRICE, GAS_LIMIT);
    }
    private void printWeb3Version() {
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String web3ClientVersionString = web3ClientVersion.getWeb3ClientVersion();
            System.out.println("Web3 client version: " + web3ClientVersionString);
        } catch (IOException e) {
            System.out.println("Error while trying to get web3ClientVersion");
        }
    }

    //Wallet, combination of private key with a pass phrase
    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils.loadCredentials("m", "wallet/path");
    }


}
