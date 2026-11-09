package com.ultimoarranque.service;
import com.ultimoarranque.model.Mecanico;
import com.ultimoarranque.repository.MecanicoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MecanicoService {
    private final MecanicoRepository repo;
    public MecanicoService(MecanicoRepository repo) {this.repo=repo;}
    @Transactional(readOnly=true)
    public List<Mecanico> listar() {return repo.listar();}
    @Transactional(readOnly=true)
    public Mecanico obtener(long id) {return repo.buscar(id).orElseThrow(()->new IllegalArgumentException("Mecanico no encontrado"));}
    @Transactional
    public void guardar(Mecanico obj) {
        obj.setIdentificador(obj.getIdentificador().trim().toUpperCase(java.util.Locale.ROOT));
        if(repo.duplicado(obj.getIdentificador(),obj.getId())) throw new IllegalArgumentException("El identificador ya está registrado");
        try {
            if(obj.getId()==null) repo.crear(obj);
            else if(repo.actualizar(obj)==0) throw new IllegalArgumentException("Mecanico no encontrado");
        } catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("El registro es duplicado o está relacionado con datos inexistentes",ex);}
    }
    @Transactional
    public void eliminar(long id) {
        obtener(id);
        try {repo.eliminar(id);}
        catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("No se puede eliminar: tiene órdenes asociadas",ex);}
    }
}
