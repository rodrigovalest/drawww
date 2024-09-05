package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.models.theme.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    @Query(value = "SELECT * FROM tb_themes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Theme findRandomTheme();
}
