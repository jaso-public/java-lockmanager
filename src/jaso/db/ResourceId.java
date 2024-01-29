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
    
    /**
     * constructor that set both the name and the id.
     * This constructor is really only used for testing.
     * 
     * Note: this constructor lets us make duplicate non-unique ResourceIds
     * 
     * @param name the of the resource
     * @param id the unique identifier for the resource.
     */
    public ResourceId(String name, long id) {
        if(name == null) name = "";
        this.name = name;
        this.id = id;
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
