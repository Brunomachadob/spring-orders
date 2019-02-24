package com.brunomb.spo.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank @NonNull
    private String username;

    @NotBlank @NonNull
    private String password;
}
