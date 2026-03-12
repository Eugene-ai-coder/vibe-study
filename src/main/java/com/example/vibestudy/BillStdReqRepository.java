package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillStdReqRepository extends JpaRepository<BillStdReq, Long> {

    Optional<BillStdReq> findByBillStdReqIdAndEffEndDt(String billStdReqId, LocalDateTime effEndDt);

    List<BillStdReq> findBySubsIdAndEffEndDt(String subsId, LocalDateTime effEndDt);

    List<BillStdReq> findAllByBillStdReqId(String billStdReqId);

    List<BillStdReq> findByBillStdIdAndEffEndDt(String billStdId, LocalDateTime effEndDt);

    List<BillStdReq> findByEffEndDtAndStdRegStatCdNotIn(LocalDateTime effEndDt, List<String> excludedStatuses);
}
