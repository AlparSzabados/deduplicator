package deduplicator.repository;

import deduplicator.model.DuplicateDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeduplicatorRepository extends JpaRepository<DuplicateDTO, Long> {

    public List<DuplicateDTO> getDuplicatesBySizeAndStartBytes(String size, String startBytes);

    public DuplicateDTO getDuplicateBySizeAndStartBytesAndPath(String size, String startBytes, String name);

}