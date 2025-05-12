package bd.org.quantum.hrm.idCard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdCardRecordsRepository extends JpaRepository<IdCardRecords, Long> {
    IdCardRecords findByEncriptedIdAndYear(String encriptedId, int year);
    List<IdCardRecords> findAllByIdIn(Long[] ids);
    List<IdCardRecords> findByStageAndYear(String stage, int year);
}
