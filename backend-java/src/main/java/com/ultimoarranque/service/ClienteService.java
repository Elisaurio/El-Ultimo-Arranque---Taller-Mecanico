package com.ultimoarranque.service;

import com.ultimoarranque.model.Cliente;
import com.ultimoarranque.repository.ClienteRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {
    private final ClienteRepository repo;
    public ClienteService(ClienteRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<Cliente> listar() { return repo.listar(); }

    @Transactional(readOnly = true)
    public int contar() { return repo.contar(); }

    @Transactional(readOnly = true)
    public Cliente obtener(long id) {
        return repo.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }

    @Transactional
    public void guardar(Cliente c) {
        c.setIdentificador(c.getIdentificador().trim());
        c.setNombre(c.getNombre().trim());
        if (repo.existeIdentificador(c.getIdentificador(), c.getId()))
            throw new IllegalArgumentException("Ya existe un cliente con ese identificador");
        try {
            if (c.getId() == null) repo.crear(c);
            else if (repo.actualizar(c) == 0) throw new IllegalArgumentException("Cliente no encontrado");
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("El identificador ya está registrado", ex);
        }
    }

    @Transactional
    public void eliminar(long id) {
        if (repo.buscarPorId(id).isEmpty()) throw new IllegalArgumentException("Cliente no encontrado");
        if (repo.tieneVehiculos(id)) throw new IllegalArgumentException("No puedes eliminar un cliente con vehículos asociados");
        try { repo.eliminar(id); }
        catch (DataIntegrityViolationException ex) { throw new IllegalArgumentException("No se puede eliminar: existen registros asociados", ex); }
    }
}
