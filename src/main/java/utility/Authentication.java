package utility;

import blockchain.BlockchainEntry;
import blockchain.Transaction;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;

public class Authentication {
    private static final HashMap<String, PublicKey> publicKeyMap = new HashMap<>();

    private static final Charset USED_CHARSET = StandardCharsets.UTF_8;

    public static PrivateKey generateKeys(String username) {
        if(publicKeyMap.containsKey(username)) {
            throw new IllegalStateException("A key pair already exists for user " + username);
        }
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
            keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKeyMap.put(username, keyPair.getPublic());

        return keyPair.getPrivate();
    }

    public static PublicKey getPublicKey(String username) {
        return publicKeyMap.get(username);
    }


    public static BlockchainEntry createEntry(Transaction transaction, int transactionID, String username, PrivateKey privateKey) {
        Signature signature;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        String entrySignature;
        try {
            signature.update(ByteBuffer.allocate(4).putInt(transactionID).array());
            signature.update(transaction.toString().getBytes(USED_CHARSET));
            entrySignature = Base64.getEncoder().encodeToString(signature.sign());
        } catch (SignatureException e) {
            return null;
        }

        return new BlockchainEntry(transactionID, transaction, entrySignature, getPublicKey(username));
    }

    public static Boolean isEntryValid(BlockchainEntry blockchainEntry, long minEntryID) {
        if(blockchainEntry.transactionID() < minEntryID) {
            return false;
        }
        Signature signature;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(blockchainEntry.publicKey());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            signature.update(ByteBuffer.allocate(4).putInt(blockchainEntry.transactionID()).array());
            signature.update(blockchainEntry.transaction().toString().getBytes(USED_CHARSET));
            byte[] signatureBytes = Base64.getDecoder().decode(blockchainEntry.transactionSignature());
            return signature.verify(signatureBytes);
        } catch (SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

}