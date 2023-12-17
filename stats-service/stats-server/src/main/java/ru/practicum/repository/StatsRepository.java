package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> findStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findUniqueStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
