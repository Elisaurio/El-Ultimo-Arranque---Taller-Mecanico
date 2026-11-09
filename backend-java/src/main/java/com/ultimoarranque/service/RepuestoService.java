package com.ultimoarranque.service;
import com.ultimoarranque.model.Repuesto;
import com.ultimoarranque.repository.RepuestoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RepuestoService {
    private final RepuestoRepository repo;
    public RepuestoService(RepuestoRepository repo) {this.repo=repo;}
    @Transactional(readOnly=true)
    public List<Repuesto> listar() {return repo.listar();}
    @Transactional(readOnly=true)
    public Repuesto obtener(long id) {return repo.buscar(id).orElseThrow(()->new IllegalArgumentException("Repuesto no encontrado"));}
    @Transactional
    public void guardar(Repuesto obj) {
        obj.setCodigo(obj.getCodigo().trim().toUpperCase(java.util.Locale.ROOT));
        if(repo.duplicado(obj.getCodigo(),obj.getId())) throw new IllegalArgumentException("El codigo ya está registrado");
        if(obj.getId()!=null) obj.setExistencia(obtener(obj.getId()).getExistencia());
        try {
            if(obj.getId()==null) repo.crear(obj);
            else if(repo.actualizar(obj)==0) throw new IllegalArgumentException("Repuesto no encontrado");
        } catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("El registro es duplicado o está relacionado con datos inexistentes",ex);}
    }
    @Transactional
    public void eliminar(long id) {
        obtener(id);
        try {repo.eliminar(id);}
        catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("No se puede eliminar: tiene órdenes asociadas",ex);}
    }
    @Transactional
    public void abastecer(long id,int cantidad) {
        if(cantidad<=0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        Repuesto actual=obtener(id);
        if((long)actual.getExistencia()+cantidad>Integer.MAX_VALUE) throw new IllegalArgumentException("Existencia fuera del rango permitido");
        if(repo.abastecer(id,cantidad)==0) throw new IllegalArgumentException("No se pudo abastecer: verifica la existencia y el límite permitido");
    }
    @Transactional(readOnly=true)
    public List<Repuesto> inventarioBajo() {return repo.inventarioBajo();}
}
