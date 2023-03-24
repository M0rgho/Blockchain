package blockchain;

import java.io.Serial;
import java.io.Serializable;

public record Transaction (String sender, String receiver, long amount) implements Serializable {
    @Serial
    private static final long serialVersionUID = 15L;

    @Override
    public String toString() {
        if(sender == null) {
            return receiver + " gets " + amount + " VC";
        } else {
            return sender + " send " + amount + " VC to " + receiver;
        }
    }
}