package data.repository.jpa;

import data.entity.UserRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegisterEntityRepository extends JpaRepository<UserRegisterEntity, Long> {
    UserRegisterEntity findByEmail(String email);
}
