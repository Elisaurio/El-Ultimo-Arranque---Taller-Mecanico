package com.ultimoarranque.service;

import com.ultimoarranque.dao.ClienteDAO;
import com.ultimoarranque.model.Cliente;
import com.ultimoarranque.util.ApiException;
import java.sql.SQLException;
import java.util.List;

public class ClienteService {
    private final ClienteDAO dao = new ClienteDAO();

    public List<Cliente> listar() throws SQLException {
        return dao.listar();
    }

    public Cliente buscar(long id) throws SQLException {
        return dao.buscarPorId(id).orElseThrow(() -> new ApiException(404, "Cliente no encontrado"));
    }

    public Cliente crear(Cliente cliente) throws SQLException {
        validar(cliente);
        if (dao.identificadorExistente(cliente.getIdentificador(), null)) {
            throw new ApiException(409, "Ya existe un cliente con ese identificador");
        }
        return dao.crear(cliente);
    }

    public Cliente actualizar(long id, Cliente cliente) throws SQLException {
        buscar(id);
        validar(cliente);
        if (dao.identificadorExistente(cliente.getIdentificador(), id)) {
            throw new ApiException(409, "Ya existe un cliente con ese identificador");
        }
        if (!dao.actualizar(id, cliente)) throw new ApiException(404, "Cliente no encontrado");
        return buscar(id);
    }

    public void eliminar(long id) throws SQLException {
        buscar(id);
        if (dao.tieneVehiculos(id)) {
            throw new ApiException(409, "No se puede eliminar: el cliente tiene vehículos asociados");
        }
        if (!dao.eliminar(id)) throw new ApiException(404, "Cliente no encontrado");
    }

    private void validar(Cliente cliente) {
        if (cliente == null) throw new ApiException(400, "Debe enviar los datos del cliente");
        cliente.setIdentificador(requerido(cliente.getIdentificador(), "Identificador", 30));
        cliente.setNombre(requerido(cliente.getNombre(), "Nombre", 120));
        cliente.setTelefono(opcional(cliente.getTelefono(), "Teléfono", 25));
        cliente.setCorreo(opcional(cliente.getCorreo(), "Correo", 120));
        cliente.setDireccion(opcional(cliente.getDireccion(), "Dirección", 255));
        if (cliente.getCorreo() != null && !cliente.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ApiException(400, "El correo electrónico no es válido");
        }
    }

    private String requerido(String valor, String nombreCampo, int maximo) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.isEmpty()) throw new ApiException(400, nombreCampo + " es obligatorio");
        if (texto.length() > maximo) throw new ApiException(400, nombreCampo + " supera " + maximo + " caracteres");
        return texto;
    }

    private String opcional(String valor, String nombreCampo, int maximo) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.length() > maximo) throw new ApiException(400, nombreCampo + " supera " + maximo + " caracteres");
        return texto.isEmpty() ? null : texto;
    }
}
