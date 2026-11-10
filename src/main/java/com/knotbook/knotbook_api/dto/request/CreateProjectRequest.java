package com.knotbook.knotbook_api.dto.request;


import com.knotbook.knotbook_api.enity.ProjectStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateProjectRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    private String description;

    @Size(max = 50)
    private String category;

    private ProjectStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    @Valid
    private List<YarnRequest> yarns;

    @Valid
    private List<HookRequest> hooks;
}