package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto.UserResponse;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity userEntity) {
        return new UserResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.isActive(),
                userEntity.getRole().getId(),
                userEntity.getRole().getName()
        );
    }
}
