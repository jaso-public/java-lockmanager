package jaso.lm.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import jaso.db.ResourceId;
import jaso.db.TransactionId;
import jaso.lm.LockInfo;
import jaso.lm.LockManager;
import jaso.lm.LockStatus;
import jaso.lm.internal.LockManagerImpl;

class TwoExclusiveLockTest {

    //@Test
    void testBeginCommit() throws InterruptedException, ExecutionException {
        LockManager lm = new LockManagerImpl();
        
        TransactionId tid1 = new TransactionId("Tid1");
        lm.beginTransaction(tid1);
        
        TransactionId tid2 = new TransactionId("Tid2");
        lm.beginTransaction(tid2);
        
        ResourceId rid = new ResourceId("resource");
        
        LockResult lr1 = new LockResult();
        lm.exclusiveLock(tid1, rid, lr1);
        LockResult lr2 = new LockResult();
        lm.exclusiveLock(tid2, rid, lr2);
        
        assertTrue(lr1.isDone());
        assertEquals(LockStatus.GRANTED_EXCLUSIVE, lr1.get());
        
        assertFalse(lr2.isDone());
        
        
        lm.commitTransaction(tid1);
        
        assertTrue(lr2.isDone());
        assertEquals(LockStatus.GRANTED_EXCLUSIVE, lr2.get());
    }
    
    @Test
    void testDeadlock() throws InterruptedException, ExecutionException {
        
        LockManager lm = new LockManagerImpl();
        
        TransactionId tid1 = new TransactionId("Tid1");
        lm.beginTransaction(tid1);
        
        TransactionId tid2 = new TransactionId("Tid2");
        lm.beginTransaction(tid2);
        
        ResourceId rid1 = new ResourceId("R1");
        ResourceId rid2 = new ResourceId("R2");
        
        LockResult lr11 = new LockResult();
        lm.exclusiveLock(tid1, rid1, lr11);
        LockResult lr22 = new LockResult();
        lm.exclusiveLock(tid2, rid2, lr22);
        
        LockResult lr12 = new LockResult();
        lm.exclusiveLock(tid1, rid2, lr12);
        LockResult lr21 = new LockResult();
        lm.exclusiveLock(tid2, rid1, lr21); // deadlock
        
        assertTrue(lr11.isDone());
        assertTrue(lr22.isDone());
        
        assertFalse(lr12.isDone());
        assertFalse(lr21.isDone());   
        
        lm.abortTransaction(tid2);
        
        assertTrue(lr12.isDone());
        assertEquals(LockStatus.GRANTED_EXCLUSIVE, lr12.get());
        assertTrue(lr21.isDone());   
        assertEquals(LockStatus.TRANSACTION_ABORTED, lr21.get());
        
        
        for(LockInfo li : lm.getLockInfos(rid1)) {
            System.out.println(li);
        }
     }


}
