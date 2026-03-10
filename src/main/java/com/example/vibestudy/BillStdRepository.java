package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillStdRepository extends JpaRepository<BillStd, String> {

    List<BillStd> findBySubsIdAndLastEffYn(String subsId, String lastEffYn);

    long countBySubsId(String subsId);

    List<BillStd> findByStdRegStatCdNotIn(List<String> excludedStatuses);
}
