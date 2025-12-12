package com.meetmate.group.repository;

import com.meetmate.group.entity.Group;
import com.meetmate.group.entity.MemberPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberPreferenceRepository extends JpaRepository<MemberPreference, UUID> {

    Optional<MemberPreference> findByGroupAndUserId(Group group, UUID userId);

    @Query("SELECT mp FROM MemberPreference mp WHERE mp.group.id = :groupId")
    List<MemberPreference> findAllByGroupId(@Param("groupId") UUID groupId);
}

