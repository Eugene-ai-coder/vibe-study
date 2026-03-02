package com.example.vibestudy;

import java.util.List;

public interface StudyLogService {
    List<StudyLog> getAll();
    StudyLog create(StudyLogRequestDto dto);
    StudyLog update(Long id, StudyLogRequestDto dto);
    void delete(Long id);
}
