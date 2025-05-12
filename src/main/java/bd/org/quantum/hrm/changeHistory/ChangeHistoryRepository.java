package bd.org.quantum.hrm.changeHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeHistoryRepository extends JpaRepository<ChangeHistory, Integer> {
    List<ChangeHistory> findByDataIdAndClassNameOrderByActionTimeDesc(long dataId, String className);
}
