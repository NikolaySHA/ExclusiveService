package com.NikolaySHA.ExclusiveService.model.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordDTO {
    private @NotBlank(
            message = "Имейлът е задължителен!"
    ) @Email(
            message = "Моля, въведете валиден имейл адрес!"
    ) String email;
}