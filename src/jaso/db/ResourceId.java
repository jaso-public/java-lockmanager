package jaso.db;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ResourceId {
    
    static private AtomicLong nextResourceId = new AtomicLong(1);
    
    public final String name; // name is just used for debugging
    public final long id;
    
    public ResourceId(String name) {
        if(name == null) name = "";
        this.name = name;
        this.id = nextResourceId.getAndIncrement();
    }

    @Override
    public String toString() {
        return "ResourceId [name='" + name + "', id=" + id + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceId other = (ResourceId) obj;
        return Objects.equals(name, other.name) && id == other.id;
    }   
}
