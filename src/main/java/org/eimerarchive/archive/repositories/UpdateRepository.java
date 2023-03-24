package org.eimerarchive.archive.repositories;

import org.eimerarchive.archive.model.File;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UpdateRepository extends JpaRepository<File, Integer> {

    List<File> findAllByResourceId(int resourceId, Sort sort);

    List<File> findAllByResourceId(int resourceId, String status, Sort sort);

    @Modifying
    @Transactional
    @Query("UPDATE File updates SET updates.downloads = updates.downloads + 1 WHERE updates.id = ?1")
    void addDownload(int id);

    @Query("SELECT SUM(updates.downloads) FROM File updates WHERE updates.resource.id = ?1")
    Optional<Integer> getTotalDownloads(int id);

    @Modifying
    @Transactional
    @Query("UPDATE File updates SET updates.name = ?2, updates.description = ?3, updates.version = ?4 WHERE updates.id = ?1")
    void setInfo(int id, String name, String description, String version);
}