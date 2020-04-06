package deduplicator.model;

import javax.persistence.*;
import java.nio.file.Path;
import java.util.List;

@Table(name = "duplicates")
@Entity
public class DuplicateDTO {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public String id;

    @Column(name = "size")
    public String size;

    @Column(name = "name")
    public String name;

    @Column(name = "path")
    public String path;

    @Column(name = "start_bytes")
    public String startBytes;

    @Column(name = "checksum")
    public String checkSum;

    @Column(name = "last_modified_date")
    public String lastModified;
}
