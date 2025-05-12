package bd.org.quantum.hrm.yr_cl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface YrClRepository extends JpaRepository<YrCl, Long>, JpaSpecificationExecutor<YrCl> {

    YrCl getById(Long id);
    boolean existsByYear(Integer year);

}
