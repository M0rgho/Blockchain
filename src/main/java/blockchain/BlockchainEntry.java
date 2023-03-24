package blockchain;

import java.io.Serial;
import java.io.Serializable;
import java.security.PublicKey;

// transactionSignature is encoded in Base64
public record BlockchainEntry(int transactionID, Transaction transaction, String transactionSignature, PublicKey publicKey) implements Serializable {
    @Serial
    private static final long serialVersionUID = 15L;
}
