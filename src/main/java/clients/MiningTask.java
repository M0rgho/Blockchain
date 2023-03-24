package clients;

import blockchain.Block;
import blockchain.Transaction;
import utility.StringUtils;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;

public class MiningTask implements Callable<Block> {

    private final User miner;
    private final int blockID;
    private final int blockDifficulty;
    private final String prevHash;
    private final long minEntryID;

    private final int miningReward;

    public MiningTask(User miner, int blockID, int blockDifficulty, String prevHash, long minEntryID, int miningReward) {
        this.miner = miner;
        this.blockID = blockID;
        this.blockDifficulty = blockDifficulty;
        this.prevHash = prevHash;
        this.minEntryID = minEntryID;
        this.miningReward = miningReward;
    }

    public Block mineBlock() {
        Random random = new Random();
        long magicNumber = 0;
        String curHash = getHash(blockID, magicNumber, prevHash);
        while(!Thread.interrupted() && !StringUtils.isHashValid(curHash, blockDifficulty)) {
            magicNumber = random.nextLong();
            curHash = getHash(blockID, magicNumber, prevHash);
        }
        if(Thread.interrupted()) {
            return null;
        }
        Block block = new Block(blockID, new Date().getTime(), magicNumber, prevHash,
                curHash, blockDifficulty, miner.getUsername(), minEntryID);
        block.addEntry(miner.createTransaction(new Transaction(null, miner.getUsername(), miningReward)));
        return block;
    }


    private String getHash(long blockID, long magicNumber, String prevHash) {
        return StringUtils.applySha256(Long.toString(blockID) + magicNumber + prevHash);
    }



    @Override
    public Block call() {
        return mineBlock();
    }
}
