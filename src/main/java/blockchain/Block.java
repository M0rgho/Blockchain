package blockchain;

import utility.Authentication;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record Block(long id, long timestamp, long magicNumber, String prevHash,
                    String curHash, int blockDifficulty, String minerName, long minEntryID, List<BlockchainEntry> blockchainEntries
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 15L;

    public Block(long id, long timestamp, long magicNumber, String prevHash,
                 String curHash, int blockDifficulty, String minerName, long minEntryID) {
        this(id, timestamp, magicNumber, prevHash, curHash, blockDifficulty, minerName, minEntryID, new ArrayList<>());
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(BlockchainEntry blockchainEntry : blockchainEntries) {
            sb.append(blockchainEntry.transaction()).append('\n');
        }
        String joinedTransactions = sb.toString();
        return """
                Block:
                Created by: %s
                %s gets 100 VC
                Id: %d
                Timestamp: %d
                Magic number: %d
                Hash of the previous block:
                %s
                Hash of the block:
                %s
                Block data:
                %s""".formatted(minerName, minerName, id, timestamp, magicNumber, prevHash, curHash, joinedTransactions);
    }

    public String getStringToHash() {
        return Long.toString(id) + magicNumber + prevHash;
    }

    public void addEntry(BlockchainEntry entry) {
        if(Authentication.isEntryValid(entry, minEntryID))
            blockchainEntries.add(entry);
        else {
            System.err.println("Invalid transaction #" + entry.transactionID());
        }
    }


    public boolean areEntriesValid() {
        for(BlockchainEntry blockchainEntry : blockchainEntries) {
            if(!Authentication.isEntryValid(blockchainEntry, minEntryID))
                return false;
        }
        return true;
    }
}
