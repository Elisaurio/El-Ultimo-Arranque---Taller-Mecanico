package com.ultimoarranque.service;

import com.ultimoarranque.model.Cliente;
import com.ultimoarranque.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {
    @Test
    void rechazaIdentificadorDuplicado() {
        ClienteRepository repo = mock(ClienteRepository.class);
        VehiculoService vehiculos = mock(VehiculoService.class);
        ClienteService service = new ClienteService(repo, vehiculos);
        Cliente cliente = new Cliente();
        cliente.setIdentificador("CLI-001");
        cliente.setNombre("Prueba");
        when(repo.existeIdentificador("CLI-001", null)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.guardar(cliente));
        verify(repo, never()).crear(any());
    }
}
