package jaso.lm.internal;

import jaso.db.ResourceId;
import jaso.db.TransactionId;
import jaso.lm.LockCallback;
import jaso.lm.LockInfo;
import jaso.lm.LockStatus;
import jaso.lm.LockType;



public class Lock {
    final Transaction transaction;
    final Resource resource;
    LockType type;
    LockStatus status;
    private LockCallback callback;
    
    Lock prev = null;
    Lock next = null;
    

    public Lock(Transaction t, Resource r, LockType type, LockStatus status, LockCallback callback) {
        this.transaction = t;
        this.resource = r;
        this.type = type;
        this.status = status;
        this.callback = callback;
    }
    

    public TransactionId getTransactionId() {
        return transaction.tid;
    }
    
    public ResourceId getResourceId() {
        return resource.rid;
    }
        
    public boolean isResultPending() {
        return callback != null;
    }
    
    public void setResult(LockCallback nextCallback) {
        if(callback != null) throw new RuntimeException("WTF?, internal error, LockResult was not null");
        callback = nextCallback;
    }
    
    public void setStatus(LockStatus newStatus) {
        if(callback == null) throw new RuntimeException("WTF?, internal error, LockResult was null");
        LockStatus previousStatus = status;
        status = newStatus;
        callback.statusChanged(transaction.tid, resource.rid, newStatus, previousStatus); 
        callback = null;
    }
        
     public void setPromoting(LockCallback nextResult) {
        setStatus(LockStatus.PROMOTING);
        type = LockType.EXCLUSIVE;
        setResult(nextResult);
    }

    public void setDemoting(LockCallback nextResult) {
        setStatus(LockStatus.DEMOTING);
        type = LockType.SHARED;
        setResult(nextResult);
    }

    public void grantedExclusive() {
        assert(type == LockType.EXCLUSIVE);
        setStatus(LockStatus.GRANTED_EXCLUSIVE);
    }
    
    public void grantedShared() {
        assert(type == LockType.SHARED);
        setStatus(LockStatus.GRANTED_SHARED);
    }
    
    @Override
    public String toString() {
        return "Lock [transactionId=" + transaction.tid + ", resourceId=" + resource.rid + ", type=" + type + ", status=" + status
                + ", callback=" + (callback!=null) + "]";
    }


    public LockInfo toLockInfo() {
        return new LockInfoImpl(getTransactionId(), getResourceId(), type, status);
    }

}
