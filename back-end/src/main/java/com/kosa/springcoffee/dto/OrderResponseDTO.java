package com.kosa.springcoffee.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private Long orderNo;
    private int total;
}
