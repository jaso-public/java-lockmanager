package jaso.lm;

import jaso.db.ResourceId;
import jaso.db.TransactionId;

public interface LockInfo {
    TransactionId getTransactionId();
    ResourceId getResourceId();
    LockType getLockType();
    LockStatus getLockStatus();
}
