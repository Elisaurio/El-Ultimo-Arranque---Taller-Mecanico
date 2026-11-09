package com.ultimoarranque.service;
import com.ultimoarranque.model.Servicio;
import com.ultimoarranque.repository.ServicioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ServicioService {
    private final ServicioRepository repo;
    public ServicioService(ServicioRepository repo) {this.repo=repo;}
    @Transactional(readOnly=true)
    public List<Servicio> listar() {return repo.listar();}
    @Transactional(readOnly=true)
    public Servicio obtener(long id) {return repo.buscar(id).orElseThrow(()->new IllegalArgumentException("Servicio no encontrado"));}
    @Transactional
    public void guardar(Servicio obj) {
        obj.setCodigo(obj.getCodigo().trim().toUpperCase(java.util.Locale.ROOT));
        if(repo.duplicado(obj.getCodigo(),obj.getId())) throw new IllegalArgumentException("El codigo ya está registrado");
        try {
            if(obj.getId()==null) repo.crear(obj);
            else if(repo.actualizar(obj)==0) throw new IllegalArgumentException("Servicio no encontrado");
        } catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("El registro es duplicado o está relacionado con datos inexistentes",ex);}
    }
    @Transactional
    public void eliminar(long id) {
        obtener(id);
        try {repo.eliminar(id);}
        catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("No se puede eliminar: tiene órdenes asociadas",ex);}
    }
}
