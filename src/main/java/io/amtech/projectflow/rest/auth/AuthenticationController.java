package io.amtech.projectflow.rest.auth;

import io.amtech.projectflow.service.token.AuthDto;
import io.amtech.projectflow.service.token.TokenDto;
import io.amtech.projectflow.service.token.TokenRefreshDto;
import io.amtech.projectflow.service.token.TokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final TokenService tokenService;

    @ApiOperation("Генерация access и refresh токенов на основе пользовательских данных")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "400", description = "Некорректное заполнение полей формы")
    @PostMapping("/login")
    public TokenDto login(@RequestBody @Valid AuthDto authDto) {
        return tokenService.generate(authDto);
    }

    @ApiOperation("Обновление пары access и refresh токенов")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "400", description = "Несуществующий refresh токен")
    @PostMapping("/refreshToken")
    public TokenDto refreshToken(@RequestBody @Valid TokenRefreshDto tokenRefreshDto) {
        return tokenService.refresh(tokenRefreshDto);
    }
}
