package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillStdReqService {

    BillStdReqResponseDto findCurrentByReqId(String billStdReqId);

    BillStdReqResponseDto findBySubsId(String subsId);

    BillStdReqResponseDto create(BillStdReqRequestDto dto);

    BillStdReqResponseDto save(String billStdReqId, BillStdReqRequestDto dto);

    BillStdReqResponseDto changeStatus(String billStdReqId, String newStatus, String createdBy);

    void delete(String billStdReqId);

    Page<SubsBillStdReqResponseDto> findList(String keyword, Pageable pageable);

    List<BillStdReqResponseDto> findTodoList();
}
