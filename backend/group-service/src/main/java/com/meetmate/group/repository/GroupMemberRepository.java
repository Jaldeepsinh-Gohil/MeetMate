package com.meetmate.group.repository;

import com.meetmate.group.entity.Group;
import com.meetmate.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    boolean existsByGroupAndUserId(Group group, UUID userId);

    Optional<GroupMember> findByGroupAndUserId(Group group, UUID userId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<GroupMember> findAllByGroupId(@Param("groupId") UUID groupId);
}

