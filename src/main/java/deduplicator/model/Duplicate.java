package deduplicator.model;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "duplicates")
@Entity
public class Duplicate implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public String id;

    @Column
    public int size;

    @Column
    public String path;

    public Duplicate(String path) {
        this.path = path.toString();
    }
}
