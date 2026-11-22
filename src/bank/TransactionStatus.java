package bank;

/** The stages a transaction goes through (see the state diagram). */
public enum TransactionStatus {
    INITIATED, VALIDATED, PROCESSING, COMPLETED, REJECTED, ROLLED_BACK
}
