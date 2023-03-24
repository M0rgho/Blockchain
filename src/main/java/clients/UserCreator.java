package clients;

import blockchain.Blockchain;
import blockchain.Transaction;

import java.util.*;

public class UserCreator {

    private final Blockchain blockchain;

    private final Map<String, User> userMap = new HashMap<>();

    private final List<String> usernames = List.of("Tom", "Anna", "Joe", "Grace");

    private final List<String> allUsernames = new ArrayList<>();

    public UserCreator(Blockchain blockchain) {
        this.blockchain = blockchain;
        createUsers(usernames);
    }

    public void createTransactions() {

        for(String username : allUsernames) {
            List<Transaction> transactions = new ArrayList<>();
            int n = random.nextInt(15, 20);
            for (int i = 0; i < n; i++) {
                String randUsername = getRandomUsername();
                if (!username.equals(randUsername)) {
                    transactions.add(new Transaction(username, randUsername, random.nextInt(5, 20)));
                }
            }
            userMap.get(username).addTransactions(transactions);
        }
    }

    Random random = new Random();

    private String getRandomUsername() {
        return allUsernames.get(random.nextInt(0, allUsernames.size() - 1));
    }

    public List<User> createUsers(List<String> names) {
        for(String username : names) {
            userMap.put(username, new User(username, blockchain));
        }
        allUsernames.addAll(names);
        return names.stream().map(userMap::get).toList();
    }

    public void startClients() {
        Random random = new Random();
        for(User users : userMap.values()) {
            int delay = random.nextInt(200, 1000);
            new Thread(() -> users.sendMessages(delay)).start();
        }
    }

    public List<String> getAllUsernames() {
        return allUsernames;
    }
}
