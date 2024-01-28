package jaso.lm.exception;

import jaso.db.TransactionId;

@SuppressWarnings("serial")

public class DuplicateTransactionException extends RuntimeException {
    public final TransactionId tid;
    
    public DuplicateTransactionException(TransactionId tid) {
        this.tid = tid;
    }

    @Override
    public String toString() {
        return "DuplicateTransactionException [tid=" + tid + "]";
    }
}
