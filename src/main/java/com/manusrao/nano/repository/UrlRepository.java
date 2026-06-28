package com.manusrao.nano.repository;

import com.manusrao.nano.domains.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long> {
}
