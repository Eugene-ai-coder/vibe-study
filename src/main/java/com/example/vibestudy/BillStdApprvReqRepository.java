package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillStdApprvReqRepository extends JpaRepository<BillStdApprvReq, String> {
    List<BillStdApprvReq> findByBillStdReqId(String billStdReqId);
}
