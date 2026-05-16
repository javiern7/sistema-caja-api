package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class ListUsersUseCase {

    private final JpaUserRepository jpaUserRepository;

    public ListUsersUseCase(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    public List<UserEntity> execute() {
        return jpaUserRepository.findAll();
    }
}
