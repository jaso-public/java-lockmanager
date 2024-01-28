package jaso.lm.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jaso.db.ResourceId;
import jaso.db.TransactionId;
import jaso.lm.LockCallback;
import jaso.lm.LockInfo;
import jaso.lm.LockManager;
import jaso.lm.LockStatus;
import jaso.lm.LockType;
import jaso.lm.exception.DuplicateTransactionException;
import jaso.lm.exception.UnknownTransactionIdException;

public class LockManagerImpl implements LockManager {
 
    private HashMap<TransactionId, Transaction> transactions = new HashMap<>();
    private HashMap<ResourceId, Resource> resources = new HashMap<>();
    
    @Override
    public synchronized void beginTransaction(TransactionId tid) {
        Transaction t = transactions.get(tid);
        if(t != null) throw new DuplicateTransactionException(tid);
        t = new Transaction(tid);
        transactions.put(tid, t);
    }

    @Override
    public synchronized boolean transactionInflight(TransactionId tid) {
        return transactions.containsKey(tid);
    }

    @Override
    public void commitTransaction(TransactionId tid) {
        removeTransaction(tid, LockStatus.TRANSACTION_COMMITTED);   
    }

    @Override
    public void abortTransaction(TransactionId tid) {
        System.out.println("abort tid:"+tid);      
        removeTransaction(tid, LockStatus.TRANSACTION_ABORTED);        
    }
    
    private synchronized void removeTransaction(TransactionId tid, LockStatus status) {
        Transaction t = transactions.remove(tid);
        if(t == null) throw new UnknownTransactionIdException(tid); 
        
        for(Lock lock : t.locks.values()) {
            System.out.println("removeLock lock:"+lock);
            lock.resource.removeLock(lock, status);
            if(! lock.resource.inUse()) {
                // if the resource longer has an locks or 
                // waiting transactions then we can remove
                // it from the list of known resources.
                resources.remove(lock.resource.rid);
            }
        }
    }

    @Override
    public void sharedLock(TransactionId tid, ResourceId rid, LockCallback callback) {
        newLock(tid, rid, LockType.SHARED, callback);
    }

    @Override
    public void exclusiveLock(TransactionId tid, ResourceId rid, LockCallback callback) {
        newLock(tid, rid, LockType.EXCLUSIVE, callback);
    }
    
    private synchronized void newLock(TransactionId tid, ResourceId rid, LockType type, LockCallback callback) {
        Transaction t = transactions.get(tid);
        if(t == null) {
            callback.statusChanged(tid, rid, LockStatus.UNKNOWN_TRANSACTION, LockStatus.NEW); 
            return;
        }
        
        Lock lock = t.getLock(rid);
        if(lock != null) {
            callback.statusChanged(tid, rid, LockStatus.DUPLICATE_LOCK, lock.status); 
            return;
        }      

        Resource r = resources.get(rid);
        if(r == null) {
            r = new Resource(rid);
            resources.put(rid, r);
        }
        
        lock = new Lock(t, r, type, LockStatus.WAITING, callback);
        t.addLock(lock);
        r.addLock(lock);
    }

    @Override
    public synchronized void changeToExclusiveLock(TransactionId tid, ResourceId rid, LockCallback callback) {
        Transaction t = transactions.get(tid);
        if(t == null) {
            callback.statusChanged(tid, rid, LockStatus.UNKNOWN_TRANSACTION, LockStatus.NEW); 
            return;
        }

        Lock lock = t.getLock(rid);
        if(lock == null) {
            callback.statusChanged(tid, rid, LockStatus.LOCK_DOES_NOT_EXIST, LockStatus.NEW); 
            return;
        }
                
        lock.resource.promote(lock, callback);
    }

    @Override
    public synchronized void changeToSharedLock(TransactionId tid, ResourceId rid, LockCallback callback) {
        Transaction t = transactions.get(tid);
        if(t == null) {
            callback.statusChanged(tid, rid, LockStatus.UNKNOWN_TRANSACTION, LockStatus.NEW); 
            return;
        }

        Lock lock = t.getLock(rid);
        if(lock == null) {
            callback.statusChanged(tid, rid, LockStatus.LOCK_DOES_NOT_EXIST, LockStatus.NEW); 
            return;
        }
        
        lock.resource.demote(lock, callback);
    }

    
    @Override
    public synchronized List<TransactionId> getTransactionIds() {
        ArrayList<TransactionId> tids = new ArrayList<>(transactions.size());
        for(TransactionId tid : transactions.keySet()) tids.add(tid);
        return tids;
    }

    @Override
    public synchronized List<LockInfo> getLockInfos(TransactionId tid) {
        Transaction t = transactions.get(tid);
        if(t == null) throw new UnknownTransactionIdException(tid);  
        return t.getLockInfos();
    }


    @Override
    public synchronized List<ResourceId> getResourceIds() {
        ArrayList<ResourceId> rids = new ArrayList<>(resources.size());
        for(ResourceId rid : resources.keySet()) rids.add(rid);
        return rids;
    }

    @Override
    public synchronized List<LockInfo> getLockInfos(ResourceId rid) {
        Resource r = resources.get(rid);
        if(r == null) return new ArrayList<LockInfo>();
        return r.getLockInfos();
    }
}
