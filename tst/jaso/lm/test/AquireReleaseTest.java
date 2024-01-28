package jaso.lm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import jaso.db.TransactionId;
import jaso.lm.LockManager;
import jaso.lm.internal.LockManagerImpl;

class AquireReleaseTest {

    @Test
    void testBeginCommit() {
        LockManager lm = new LockManagerImpl();
        
        TransactionId tid = new TransactionId("BeginEnd");
        
        assertFalse(lm.transactionInflight(tid));

        lm.beginTransaction(tid);
        
        assertTrue(lm.transactionInflight(tid));
        
        // make sure the lock manager reports this transaction
        List<TransactionId> inflightTids = lm.getTransactionIds();
        assertEquals(1, inflightTids.size());
        assertEquals(tid, inflightTids.get(0));
        
        lm.commitTransaction(tid);
        
        assertFalse(lm.transactionInflight(tid));
        
        inflightTids = lm.getTransactionIds();
        assertEquals(0, inflightTids.size());
    
        
    }

}
