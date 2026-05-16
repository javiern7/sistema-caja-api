package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application.CreateUserUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application.ListUsersUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application.UpdateUserStatusUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application.UpdateUserUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application.UserMapper;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto.CreateUserRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto.UpdateUserRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto.UpdateUserStatusRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto.UserResponse;

@RestController
@RequestMapping("/api/v1/usuarios")
@PreAuthorize("hasAuthority('usuario.gestionar')")
public class UsersController {

    private final ListUsersUseCase listUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final UserMapper userMapper;
    private final ApiResponseFactory responseFactory;

    public UsersController(
            ListUsersUseCase listUsersUseCase,
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            UpdateUserStatusUseCase updateUserStatusUseCase,
            UserMapper userMapper,
            ApiResponseFactory responseFactory
    ) {
        this.listUsersUseCase = listUsersUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updateUserStatusUseCase = updateUserStatusUseCase;
        this.userMapper = userMapper;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> listUsers() {
        return responseFactory.success(
                "Usuarios obtenidos correctamente.",
                listUsersUseCase.execute().stream().map(userMapper::toResponse).toList()
        );
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return responseFactory.success(
                "Usuario registrado correctamente.",
                userMapper.toResponse(createUserUseCase.execute(request))
        );
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return responseFactory.success(
                "Usuario actualizado correctamente.",
                userMapper.toResponse(updateUserUseCase.execute(userId, request))
        );
    }

    @PatchMapping("/{userId}/estado")
    public ApiResponse<UserResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return responseFactory.success(
                "Estado de usuario actualizado correctamente.",
                userMapper.toResponse(updateUserStatusUseCase.execute(userId, request.active()))
        );
    }
}
