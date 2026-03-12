package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpclSubsMthBillQtyRepository extends JpaRepository<SpclSubsMthBillQty, SpclSubsMthBillQtyId>,
        JpaSpecificationExecutor<SpclSubsMthBillQty> {
}
