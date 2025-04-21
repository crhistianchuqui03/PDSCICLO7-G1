package pe.edu.upeu.sysalmacen.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ICrudGenericoRepository<E, I> extends JpaRepository<E, I> {
}
