package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyLogServiceImpl implements StudyLogService {

    private final StudyLogRepository studyLogRepository;

    public StudyLogServiceImpl(StudyLogRepository studyLogRepository) {
        this.studyLogRepository = studyLogRepository;
    }

    @Override
    public List<StudyLog> getAll() {
        return studyLogRepository.findAll();
    }

    @Override
    public StudyLog create(StudyLogRequestDto dto) {
        StudyLog log = new StudyLog();
        log.setContent(dto.getContent());
        log.setDate(dto.getDate());
        log.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
        log.setCreatedDt(LocalDateTime.now());
        return studyLogRepository.save(log);
    }

    @Override
    public StudyLog update(Long id, StudyLogRequestDto dto) {
        StudyLog log = studyLogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "학습 로그를 찾을 수 없습니다."));
        log.setContent(dto.getContent());
        log.setDate(dto.getDate());
        log.setUpdatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
        log.setUpdatedDt(LocalDateTime.now());
        return studyLogRepository.save(log);
    }

    @Override
    public void delete(Long id) {
        if (!studyLogRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "학습 로그를 찾을 수 없습니다.");
        }
        studyLogRepository.deleteById(id);
    }
}
