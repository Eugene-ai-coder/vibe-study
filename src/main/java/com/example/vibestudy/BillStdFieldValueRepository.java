package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillStdFieldValueRepository
        extends JpaRepository<BillStdFieldValue, BillStdFieldValueId> {

    List<BillStdFieldValue> findByIdBillStdId(String billStdId);

    long countByIdFieldCd(String fieldCd);

    void deleteByIdBillStdId(String billStdId);
}
