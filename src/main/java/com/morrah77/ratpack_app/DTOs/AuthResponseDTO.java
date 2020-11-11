package com.morrah77.ratpack_app.DTOs;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AuthResponseDTO {
    public String token;

    public AuthResponseDTO(){}
}
