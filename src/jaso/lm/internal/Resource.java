package jaso.lm.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jaso.db.ResourceId;
import jaso.db.TransactionId;
import jaso.lm.LockCallback;
import jaso.lm.LockInfo;
import jaso.lm.LockStatus;
import jaso.lm.LockType;

public class Resource {
    public final ResourceId rid;
    
    private HashMap<TransactionId, Lock> locks = new HashMap<>();    
    
    private Lock exclusiveLock = null;
    private Lock promotingLock = null;
    
    // the doubly linked list of pending locks
    private LinkedList queue = new LinkedList();
    
    // doubly linked list of granted shared locks
    private LinkedList sharedLocks = new LinkedList();
    
    
    public Resource(ResourceId rid) {
        this.rid = rid;
    }        
   
    public List<LockInfo> getLockInfos() {
        ArrayList<LockInfo> result = new ArrayList<LockInfo>(locks.size());
        for(Lock lock : locks.values()) {
            result.add(lock.toLockInfo());
        }
        return result;        
    }

    public boolean inUse() {
        if(sharedLocks.size() > 0) return true;
        
        if(exclusiveLock != null) return true;
        
        if(! queue.isEmpty()) return true;

        return false;
    }

    
    public void addLock(Lock lock) {        
       locks.put(lock.getTransactionId(), lock); 
       queue.appendTail(lock);
       update();
    }


    public void promote(Lock lock, LockCallback callback) {
        if(lock.type != LockType.SHARED) {
            callback.statusChanged(lock.transaction.tid, rid, LockStatus.NOT_SHARED, lock.status);
            return;
        }

        // if the lock has not been granted
        if(lock.isResultPending()) {
            lock.setPromoting(callback);
            return;
        }        
        
        if(promotingLock != null) {
            if(promotingLock == lock) {
                callback.statusChanged(lock.transaction.tid, rid, LockStatus.DUPLICATE_LOCK, lock.status);
            } else {
                callback.statusChanged(lock.transaction.tid, rid, LockStatus.WOULD_DEADLOCK, lock.status);
            }
            return;
        }
        
        promotingLock = lock;
        lock.setResult(callback);
        update();
    }
    

    public void demote(Lock lock, LockCallback callback) {
        if(lock.type != LockType.EXCLUSIVE) {
            callback.statusChanged(lock.transaction.tid, rid, LockStatus.NOT_EXCLUSIVE, lock.status);
            return;
        }
        
        // if this lock is the promoting lock, then it must already be
        // shared and waiting for the other shared locks to go away.
        // We can set the status of the previous request to DEMOTING
        // and make the current request GRANTED.
        if(lock == promotingLock) {
            promotingLock = null;
            lock.setDemoting(callback);
            callback.statusChanged(lock.transaction.tid, rid, LockStatus.GRANTED_SHARED, lock.status);
            update();
            return;
        }
        
        if(lock == exclusiveLock) {
            assert(lock.status == LockStatus.GRANTED_EXCLUSIVE);
            exclusiveLock = null;
            sharedLocks.appendTail(lock);
            lock.type = LockType.SHARED;
            callback.statusChanged(lock.transaction.tid, rid, LockStatus.GRANTED_SHARED, lock.status);
            update();
            return;           
        }
        
        lock.setDemoting(callback);
        update();
    }

    public void removeLock(Lock lock, LockStatus status) {
        Lock tmp = locks.remove(lock.getTransactionId());
        assert(tmp == lock);
        
        if(lock == promotingLock) {
            // a lock that is promoting must already be granted as a shared lock. 
            // and we haven't informed the requester that it has been granted,
            // so we will inform them that the lock will not be granted and the
            // reason is either ABORTED or COMMITED (status supplied)
            promotingLock = null;
            sharedLocks.removeNode(lock);
            lock.setStatus(status); 
         } else if(lock.isResultPending()) {
            // any locks that have outstanding notifications that are not locks
            // being promoted, must be in the queue waiting to be granted. So
            // inform the waiter that the lock won't be granted and then remove 
            // it from the list.
            lock.setStatus(status); 
            queue.removeNode(lock);
        } else if(lock == exclusiveLock) {
            // this lock has been granted as an exclusive lock.
            // just clear the exclusive lock, nothing else needed.
            exclusiveLock = null;
        } else {
            // this lock must be a shared lock that has been granted.
            sharedLocks.removeNode(lock);
        }
        
        update();
    }
 
    
    private void update() {
        
        // if there is a lock waiting to be promoted,
        // can we promote the lock to an exclusive lock?
        if(promotingLock != null) {
            
            // we can only promote the lock if there is only one shared lock
            // and the remaining shared lock better be the lock being promoted.
            if(sharedLocks.size() == 1) {
                // remove the lock from the shared locks
                sharedLocks.removeNode(promotingLock);
                
                // and then promote it.
                exclusiveLock = promotingLock;
                promotingLock = null;
                exclusiveLock.grantedExclusive();                
            }
            
            // no matter whether we did the promotion or not, there
            // is nothing else we can do until the promoting lock 
            // is dealt with (either granted or ABORT/COMMIT)
            return;
        }
        
        // grant as many locks as we can.
        while(true) {
            if(exclusiveLock != null) return;
            if(queue.isEmpty()) return;
            
            LockType headType = queue.peekHead().type;
            
            if(headType == LockType.SHARED) {
                Lock lock = queue.removeHead();
                sharedLocks.appendTail(lock);
                lock.grantedShared();
            } else if (headType == LockType.EXCLUSIVE) {
                if(sharedLocks.size() == 0) {
                    Lock lock = queue.removeHead();
                    exclusiveLock = lock;
                    lock.grantedExclusive();
                }
                return ;
            } else {
                throw new RuntimeException("Unhandled lock type:"+headType);
            }
        }
    }
    
    
    @Override
    public String toString() {
        return "Resource [rid=" + rid + ", lock count=" + locks.size() + ", sharedLock count=" + sharedLocks.size() + ", exclusiveLock="
                + exclusiveLock + ", promotingLock=" + promotingLock + "]";
    }
}
