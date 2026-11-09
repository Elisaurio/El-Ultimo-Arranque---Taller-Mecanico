package com.ultimoarranque.service;

import com.ultimoarranque.model.Cliente;
import com.ultimoarranque.model.Vehiculo;
import com.ultimoarranque.repository.ClienteRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {
    private final ClienteRepository repo;
    private final VehiculoService vehiculos;

    public ClienteService(ClienteRepository repo, VehiculoService vehiculos) {
        this.repo=repo;
        this.vehiculos=vehiculos;
    }

    @Transactional(readOnly=true)
    public List<Cliente> listar() { return repo.listar(); }

    @Transactional(readOnly=true)
    public int contar() { return repo.contar(); }

    @Transactional(readOnly=true)
    public Cliente obtener(long id) {
        return repo.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    private void normalizar(Cliente c) {
        if (c.getIdentificador()==null || c.getIdentificador().isBlank() || c.getNombre()==null || c.getNombre().isBlank())
            throw new IllegalArgumentException("Identificador y nombre del cliente son obligatorios");
        c.setIdentificador(c.getIdentificador().trim());
        c.setNombre(c.getNombre().trim());
        if (repo.existeIdentificador(c.getIdentificador(), c.getId()))
            throw new IllegalArgumentException("Ya existe un cliente con ese identificador");
    }

    @Transactional
    public void guardar(Cliente c) {
        normalizar(c);
        try {
            if (c.getId()==null) c.setId(repo.crear(c));
            else if (repo.actualizar(c)==0) throw new IllegalArgumentException("Cliente no encontrado");
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("El identificador ya está registrado",ex);
        }
    }

    @Transactional
    public void guardarConVehiculo(Cliente c, Vehiculo v) {
        if (c.getId()!=null) throw new IllegalArgumentException("Para un cliente existente agrega vehículos desde su detalle");
        guardar(c);
        v.setClienteId(c.getId());
        try {
            vehiculos.guardar(v);
        } catch (RuntimeException ex) {
            c.setId(null);
            throw ex;
        }
    }

    @Transactional
    public void eliminar(long id) {
        if (repo.buscarPorId(id).isEmpty()) throw new IllegalArgumentException("Cliente no encontrado");
        if (repo.tieneVehiculos(id)) throw new IllegalArgumentException("No puedes eliminar un cliente con vehículos asociados");
        try { repo.eliminar(id); }
        catch (DataIntegrityViolationException ex) { throw new IllegalArgumentException("No se puede eliminar: existen registros asociados",ex); }
    }
}
