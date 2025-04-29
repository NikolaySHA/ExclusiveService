package com.NikolaySHA.ExclusiveService.model.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {
    private String token;
    private String password;
    private String confirmPassword;
}
