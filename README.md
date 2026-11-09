# El Último Arranque — Etapa 2

Aplicación WEB local con MVC Java + Spring Boot 3.5.16 + Thymeleaf + JDBC MySQL. Jetty integrado; se excluyó Tomcat.

## Precaución
Antes de copiar esta etapa, respalda la carpeta `backend-java` y haz una copia de tu BD si contiene datos. Los scripts usan `CREATE TABLE IF NOT EXISTS` y **no borran ni reemplazan** datos. Si ya tienes tablas con nombres iguales pero esquemas diferentes, el script NO las modifica y deberás revisar sus columnas.

## Pasos
1. Copia las carpetas del ZIP a `D:\Proyectos\tallerMecanico`, fusionando las carpetas. No copies encima de cambios propios sin revisarlos.
2. Inicia MySQL. Ejecuta `database/01_crear_base.sql`, luego `database/02_crear_tablas.sql` y opcionalmente `database/03_datos_prueba.sql`, en ese orden, con HeidiSQL o Workbench.
3. En la MISMA terminal PowerShell:
   ```powershell
   $env:DB_USER = 'root'
   $env:DB_PASSWORD = 'TU_CONTRASENA'
   cd D:\Proyectos\tallerMecanico\backend-java
   mvn clean test
   mvn spring-boot:run
   ```
4. Abre `http://localhost:8080/`, `http://localhost:8080/clientes` y `http://localhost:8080/api/estado`.
5. Prueba: crear cliente, editar cliente, intentar identificador duplicado y borrar cliente sin vehículos. La prohibición de borrar clientes con vehículos existe en Java y mediante FK SQL; se comprobará al implementar Vehículos.

## La arquitectura
- C: `controller/` (`InicioController`, `ClienteController`)
- M: `model/`, `service/`, `repository/` (SQL con PreparedStatement)
- V: `src/main/resources/view/` (Thymeleaf HTML)
- Estilos: `src/main/resources/static/css/estilos.css`

## Alcance de esta etapa
Ocho tablas listas para siguientes módulos. Solo **Clientes** tiene CRUD web funcional. El dashboard muestra conteo real de clientes; los demás datos no se simulan. Android, otros CRUD, órdenes, transacciones de inventario y PDF quedan pendientes.

## Problemas frecuentes
- Si el dashboard falla con 'Table clientes doesn't exist', ejecuta `02_crear_tablas.sql` en la BD `taller_mecanico`.
- Si `/api/estado` dice `ERROR_CONEXION`, revisa puerto 3306, MySQL activo y credenciales `DB_USER` y `DB_PASSWORD` de esa terminal.
- Si puerto 8080 está ocupado, detén el backend anterior antes de iniciar esta etapa.
- Si `mvn` no se reconoce, instala Maven o configura PATH.
- No guardes credenciales en Git. El SQL de prueba usa correos `example.com`, no datos reales.

## Git
Haz un commit local cuando la etapa pase las pruebas: `git add .` y `git commit -m "Etapa 2: esquema MySQL, clientes MVC y rediseño UI"`. El documento solicita cinco commits en **fechas distintas**: no fabriques fechas anteriores.
