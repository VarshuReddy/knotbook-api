package com.knotbook.knotbook_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveProjectNoteRequest {

    @NotNull(message = "Content cannot be null")
    private String content;
}