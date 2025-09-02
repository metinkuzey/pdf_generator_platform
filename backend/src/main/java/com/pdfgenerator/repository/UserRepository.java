package com.pdfgenerator.repository;

import com.pdfgenerator.entity.User;
import com.pdfgenerator.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username and active status
     */
    Optional<User> findByUsernameAndActiveTrue(String username);

    /**
     * Find user by email and active status
     */
    Optional<User> findByEmailAndActiveTrue(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists for different user (for updates)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :userId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("userId") String userId);

    /**
     * Check if email exists for different user (for updates)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") String userId);

    /**
     * Find all active users
     */
    List<User> findByActiveTrue();

    /**
     * Find active users with pagination
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Find users by role
     */
    List<User> findByRoleAndActiveTrue(UserRole role);

    /**
     * Find users by role with pagination
     */
    Page<User> findByRoleAndActiveTrue(UserRole role, Pageable pageable);

    /**
     * Search users by name or username
     */
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "u.active = true")
    List<User> searchByNameOrUsername(@Param("searchTerm") String searchTerm);

    /**
     * Search users by name or username with pagination
     */
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "u.active = true")
    Page<User> searchByNameOrUsername(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find users created within date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.active = true")
    List<User> findByCreatedAtBetweenAndActiveTrue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Count users by role
     */
    long countByRoleAndActiveTrue(UserRole role);

    /**
     * Count active users
     */
    long countByActiveTrue();

    /**
     * Find users by ID and active status
     */
    Optional<User> findByIdAndActiveTrue(String id);

    /**
     * Soft delete user (set active = false)
     */
    @Query("UPDATE User u SET u.active = false WHERE u.id = :id")
    void softDeleteById(@Param("id") String id);

    /**
     * Search users by multiple criteria
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:searchTerm IS NULL OR " +
           " LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           " LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           " LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "u.active = true")
    Page<User> searchUsers(
            @Param("role") UserRole role,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}