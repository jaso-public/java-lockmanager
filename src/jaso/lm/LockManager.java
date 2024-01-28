package jaso.lm;

import java.util.List;

import jaso.db.ResourceId;
import jaso.db.TransactionId;

public interface LockManager {
    
    void beginTransaction(TransactionId tid);

    boolean transactionInflight(TransactionId tid);
    
    void commitTransaction(TransactionId tid);

    void abortTransaction(TransactionId tid);

    

    
    void sharedLock(TransactionId tid, ResourceId rid, LockCallback callback);
    
    void exclusiveLock(TransactionId tid, ResourceId rid, LockCallback callback);
    
    void changeToExclusiveLock(TransactionId tid, ResourceId rid, LockCallback callback);
    
    void changeToSharedLock(TransactionId tid, ResourceId rid, LockCallback callback);
    
  
    
    
    List<TransactionId> getTransactionIds();
    
    List<LockInfo> getLockInfos(TransactionId tid);    
    
    List<ResourceId> getResourceIds();
    
    List<LockInfo> getLockInfos(ResourceId rid);  
    
 }
