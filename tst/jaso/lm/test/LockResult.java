package jaso.lm.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jaso.db.ResourceId;
import jaso.db.TransactionId;
import jaso.lm.LockCallback;
import jaso.lm.LockStatus;

public class LockResult implements LockCallback {

    private LockStatus previousStatus = null;
    private LockStatus newStatus = null;
    private CountDownLatch latch = new CountDownLatch(1);


    public boolean isDone() {
        return latch == null;
    }

    public LockStatus get() throws InterruptedException, ExecutionException {
        CountDownLatch l = latch;
        if(l != null) latch.await();
        return newStatus;
    }

    public LockStatus get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        CountDownLatch l = latch;
        if(l != null) latch.await();
        return newStatus;
    }
    
    public LockStatus getPreviousStatus() throws InterruptedException, ExecutionException {
        if(isDone()) return previousStatus;
        return null;
    }

    @Override
    public void statusChanged(TransactionId tid, ResourceId rid, LockStatus newStatus, LockStatus previousStatus) {
        this.newStatus = newStatus;
        this.previousStatus = previousStatus;
        latch.countDown();
        latch = null;
    }
}
