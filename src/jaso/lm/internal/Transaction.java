package jaso.lm.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jaso.db.ResourceId;
import jaso.db.TransactionId;
import jaso.lm.LockInfo;

public class Transaction {
    
    public final TransactionId tid;
    public final HashMap<ResourceId, Lock> locks = new HashMap<>();
    

    public Transaction(TransactionId tid) {
        this.tid = tid;
    }

    public void addLock(Lock lock) {
        locks.put(lock.getResourceId(), lock);     
    }
    
    public Lock getLock(ResourceId rid) {
        return locks.get(rid);
    }
    
    public List<LockInfo> getLockInfos() {
        ArrayList<LockInfo> result = new ArrayList<LockInfo>(locks.size());
        for(Lock lock : locks.values()) {
            result.add(lock.toLockInfo());
        }
        return result;        
    }

    @Override
    public String toString() {
        return "Transaction [tid=" + tid + ", lock count=" + locks.size() + "]";
    }
    
    
}
