package jaso.lm.exception;

import jaso.db.TransactionId;

@SuppressWarnings("serial")

public class UnknownTransactionIdException extends RuntimeException {
    public final TransactionId tid;
    
    public UnknownTransactionIdException(TransactionId tid) {
        this.tid = tid;
    }

    @Override
    public String toString() {
        return "UnknownTransactionId [tid=" + tid + "]";
    }
}
