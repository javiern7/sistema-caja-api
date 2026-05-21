package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class ListUsersUseCase {

    private final JpaUserRepository jpaUserRepository;

    public ListUsersUseCase(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    public Page<UserEntity> execute(Pageable pageable) {
        return jpaUserRepository.findAll(pageable);
    }
}
