package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpclSubsMthBillElemRepository extends JpaRepository<SpclSubsMthBillElem, SpclSubsMthBillElemId>,
        JpaSpecificationExecutor<SpclSubsMthBillElem> {
}
