package com.example.vibestudy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubsBillStdServiceImpl implements SubsBillStdService {

    private final EntityManager em;

    public SubsBillStdServiceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<SubsBillStdResponseDto> findList(String keyword, Pageable pageable) {
        String baseJpql = """
            FROM Subscription s
            LEFT JOIN BillStd b ON b.subsId = s.subsId AND b.lastEffYn = 'Y'
            WHERE s.subsStatusCd <> 'TERMINATED'
            """;

        if (keyword != null && !keyword.isBlank()) {
            baseJpql += " AND (s.subsId LIKE :kw OR s.subsNm LIKE :kw)";
        }

        String selectJpql = """
            SELECT new com.example.vibestudy.SubsBillStdResponseDto(
                s.subsId, s.subsNm, s.subsStatusCd,
                b.billStdId, CAST(NULL AS string), b.stdRegStatCd, b.effStartDt, b.effEndDt
            )
            """ + baseJpql + " ORDER BY s.subsId ASC";

        String countJpql = "SELECT COUNT(s) " + baseJpql;

        TypedQuery<SubsBillStdResponseDto> query = em.createQuery(selectJpql, SubsBillStdResponseDto.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        if (keyword != null && !keyword.isBlank()) {
            String kw = "%" + keyword.trim() + "%";
            query.setParameter("kw", kw);
            countQuery.setParameter("kw", kw);
        }

        long total = countQuery.getSingleResult();

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<SubsBillStdResponseDto> content = query.getResultList();

        return new PageImpl<>(content, pageable, total);
    }
}
