package com.meetmate.group.repository;

import com.meetmate.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query("""
        SELECT DISTINCT g FROM Group g
        LEFT JOIN GroupMember gm ON gm.group = g
        WHERE g.ownerId = :userId OR gm.userId = :userId
        """)
    List<Group> findAllForUser(@Param("userId") UUID userId);
}

