package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {

    List<Project> findByStatus(ProjectStatus status);

    Page<Project> findAll(Specification<Project> spec, Pageable pageable);

    // Методы для статистики фильтров
    @Query("SELECT COALESCE(MIN(p.area), 0) FROM Project p WHERE p.area IS NOT NULL AND p.status = 'AVAILABLE'")
    Double getMinArea();

    @Query("SELECT COALESCE(MAX(p.area), 0) FROM Project p WHERE p.area IS NOT NULL AND p.status = 'AVAILABLE'")
    Double getMaxArea();

    @Query("SELECT COALESCE(MIN(p.price), 0) FROM Project p WHERE p.price IS NOT NULL AND p.status = 'AVAILABLE'")
    Double getMinPrice();

    @Query("SELECT COALESCE(MAX(p.price), 0) FROM Project p WHERE p.price IS NOT NULL AND p.status = 'AVAILABLE'")
    Double getMaxPrice();

    @Query("SELECT COALESCE(MIN(p.floors), 0) FROM Project p WHERE p.floors IS NOT NULL AND p.status = 'AVAILABLE'")
    Integer getMinFloors();

    @Query("SELECT COALESCE(MAX(p.floors), 0) FROM Project p WHERE p.floors IS NOT NULL AND p.status = 'AVAILABLE'")
    Integer getMaxFloors();

    // Поиск похожих проектов - исправленный запрос
    @Query("""
        SELECT p FROM Project p 
        WHERE p.status = 'AVAILABLE' 
        AND p.id != :excludeId
        AND p.area IS NOT NULL
        AND p.price IS NOT NULL
        AND p.floors IS NOT NULL
        ORDER BY 
            CASE WHEN p.area IS NOT NULL AND :targetArea IS NOT NULL 
                 THEN ABS(p.area - :targetArea) ELSE 999999 END ASC,
            CASE WHEN p.price IS NOT NULL AND :targetPrice IS NOT NULL 
                 THEN ABS(p.price - :targetPrice) ELSE 999999 END ASC,
            CASE WHEN p.floors IS NOT NULL AND :targetFloors IS NOT NULL 
                 THEN ABS(p.floors - :targetFloors) ELSE 999999 END ASC
        """)
    List<Project> findSimilarProjects(
            @Param("targetArea") Double targetArea,
            @Param("targetPrice") Double targetPrice,
            @Param("targetFloors") Integer targetFloors,
            @Param("excludeId") UUID excludeId);

    // Поиск по названию или описанию
    @Query("""
        SELECT p FROM Project p 
        WHERE (LOWER(p.title) LIKE LOWER(:searchPattern) 
           OR LOWER(p.description) LIKE LOWER(:searchPattern))
        AND p.status = 'AVAILABLE'
        """)
    List<Project> searchByTitleOrDescription(@Param("searchPattern") String searchPattern);

    // Поиск проектов в диапазоне цены
    @Query("SELECT p FROM Project p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.status = 'AVAILABLE'")
    List<Project> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Поиск проектов в диапазоне площади
    @Query("SELECT p FROM Project p WHERE p.area BETWEEN :minArea AND :maxArea AND p.status = 'AVAILABLE'")
    List<Project> findByAreaRange(@Param("minArea") Double minArea, @Param("maxArea") Double maxArea);

    // Поиск проектов по количеству этажей
    @Query("SELECT p FROM Project p WHERE p.floors = :floors AND p.status = 'AVAILABLE'")
    List<Project> findByFloors(@Param("floors") Integer floors);

    // Поиск проектов по нескольким критериям
    @Query("""
        SELECT p FROM Project p 
        WHERE (:minArea IS NULL OR p.area >= :minArea)
        AND (:maxArea IS NULL OR p.area <= :maxArea)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:minFloors IS NULL OR p.floors >= :minFloors)
        AND (:maxFloors IS NULL OR p.floors <= :maxFloors)
        AND p.status = 'AVAILABLE'
        """)
    List<Project> findByMultipleCriteria(
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minFloors") Integer minFloors,
            @Param("maxFloors") Integer maxFloors);

    // Подсчет проектов по статусу
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    long countByStatus(@Param("status") ProjectStatus status);

    // Поиск самых популярных проектов (по количеству заявок)
    @Query("""
        SELECT p FROM Project p 
        WHERE p.id IN (
            SELECT cr.project.id FROM ConstructionRequest cr 
            WHERE cr.status = 'APPROVED'
            GROUP BY cr.project.id
            ORDER BY COUNT(cr) DESC
        )
        AND p.status = 'AVAILABLE'
        """)
    List<Project> findPopularProjects(Pageable pageable);

    // Поиск новейших проектов
    @Query("SELECT p FROM Project p WHERE p.status = 'AVAILABLE' ORDER BY p.createdAt DESC")
    List<Project> findNewestProjects(Pageable pageable);

    // Поиск самых дешевых проектов
    @Query("SELECT p FROM Project p WHERE p.status = 'AVAILABLE' AND p.price IS NOT NULL ORDER BY p.price ASC")
    List<Project> findCheapestProjects(Pageable pageable);

    // Поиск самых дорогих проектов
    @Query("SELECT p FROM Project p WHERE p.status = 'AVAILABLE' AND p.price IS NOT NULL ORDER BY p.price DESC")
    List<Project> findMostExpensiveProjects(Pageable pageable);

    // Поиск проектов с самым быстрым сроком строительства
    @Query("SELECT p FROM Project p WHERE p.status = 'AVAILABLE' AND p.constructionTime IS NOT NULL ORDER BY p.constructionTime ASC")
    List<Project> findFastestConstructionProjects(Pageable pageable);

    // Поиск проектов по категориям площади
    @Query("SELECT p FROM Project p WHERE p.status = 'AVAILABLE' AND p.area IS NOT NULL AND p.area BETWEEN :minArea AND :maxArea")
    List<Project> findByAreaCategory(
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea);

    // Статистика по проектам
    @Query("""
        SELECT 
            COUNT(p) as total,
            SUM(CASE WHEN p.status = 'AVAILABLE' THEN 1 ELSE 0 END) as available,
            SUM(CASE WHEN p.status = 'UNDER_CONSTRUCTION' THEN 1 ELSE 0 END) as under_construction,
            SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed,
            AVG(p.area) as avg_area,
            AVG(p.price) as avg_price,
            MIN(p.constructionTime) as min_construction_time,
            MAX(p.constructionTime) as max_construction_time
        FROM Project p
        """)
    Object[] getProjectStatistics();
}