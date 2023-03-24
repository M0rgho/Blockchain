package blockchain;

import clients.MiningTask;
import clients.User;
import utility.Authentication;
import utility.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Blockchain implements Serializable {

    @Serial
    private static final long serialVersionUID = 15L;
    private static int MAX_MESSAGES_PER_BLOCK = 5;

    private final List<Block> blocks = new ArrayList<>();

    private int curId = 0;

    private int blockDifficulty = 5; // number of leading zeros in a hash

    private static final long creationTime = new Date().getTime();

    private final Queue<BlockchainEntry> entryQueue = new ArrayDeque<>();

    private final Ledger ledger;

    private int entryID = 5;

    private int minEntryID = 0;

    public Blockchain() {
        //  Add first empty block
        blocks.add(new Block(0, creationTime, 0, "0", "0", 0, "", 0, new ArrayList<>()));
        this.ledger = new Ledger(blocks);
    }

    private final long MIN_MINING_TIME = 50;
    private final long MAX_MINING_TIME = 300;

    private int miningReward = 100;

    private final StringBuilder stringBuilder = new StringBuilder();


    synchronized public void addBlock(Block newBlock) {
        if (!isBlockValid(getLastBlock(), newBlock)) {
            System.err.println("Invalid block: " + newBlock);
            return;
        }
        long miningTime = newBlock.timestamp() - getLastBlock().timestamp();
        ++curId;
        int prevBlockDifficulty = blockDifficulty;
        checkMiningTime(miningTime);
        blocks.add(newBlock);
        synchronized (entryQueue) {
            int nrToAdd = Math.min(MAX_MESSAGES_PER_BLOCK, entryQueue.size()) - 1;
            while (nrToAdd > 0  && !entryQueue.isEmpty()) {
                BlockchainEntry entry = entryQueue.poll();
                if(entry.transactionID() < newBlock.minEntryID()) {

                    System.err.println("Invalid transaction #" + entry.transaction());
                    continue;
                }
                if(!ledger.isTransactionAllowed(entry.transaction())) {
                    System.err.println("Illegal transaction #" + entry.transaction());
                    continue;
                }
                newBlock.addEntry(entry);
                nrToAdd--;
            }
        }

        ledger.updateLedger(newBlock);

        System.out.println(createReport(newBlock.toString(), miningTime, prevBlockDifficulty - blockDifficulty) + "\n");
    }

    private String createReport(String blockData, long miningTime, int difficultyChange) {
        stringBuilder.setLength(0);
        stringBuilder.append(blockData);
        stringBuilder.append("Block was generating for ").append(miningTime).append(" milliseconds\n");
        if(difficultyChange == 1) {
            stringBuilder.append("N was decreased to ").append(blockDifficulty).append("\n");
        } else if(difficultyChange == -1) {
            stringBuilder.append("N was increased to ").append(blockDifficulty).append("\n");
        } else {
            stringBuilder.append("N stays the same");
        }
        return stringBuilder.toString();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    void checkMiningTime(long miningTime) {
        if (miningTime < MIN_MINING_TIME) {
            blockDifficulty++;
        } else if (miningTime > MAX_MINING_TIME) {
            blockDifficulty--;
        }
    }

    public boolean isBlockValid(Block prevBlock, Block curBlock) {
        return prevBlock.curHash().equals(curBlock.prevHash())
                && StringUtils.isHashValid(StringUtils.applySha256(curBlock.getStringToHash()), curBlock.blockDifficulty())
                && curBlock.areEntriesValid();
    }

    public boolean isBlockchainValid() {
        for (int i = 1; i < blocks.size(); i++) {
            if(!isBlockValid(blocks.get(i - 1), blocks.get(i))) {
                System.err.println("Prev block: "  + blocks.get(i - 1));
                System.err.println("Invalid block:" + blocks.get(i));
                return false;

            }
        }
        return true;
    }
    public int getDifficulty() {
        return blockDifficulty;
    }

    public String getLastHash() {
        return getLastBlock().curHash();
    }

    public Block getLastBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public int getNextId() {
        return curId + 1;
    }

    synchronized public int getUniqueEntryID() {
        entryID++;
        return entryID;
    }


    public void printBlockchain() {
        for (int i = 1; i < blocks.size(); i++) {
            System.out.println(blocks.get(i));
        }
    }

    public void addMessageToQueue(BlockchainEntry blockchainEntry) {
        if (!Authentication.isEntryValid(blockchainEntry, 0)) {
            System.err.println("message #" + blockchainEntry.transactionID() +  "has invalid signature!");
            return;
        }
        synchronized (entryQueue) {
            entryQueue.add(blockchainEntry);
        }
    }

    public int length() {
        return blocks.size() - 1;
    }

    public synchronized MiningTask getCurrentMiningTask(User miner) {
        return new MiningTask(miner, getNextId(), getDifficulty(), getLastHash(), minEntryID, miningReward);
    }
}
