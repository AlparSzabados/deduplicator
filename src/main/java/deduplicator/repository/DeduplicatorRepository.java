package deduplicator.repository;

import deduplicator.model.Duplicate;
import org.openjdk.jmh.util.Deduplicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;

@Repository
public interface DeduplicatorRepository extends JpaRepository<Duplicate, Long> {

}