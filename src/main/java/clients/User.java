package clients;

import blockchain.Blockchain;
import blockchain.BlockchainEntry;
import blockchain.Transaction;
import utility.Authentication;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final List<Transaction> transactions;
    private PrivateKey privateKey;
    private final Blockchain blockchain;


    public User(String username, List<Transaction> transactions, Blockchain blockchain) {
        this.blockchain = blockchain;
        this.username = username;
        this.transactions = transactions;
        try {
            privateKey = Authentication.generateKeys(username);
        } catch (Exception e) {
            System.err.println("This user already exists!");
        }
    }

    public User(String username, Blockchain blockchain) {
        this(username, new ArrayList<>(), blockchain);
    }

    public void sendMessages(int delayMS) {
        for(Transaction transaction : transactions) {
            BlockchainEntry blockchainEntry = createTransaction(transaction);
            if (blockchainEntry != null) {
                blockchain.addMessageToQueue(blockchainEntry);
            }
            try {
                Thread.sleep(delayMS);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void addTransactions(List<Transaction> transactions) {
        this.transactions.addAll(transactions);
    }

    BlockchainEntry createTransaction(Transaction transaction) {
        int entryID = blockchain.getUniqueEntryID();
        return Authentication.createEntry(transaction, entryID, username, privateKey);
    }

    public String getUsername() {
        return username;
    }
}
