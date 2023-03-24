package blockchain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ledger implements Serializable {

    @Serial
    private static final long serialVersionUID = 15L;

    // for debugging purposes, otherwise should be zero
    private final long MAXIMUM_DEBT = -100;

    Map<String , Long> accounts = new HashMap<>();

    List<Block> blocklist;

    public Ledger(List<Block> blocklist) {
        this.blocklist = blocklist;
        for(Block block : blocklist) {
            for(BlockchainEntry entry : block.blockchainEntries()) {
                processTransaction(entry.transaction());
            }
        }
    }

    public void updateLedger(Block block) {
        for(BlockchainEntry entry : block.blockchainEntries()) {
            processTransaction(entry.transaction());
        }
    }

    private void processTransaction(Transaction transaction) {
        String sender = transaction.sender();
        String receiver = transaction.receiver();
        if(sender != null) {
            long sender_balance = accounts.getOrDefault(sender, 0L);
            accounts.put(sender, sender_balance - transaction.amount());
        }
        long receiver_balance = accounts.getOrDefault(receiver, 0L);
        accounts.put(receiver, receiver_balance + transaction.amount());
    }

    public long getBalance(String username) {
        return accounts.getOrDefault(username, 0L);
    }

    public boolean isTransactionAllowed(Transaction transaction) {
        if(transaction.amount() <= 0)
            return false;
        String sender = transaction.sender();
        if(sender != null) {
            long sender_balance = accounts.getOrDefault(sender, 0L);
            return sender_balance - transaction.amount() >= MAXIMUM_DEBT;
        }
        return true;
    }

}
