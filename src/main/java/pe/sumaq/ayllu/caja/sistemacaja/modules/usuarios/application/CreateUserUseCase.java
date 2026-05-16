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
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto.CreateUserRequest;

@Service
public class CreateUserUseCase {

    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(
            JpaUserRepository jpaUserRepository,
            JpaRoleRepository jpaRoleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.jpaUserRepository = jpaUserRepository;
        this.jpaRoleRepository = jpaRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity execute(CreateUserRequest request) {
        RoleEntity roleEntity = jpaRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ROL_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el rol solicitado."
                ));

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.username());
        userEntity.setPasswordHash(passwordEncoder.encode(request.password()));
        userEntity.setActive(request.active());
        userEntity.setRole(roleEntity);
        return jpaUserRepository.save(userEntity);
    }
}
