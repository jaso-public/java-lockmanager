package jaso.lm;

import jaso.db.ResourceId;
import jaso.db.TransactionId;

public interface LockCallback {
    void statusChanged(TransactionId tid, ResourceId rid, LockStatus newStatus, LockStatus previousStatus);
}
