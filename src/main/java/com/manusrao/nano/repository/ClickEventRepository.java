package com.manusrao.nano.repository;

import com.manusrao.nano.domains.ClickEvent;
import com.manusrao.nano.domains.ClickEventId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface ClickEventRepository extends JpaRepository<ClickEvent, ClickEventId> {

    long countByUrlIdAndClickedAtBetween(Long urlId, Instant from, Instant to);
}
