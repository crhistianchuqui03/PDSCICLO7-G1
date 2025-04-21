package pe.edu.upeu.sysalmacen.servicio;

import pe.edu.upeu.sysalmacen.excepciones.CustomResponse;
import java.util.List;

public interface ICrudGenericoService<E, I> {
    E save(E e);
    E update(I id, E e);
    List<E> findAll();
    E findById(I id);
    CustomResponse delete(I id);
}
