package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto.UpdateUserRequest;

@Service
public class UpdateUserUseCase {

    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserUseCase(
            JpaUserRepository jpaUserRepository,
            JpaRoleRepository jpaRoleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.jpaUserRepository = jpaUserRepository;
        this.jpaRoleRepository = jpaRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity execute(Long userId, UpdateUserRequest request) {
        UserEntity userEntity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario solicitado."
                ));

        if (jpaUserRepository.existsByUsernameAndIdNot(request.username(), userId)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con el username indicado."
            );
        }

        RoleEntity roleEntity = jpaRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ROL_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el rol solicitado."
                ));

        userEntity.setUsername(request.username());
        userEntity.setRole(roleEntity);
        userEntity.setActive(request.active());
        if (request.password() != null && !request.password().isBlank()) {
            userEntity.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return jpaUserRepository.save(userEntity);
    }
}
