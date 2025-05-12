package bd.org.quantum.hrm.designation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long>, JpaSpecificationExecutor<Designation> {
    Designation findByName(String name);
    Designation getById(Long id);
    boolean existsByName(String name);

    @Query("SELECT d FROM Designation d WHERE d.status = 1 ORDER BY d.sortingOrder")
    public List<Designation> getAllOrderBySortingOrder();
}
