# Blockchain
A java mockup of a blockchain. It's a simplified version of a digital ledger, that was creating as a learning tool.

### Mining
Each miner is represented by a different thread, and they all compete to mine the block first. 
Whoever does it first is rewarded with virtual currency. Blockchain dynamically changes the difficulty to match the expected time to mine a block.

### Transactions
Each transaction is signed with private key of the user who created it and later verified by the blockchain. Each transaction also has an unique id that prevents duplicating already created transactions.
Blockchain also prevents adding unsigned or impossible transactions (paying someone more than you have).

**Key pairs are generated with RSA**  
**Messages are signed by SHA256**

### Output
The blockchain writes it content to the standard output.

An example block:
```
Block:
Created by: miner3
miner3 gets 100 VC
Id: 15
Timestamp: 1679690025056
Magic number: 8801267228348242763
Hash of the previous block:
00000e601674606b3f9ab4b37bde60f521c4f2c6cc129b800f0b1c92c50ae95b
Hash of the block:
000002ebfb2cec789a849b766b621ba88b95539212fa5b3b17bffceeb8d31a62
Block data:
miner3 gets 100 VC
Anna send 17 VC to miner3
miner1 send 15 VC to miner2
Grace send 18 VC to miner4
miner3 send 7 VC to miner5
Block was generating for 43 milliseconds
N was increased to 6
```


Main function creates new blockchain and spawns new users and miners each time. After mining a specified number of blocks, account balance of each participant is displayed.  

The blockchain can be saved with serialisation, however I didn't bother to finish it properly.

---
Made by Mikołaj Maślak