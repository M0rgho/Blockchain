import blockchain.Block;
import blockchain.Blockchain;
import blockchain.Ledger;
import clients.User;
import clients.UserCreator;
import clients.MiningTask;
import utility.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {

    private static final String BLOCKCHAIN_FILENAME = "blockchain.data";
    static final int nrOfThreads = 8;
    private static ExecutorService executorService;
    private static Blockchain blockchain;
    private static List<User> miners = new ArrayList<>();

    private static final int MAX_BLOCKCHAIN_LENGTH = 15;

    public static void main(String[] args) {
        blockchain = getBlockchain();
        executorService = Executors.newFixedThreadPool(nrOfThreads);
        blockchain.printBlockchain();


        UserCreator userCreator = new UserCreator(blockchain);

        List<String> minerNames = new ArrayList<>();
        IntStream.range(0, nrOfThreads).forEach(i -> minerNames.add("miner" + i));
        miners = userCreator.createUsers(minerNames);
        userCreator.createTransactions();
        Thread transactionThread = new Thread(userCreator::startClients);
        transactionThread.start();

        while(blockchain.length() < MAX_BLOCKCHAIN_LENGTH) {
            Block newBlock = generateNewBlock();
            blockchain.addBlock(newBlock);
        }

        executorService.shutdownNow();

        Ledger ledger = new Ledger(blockchain.getBlocks());

        List<String> allUsernames = userCreator.getAllUsernames();
        for(String name : allUsernames) {
            System.out.println(name + " - " + ledger.getBalance(name) + "VC");
        }

        saveBlockchain();
    }

    private static Block generateNewBlock() {
        List<MiningTask> miningTasks = new ArrayList<>();
        try {
            for(User miner : miners) {
                miningTasks.add(blockchain.getCurrentMiningTask(miner));
            }
            return executorService.invokeAny(miningTasks);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            miningTasks.clear();
        }
    }

    private static Blockchain getBlockchain() {
        Blockchain blockchain;
        try {
            blockchain = (Blockchain) SerializationUtils.deserialize(BLOCKCHAIN_FILENAME);
        } catch (IOException | ClassNotFoundException exception) {
            blockchain = new Blockchain();
            System.err.println("Failed to open the blockchain");
        }
        if(blockchain == null || !blockchain.isBlockchainValid()) {
            System.err.println("Blockchain was compromised");
            blockchain = new Blockchain();
        }
        return blockchain;
    }

    private static void saveBlockchain() {
        try {
            SerializationUtils.serialize(blockchain, BLOCKCHAIN_FILENAME);
        } catch (IOException exception) {
            System.err.println("Failed to save the blockchain");
        }
    }
}
