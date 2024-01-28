package jaso.lm.internal;

public class LinkedList {
    private int count = 0;
    private Lock head = null;
    private Lock tail = null;

    Lock peekHead() {
        return head;
    }
    
    int size() {
        return  count;
    }
    
    boolean isEmpty() {
        return count == 0;
    }
    
   
    void appendTail(Lock lock) {
        if(tail == null) {
            head = lock;
            tail = lock;
            lock.prev = null;
            lock.next = null;      
        } else {            
            lock.prev = tail;
            lock.next = null;
            tail.next = lock;
            tail = lock;
        }
        count++;
    }
    
    Lock removeHead() {
        if(head==null) return null;
        Lock result = head;
        removeNode(result);
        return result;
    }
        
    void removeNode(Lock node) {
        if(node == head) {
            if( node == tail) {
                head = null;
                tail = null;
            } else {
                head = node.next;
                head.prev = null;            
            }
        } else if(node == tail) {
            tail = node.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
        count--;
    }

}
