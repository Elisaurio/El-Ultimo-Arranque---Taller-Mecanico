# Manual de usuario — El Último Arranque

1. Inicia Java y MySQL en la computadora local y abre la aplicación Flutter desde Android o Chrome.
2. En **Clientes**, registra un cliente, expande su tarjeta y selecciona **Agregar vehículo**; puedes asociar más de uno.
3. En **Mecánicos** y **Servicios**, registra el personal y los trabajos disponibles; en **Inventario**, registra y abastece repuestos.
4. En **Órdenes**, selecciona un vehículo y mecánico, describe el problema y crea la orden (PENDIENTE). Pulsa **Iniciar reparación** para pasar a EN_PROCESO.
5. Añade servicios y repuestos. El inventario se descuenta al asignar; se reintegra al retirar un repuesto o al cancelar. Finaliza solo cuando exista al menos un servicio y abre el comprobante PDF.
6. En **Reportes**, exporta los PDF obligatorios: por estado, historial por placa, bajo inventario, por mecánico e ingresos por fechas.

> Nota: las órdenes finalizadas o canceladas quedan bloqueadas para modificaciones.
