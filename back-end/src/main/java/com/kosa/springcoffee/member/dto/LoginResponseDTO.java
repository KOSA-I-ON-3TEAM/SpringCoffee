package com.kosa.springcoffee.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {
    private String email;
    private String token;
}
