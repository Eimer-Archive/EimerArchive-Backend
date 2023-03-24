package org.eimerarchive.archive.repositories;

import org.eimerarchive.archive.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);

    boolean existsByUsernameEqualsIgnoreCase(String username);

    boolean existsByEmailEqualsIgnoreCase(String email);

    Optional<Account> findByUsernameEqualsIgnoreCase(String username);

    Optional<Account> findByUsernameEquals(String username);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.username = ?2 WHERE account.id = ?1")
//    void setUsernameById(long id, String username);
//
////    @Modifying
////    @Transactional
////    @Query("UPDATE Account account SET account.email = ?2 WHERE account.id = ?1")
////    void setEmailById(long id, String email);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.password = ?2 WHERE account.id = ?1")
//    void setPasswordById(long id, String password);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.role = ?2 WHERE account.id = ?1")
//    void setRoleById(long id, String role);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Account account SET account.profilePicture = ?2 WHERE account.id = ?1")
//    void updateProfilePictureById(long id, byte[] profilePicture);
//
//    @Query("SELECT profilePicture FROM Account WHERE id = ?1")
//    byte[] findAccountProfilePicture(long id);
}