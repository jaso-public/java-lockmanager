package jaso.lm.internal;

import jaso.db.ResourceId;
import jaso.db.TransactionId;
import jaso.lm.LockInfo;
import jaso.lm.LockStatus;
import jaso.lm.LockType;

public class LockInfoImpl implements LockInfo {
    
    final TransactionId transactionId;
    final ResourceId resourceId;
    final LockType lockType;
    final LockStatus lockStatus;
    
    
    public LockInfoImpl(TransactionId transactionId, ResourceId resourceId, LockType lockType, LockStatus lockStatus) {
        this.transactionId = transactionId;
        this.resourceId = resourceId;
        this.lockType = lockType;
        this.lockStatus = lockStatus;
    }

    @Override
    public TransactionId getTransactionId() {
        return transactionId;
    }

    @Override
    public ResourceId getResourceId() {
        return resourceId;
    }

    @Override
    public LockType getLockType() {
        return lockType;
    }

    @Override
    public LockStatus getLockStatus() {
        return lockStatus;
    }    

    @Override
    public String toString() {
        return "LockInfoImpl [transactionId=" + transactionId + ", resourceId=" + resourceId + ", lockType=" + lockType
                + ", lockStatus=" + lockStatus + "]";
    }

}
