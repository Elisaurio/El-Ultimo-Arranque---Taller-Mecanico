# Pruebas de aceptación y criterios de entrega

| Prueba | Resultado esperado |
|---|---|
| Cliente con 0 vehículos | Muestra siempre «Agregar vehículo». |
| Cliente con varios vehículos | Cada placa aparece dentro del mismo cliente. |
| Placa duplicada | HTTP 409 y mensaje de duplicado. |
| Eliminar cliente con vehículos | Rechazo HTTP 409. |
| Eliminar vehículo con órdenes | Rechazo HTTP 409 por clave foránea. |
| Repuesto con existencia 5, asignar 2 | Existencia pasa a 3. |
| Repuesto en orden EN_PROCESO, retirarlo | Existencia vuelve a 5. |
| Orden EN_PROCESO, cancelarla | Devuelve todas las existencias. |
| Finalizar sin servicios | Se rechaza con HTTP 409. |
| Finalizar con servicio | Estado FINALIZADA, total guardado, comprobante PDF. |
| Intentar editar orden FINALIZADA | Se rechaza. |
| Reporte de inventario | Solo registros con existencia <= 5. |
| Reporte de ingresos | Solo órdenes FINALIZADAS con fecha de finalización dentro del rango. |
| Reiniciar app | Datos siguen guardados en MySQL. |

## Entrega formal pendiente de comprobar

- 5 commits en fechas distintas y participación de todos los integrantes.
- SQL de datos de prueba y capturas de funcionamiento.
- Diagramas UML y ER: se entregan como `.puml` para exportar a PNG.
- Herencia, polimorfismo y composición deben demostrarse de forma funcional y clara durante la defensa; el diseño actual prioriza encapsulamiento, colecciones, validación y transacciones. Revisar y completar antes de entregar.
- Código final sin comentarios, según instrucciones de la rúbrica.
