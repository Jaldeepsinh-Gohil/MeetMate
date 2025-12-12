package com.meetmate.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddMemberRequest {

    @NotBlank
    private String userId;

    @Size(max = 50)
    private String nickname;
}

