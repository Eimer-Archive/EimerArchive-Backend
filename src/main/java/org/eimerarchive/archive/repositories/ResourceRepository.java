package org.eimerarchive.archive.repositories;

import org.eimerarchive.archive.model.ECategory;
import org.eimerarchive.archive.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {


    boolean existsByNameEqualsIgnoreCase(String name);

    @Query("SELECT COUNT(resource)>0 FROM Resource resource WHERE NOT resource.id = ?1 AND resource.name = ?2")
    boolean existsByNameEqualsIgnoreCaseAndIdEqualsNot(int id, String name);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.name = ?2, resource.blurb = ?3, resource.description = ?4, " +
            "resource.source = ?5, resource.category = ?6 WHERE resource.id = ?1")
    void setInfo(int id, String name, String blurb, String description, String source, String category);

    List<Resource> findAllByAuthor(String author, Pageable pageable);

    Page<Resource> findAllByCategory(ECategory category, Pageable pageable);

    Optional<Resource> findByNameEqualsIgnoreCase(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.logo = ?2 WHERE resource.id = ?1")
    void updateLogoById(int id, byte[] logo);

    @Query("SELECT logo FROM Resource WHERE id = ?1")
    byte[] findResourceLogo(int id);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.logo = ?2, resource.name = ?3, resource.slug = ?4, resource.blurb = ?5, resource.description = ?6, resource.source = ?7 WHERE resource.id = ?1")
    void updateResource(int id, byte[] logo, String name, String slug, String blurb, String description, String source);

    @Modifying
    @Transactional
    @Query("UPDATE Resource resource SET resource.logo = ?2, resource.name = ?3, resource.slug = ?4, resource.blurb = ?5, resource.description = ?6, resource.source = ?7 WHERE resource.slug = ?1")
    void updateResourceBySlug(String slug, byte[] logo, String name, String newSlug, String blurb, String description, String source);

    Optional<Resource> findBySlugEqualsIgnoreCase(String slug);

    boolean existsBySlugEqualsIgnoreCase(String slug);
}