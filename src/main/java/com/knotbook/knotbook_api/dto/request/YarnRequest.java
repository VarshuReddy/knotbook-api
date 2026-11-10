package com.knotbook.knotbook_api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YarnRequest {

    @Size(max = 100)
    private String color;

    @Size(max = 50)
    private String weight;

    @Size(max = 100)
    private String type;
}