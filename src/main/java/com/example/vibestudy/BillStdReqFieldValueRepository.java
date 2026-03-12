package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillStdReqFieldValueRepository
        extends JpaRepository<BillStdReqFieldValue, BillStdReqFieldValueId> {

    List<BillStdReqFieldValue> findByIdBillStdReqSeq(Long billStdReqSeq);

    void deleteByIdBillStdReqSeq(Long billStdReqSeq);
}
