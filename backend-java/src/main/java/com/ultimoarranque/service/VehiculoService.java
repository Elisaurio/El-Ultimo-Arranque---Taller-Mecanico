package com.ultimoarranque.service;
import com.ultimoarranque.model.Vehiculo;
import com.ultimoarranque.repository.VehiculoRepository;
import com.ultimoarranque.repository.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VehiculoService {
    private final VehiculoRepository repo;
    private final ClienteRepository clientes;
    public VehiculoService(VehiculoRepository repo, ClienteRepository clientes) {
        this.repo=repo;
        this.clientes=clientes;
    }
    @Transactional(readOnly=true)
    public List<Vehiculo> listar() {return repo.listar();}
    @Transactional(readOnly=true)
    public Vehiculo obtener(long id) {return repo.buscar(id).orElseThrow(()->new IllegalArgumentException("Vehiculo no encontrado"));}
    @Transactional
    public void guardar(Vehiculo obj) {
        if (obj.getClienteId() == null) throw new IllegalArgumentException("Selecciona un cliente");
        if (clientes.buscarPorId(obj.getClienteId()).isEmpty())
            throw new IllegalArgumentException("El cliente seleccionado no existe");
        if (obj.getPlaca() == null || obj.getPlaca().isBlank())
            throw new IllegalArgumentException("La placa es obligatoria");
        obj.setPlaca(obj.getPlaca().trim().toUpperCase(java.util.Locale.ROOT));
        if(repo.duplicado(obj.getPlaca(),obj.getId())) throw new IllegalArgumentException("La placa ya está registrada");
        try {
            if(obj.getId()==null) repo.crear(obj);
            else if(repo.actualizar(obj)==0) throw new IllegalArgumentException("Vehiculo no encontrado");
        } catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("El registro es duplicado o está relacionado con datos inexistentes",ex);}
    }
    @Transactional
    public void eliminar(long id) {
        obtener(id);
        try {repo.eliminar(id);}
        catch(DataIntegrityViolationException ex) {throw new IllegalArgumentException("No se puede eliminar: tiene órdenes registradas",ex);}
    }
    @Transactional(readOnly=true)
    public List<Vehiculo> porCliente(long id) {return repo.porCliente(id);}
    @Transactional(readOnly=true)
    public List<java.util.Map<String,Object>> historial(long id) {obtener(id);return repo.historial(id);}
}
