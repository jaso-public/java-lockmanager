package jaso.db;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionId {
    
    static private AtomicLong nextTransactionId = new AtomicLong(1);
    
    public final String name; // name is just used for debugging
    public final long tid;
    
    public TransactionId(String name) {
        if(name == null) name = "";
        this.name = name;
        this.tid = nextTransactionId.getAndIncrement();
    }

    @Override
    public String toString() {
        return "TransactionId [name='" + name + "', tid=" + tid + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransactionId other = (TransactionId) obj;
        return Objects.equals(name, other.name) && tid == other.tid;
    }   
}
