package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubsMthBillQtyRepository extends JpaRepository<SubsMthBillQty, SubsMthBillQtyId>,
        JpaSpecificationExecutor<SubsMthBillQty> {
}
