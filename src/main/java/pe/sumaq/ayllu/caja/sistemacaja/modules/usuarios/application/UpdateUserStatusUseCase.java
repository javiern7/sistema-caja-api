package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class UpdateUserStatusUseCase {

    private final JpaUserRepository jpaUserRepository;

    public UpdateUserStatusUseCase(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    public UserEntity execute(Long userId, boolean active) {
        UserEntity userEntity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario solicitado."
                ));

        userEntity.setActive(active);
        return jpaUserRepository.save(userEntity);
    }
}
