# Módulo de clientes: explicación para Programación II

## Objetivo
Registrar, listar, consultar, modificar y eliminar clientes mediante Flutter, Java y MySQL. El identificador es único y no se permite eliminar un cliente con vehículos asociados.

## Flujo
1. Flutter construye un objeto `Cliente` con los datos del formulario.
2. `ApiService` envía una solicitud HTTP al backend Java.
3. `ClientesController` interpreta la operación (GET, POST, PUT o DELETE).
4. `ClienteService` verifica las reglas de negocio.
5. `ClienteDAO` realiza consultas SQL usando `PreparedStatement`.
6. MySQL conserva los datos y Java devuelve JSON a Flutter.

## POO y MVC
- **Modelo:** `Cliente` mantiene sus atributos privados y ofrece getters/setters (encapsulamiento).
- **Controlador:** `ClientesController` recibe las peticiones HTTP.
- **Lógica de negocio:** `ClienteService` valida campos e identificadores únicos.
- **Acceso a datos:** `ClienteDAO` trabaja con conexiones JDBC y `PreparedStatement`.
- **Vista:** `ClientesScreen` y `ClienteFormDialog` muestran y editan información.
- **Colecciones:** `List<Cliente>` en Java y Flutter permite presentar el listado.
- **Excepciones:** `ApiException` y `SQLException` se convierten en mensajes comprensibles.

Esta primera etapa muestra encapsulamiento, colecciones y excepciones. La herencia, el polimorfismo y la composición se demostrarán de manera funcional en los demás módulos, sin inventar jerarquías artificiales.

## API REST
| Método | Ruta | Acción |
| --- | --- | --- |
| GET | `/api/health` | Comprobar backend Java |
| GET | `/api/clientes` | Listar |
| GET | `/api/clientes/{id}` | Consultar uno |
| POST | `/api/clientes` | Crear |
| PUT | `/api/clientes/{id}` | Modificar |
| DELETE | `/api/clientes/{id}` | Eliminar |

Ejemplo JSON para POST:

```json
{
  "identificador": "CLI-004",
  "nombre": "Luis Pérez",
  "telefono": "5555-0404",
  "correo": "luis.ejemplo@correo.com",
  "direccion": "Villa Nueva, Guatemala"
}
```

## Pruebas de aceptación
- [ ] Crear un cliente y verlo en el listado de Flutter.
- [ ] Cerrar y abrir la aplicación; comprobar que el registro permanece.
- [ ] Intentar ingresar otro cliente con el mismo identificador; debe rechazarlo.
- [ ] Modificar nombre, teléfono o dirección; comprobar los cambios tras actualizar.
- [ ] Buscar por nombre, identificador o teléfono.
- [ ] Eliminar un cliente sin vehículos y verificar que desaparece.
- [ ] Intentar eliminar un cliente con vehículos y comprobar que la operación se rechaza.
- [ ] Abrir Chrome y el emulador Android y verificar que muestran los mismos datos.

Para probar la restricción de eliminación, puedes ejecutar temporalmente en MySQL:

```sql
INSERT INTO vehiculos (cliente_id, placa, marca, modelo, anio)
SELECT id, 'TEST001', 'Toyota', 'Corolla', 2014
FROM clientes WHERE identificador = 'CLI-001';
```

Después intenta eliminar el cliente `CLI-001`. Cuando termines la prueba, puedes retirar ese vehículo:

```sql
DELETE FROM vehiculos WHERE placa = 'TEST001';
```

No uses estos comandos si ya existe esa placa en tu base de datos. El módulo de vehículos se desarrollará después.
