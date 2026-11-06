
# El Último Arranque 🚗🔧

**Sistema de Gestión de Órdenes de Trabajo para Taller Mecánico**  
**Proyecto final — Programación II**  
**Modalidad:** Aplicación Flutter para Android y navegador web, conectada a un backend Java y una base de datos MySQL, ejecutada localmente.

## 1. Descripción del proyecto

El Último Arranque es un sistema desarrollado para facilitar la administración de las actividades de un taller mecánico.

La aplicación permite gestionar clientes, vehículos, mecánicos, servicios, repuestos y órdenes de trabajo, manteniendo la información almacenada en una base de datos MySQL.

También permite controlar el inventario durante las reparaciones, calcular costos y generar comprobantes y reportes en formato PDF.

El proyecto tiene como objetivo aplicar los conocimientos adquiridos en Programación II, principalmente:

- Programación orientada a objetos.
- Arquitectura MVC.
- Manejo de colecciones y excepciones.
- Conexión a bases de datos mediante JDBC.
- Consultas SQL con PreparedStatement.
- Transacciones de base de datos.
- Desarrollo de interfaces gráficas responsivas.
- Comunicación entre Flutter y Java mediante HTTP y JSON.

## 2. Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Flutter | Desarrollo de la interfaz gráfica |
| Dart | Programación de la aplicación móvil y web |
| Java 17 | Backend y lógica del sistema |
| MySQL | Almacenamiento de datos |
| JDBC | Conexión entre Java y MySQL |
| Maven | Administración de dependencias Java |
| Jackson | Intercambio de información mediante JSON |
| Apache PDFBox | Generación de comprobantes y reportes PDF |
| Android Studio | Emulador y herramientas Android |
| Visual Studio Code | Desarrollo del proyecto |
| Git y GitHub | Control de versiones |

El sistema utiliza un servidor HTTP integrado en Java, por lo que no requiere Tomcat.

## 3. Funcionalidades principales

### 3.1. Gestión de clientes

El módulo permite:

- Registrar nuevos clientes.
- Consultar clientes existentes.
- Modificar información.
- Eliminar clientes sin vehículos asociados.
- Validar identificadores únicos.
- Buscar clientes por sus datos.

Cada cliente puede tener uno o varios vehículos registrados.

### 3.2. Gestión de vehículos

Los vehículos se administran dentro del apartado de clientes.

Permite:

- Registrar vehículos asociados a clientes.
- Agregar varios vehículos a un mismo propietario.
- Consultar marca, modelo, placa, año y tipo.
- Modificar información del vehículo.
- Validar que la placa no esté duplicada.
- Consultar el historial de reparaciones.

Un vehículo con órdenes registradas no debe eliminarse.

### 3.3. Gestión de mecánicos

Permite registrar y administrar los mecánicos disponibles en el taller.

Cada mecánico puede tener múltiples órdenes de trabajo asignadas.

### 3.4. Gestión de servicios

Permite administrar el catálogo de servicios que ofrece el taller.

Cada servicio cuenta con una descripción y un costo de mano de obra.

Ejemplos:

- Cambio de aceite.
- Revisión de frenos.
- Alineación.
- Balanceo.
- Diagnóstico general.
- Cambio de batería.

### 3.5. Gestión de inventario

Permite registrar y administrar los repuestos disponibles.

Cada repuesto contiene:

- Identificador.
- Nombre.
- Descripción.
- Precio.
- Cantidad disponible.

También permite abastecer inventario, identificar repuestos agotados y consultar productos con cinco unidades o menos.

### 3.6. Órdenes de trabajo

Es el módulo principal del sistema.

Permite crear órdenes de reparación seleccionando un vehículo, un mecánico y describiendo el problema.

Las órdenes utilizan los estados:

1. PENDIENTE.
2. EN PROCESO.
3. FINALIZADA.
4. CANCELADA.

Al iniciar una reparación se pueden agregar servicios y repuestos.

Cuando se asigna un repuesto, el sistema debe verificar su disponibilidad y descontarlo del inventario mediante una transacción.

Si se cancela una orden, los repuestos asignados deben regresar al inventario.

No se permite finalizar una orden sin servicios registrados.

Una vez finalizada, se calcula el costo total y se genera el comprobante correspondiente.

## 4. Arquitectura del sistema

El sistema está organizado mediante una separación de responsabilidades basada en MVC.

La aplicación Flutter funciona como capa de presentación, mientras que el backend Java administra las operaciones y la comunicación con MySQL.

### Estructura de carpetas

```text
tallerMecanico/
│
├── app_taller_mecanico/
│   ├── android/
│   ├── lib/
│   │   ├── main.dart
│   │   ├── models/
│   │   ├── screens/
│   │   ├── services/
│   │   └── widgets/
│   ├── web/
│   └── pubspec.yaml
│
├── backend-java/
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── ultimoarranque/
│                       ├── Main.java
│                       ├── controller/
│                       ├── dao/
│                       ├── model/
│                       ├── service/
│                       ├── report/
│                       └── util/
│
├── database/
├── docs/
└── README.md
```

### Responsabilidades

**Model:** representa las entidades del sistema.

**Controller:** recibe las solicitudes HTTP y coordina las respuestas.

**Service:** contiene las validaciones y reglas de negocio.

**DAO:** realiza las operaciones de acceso a MySQL mediante JDBC.

**Flutter:** contiene las pantallas y formularios que permiten interactuar con el sistema.

### Comunicación

```text
Flutter (Android / Web)
          |
          v
       HTTP / JSON
          |
          v
     Backend Java
          |
          v
      JDBC / SQL
          |
          v
        MySQL
```

La aplicación no se conecta directamente a MySQL desde Flutter. Toda la comunicación pasa por el backend Java.

## 5. Requisitos para ejecutar el proyecto

Se necesita tener instalado:

- Java JDK 17 o superior.
- Apache Maven.
- MySQL.
- Flutter SDK.
- Android Studio.
- Visual Studio Code.
- Google Chrome.

La base de datos utilizada se llama:

`taller_mecanico`

Por defecto, el backend utiliza el puerto 8080 y MySQL utiliza el puerto 3306.

## 6. Configuración de MySQL

Para preparar la base de datos, se utilizan los scripts SQL incluidos en la carpeta `database`.

Orden recomendado cuando se prepara una instalación nueva:

1. `01_clientes_y_vehiculos.sql`
2. `03_modulos_taller.sql`
3. `02_datos_prueba_clientes.sql` (opcional).
4. `04_datos_demostracion_taller.sql` (opcional).

Los scripts permiten crear las tablas necesarias y agregar datos para las demostraciones.

Si la base de datos ya tiene información, los scripts deben revisarse antes de ejecutarlos nuevamente para evitar conflictos.

Para consultar los registros desde HeidiSQL:

```sql
USE taller_mecanico;

SELECT * FROM clientes;
SELECT * FROM vehiculos;
SELECT * FROM mecanicos;
SELECT * FROM servicios;
SELECT * FROM repuestos;
```

## 7. Ejecutar el backend Java

Abrir una terminal PowerShell dentro de la carpeta principal del proyecto.

Ingresar al backend:

```powershell
cd backend-java
```

Configurar las variables de conexión:

```powershell
$env:DB_USER = "root"

$env:DB_URL = "jdbc:mysql://localhost:3306/taller_mecanico?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
```

Ingresar la contraseña local de MySQL:

```powershell
$clave = Read-Host "Contraseña de MySQL" -AsSecureString

$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $clave).Password
```

Compilar el proyecto:

```powershell
mvn clean compile
```

Iniciar el servidor:

```powershell
mvn exec:java
```

El backend debe permanecer ejecutándose para que Flutter pueda realizar las consultas.

### Verificar el funcionamiento

Abrir las siguientes direcciones:

```text
http://localhost:8080/api/health
http://localhost:8080/api/clientes
```

La primera verifica que el servidor Java está disponible.

La segunda consulta información almacenada en MySQL.

## 8. Ejecutar Flutter

Abrir una segunda terminal y entrar a:

```powershell
cd app_taller_mecanico
```

Descargar las dependencias:

```powershell
flutter pub get
```

### Ejecutar en navegador

```powershell
flutter run -d chrome
```

### Ejecutar en Android

Primero iniciar un emulador desde Android Studio.

Consultar los dispositivos disponibles:

```powershell
flutter devices
```

Ejecutar:

```powershell
flutter run
```

### Comunicación con Java

La dirección del backend depende del entorno:

**Navegador web:**

```text
http://localhost:8080
```

**Emulador Android:**

```text
http://10.0.2.2:8080
```

La dirección `10.0.2.2` permite que el emulador Android acceda al servidor local de la computadora.

Para un dispositivo físico debe utilizarse la dirección IP local de la computadora y comprobar que ambos dispositivos puedan comunicarse.

## 9. Reportes PDF

El sistema contempla cinco reportes principales y un comprobante de reparación.

### Reportes disponibles

**1. Órdenes por estado**

Muestra las órdenes registradas y sus estados actuales.

**2. Historial de reparaciones por vehículo**

Permite consultar las reparaciones realizadas a un vehículo utilizando su placa.

**3. Repuestos con inventario bajo**

Muestra los repuestos que tienen cinco unidades o menos.

**4. Órdenes por mecánico**

Presenta las órdenes asignadas a cada mecánico y su estado.

**5. Ingresos por rango de fechas**

Permite consultar los ingresos correspondientes a reparaciones finalizadas dentro de un período seleccionado.

**6. Comprobante final**

Se genera al finalizar una orden de trabajo e incluye el detalle de servicios, repuestos y costo total.

La generación de documentos se realiza mediante Apache PDFBox desde el backend Java.

## 10. Endpoints principales

| Método / módulo | Ruta |
|---|---|
| Verificar servidor | GET /api/health |
| Clientes | /api/clientes |
| Vehículos | /api/vehiculos |
| Mecánicos | /api/mecanicos |
| Servicios | /api/servicios |
| Repuestos | /api/repuestos |
| Órdenes | /api/ordenes |
| Reportes | /api/reportes |

La comunicación entre Flutter y el backend se realiza mediante solicitudes HTTP y respuestas JSON.

Los métodos HTTP se utilizan según la operación correspondiente, como consultar, registrar, actualizar o eliminar información.

## 11. Programación orientada a objetos

El proyecto permite demostrar distintos conceptos de Programación II.

### Encapsulamiento

Los modelos contienen atributos y métodos que permiten acceder a la información de forma controlada.

### Colecciones

Se utilizan listas para almacenar y procesar los resultados de las consultas.

### Manejo de excepciones

Se controlan errores relacionados con validaciones, conexión a MySQL y operaciones HTTP.

### Persistencia

Los registros se almacenan en MySQL mediante JDBC y consultas preparadas.

### Transacciones

Las operaciones relacionadas con el inventario deben ejecutarse dentro de transacciones, garantizando que los movimientos de repuestos y las asignaciones de las órdenes sean consistentes.

### Separación de responsabilidades

Las clases se distribuyen entre modelos, controladores, servicios y acceso a datos.

Los conceptos de herencia, polimorfismo y composición deben identificarse y comprobarse específicamente en el código presentado durante la defensa.

## 12. Pruebas del sistema

Para comprobar el funcionamiento del proyecto se recomienda ejecutar las siguientes pruebas:

1. Registrar un cliente y asociarle uno o varios vehículos.
2. Registrar mecánicos y servicios.
3. Agregar repuestos al inventario.
4. Crear una orden de trabajo y asignar un mecánico.
5. Iniciar una reparación, agregar servicios y utilizar repuestos.
6. Finalizar la orden y generar el comprobante PDF.

También deben comprobarse las validaciones:

- No registrar identificadores duplicados.
- No registrar placas repetidas.
- No permitir existencias negativas.
- No utilizar más repuestos de los disponibles.
- No finalizar órdenes sin servicios.
- No eliminar clientes que tengan vehículos asociados.
- No modificar órdenes finalizadas o canceladas.

### Ejemplo de cálculo

Servicio de cambio de aceite: Q150.00

Dos filtros de aceite: Q45.00 cada uno.

```text
Mano de obra:       Q150.00
Repuestos:           Q90.00
---------------------------
TOTAL:              Q240.00
```

## 13. Documentación

El proyecto incluye o contempla los siguientes documentos de apoyo:

- README con instrucciones de instalación.
- Script SQL de creación de tablas.
- Datos de demostración.
- Diagrama de clases UML.
- Diagrama entidad-relación.
- Manual de usuario.
- Pruebas de funcionamiento.

Los diagramas pueden desarrollarse en PlantUML y exportarse como imágenes para la presentación.

## 14. Control de versiones

El proyecto utiliza Git y GitHub para registrar los cambios durante su desarrollo.

El repositorio debe conservar el código fuente y los archivos necesarios para ejecutar el sistema.

Los requisitos del curso incluyen cinco commits realizados en fechas distintas y la participación de todos los integrantes del equipo.

No deben subirse contraseñas de MySQL ni información confidencial.

## 15. Solución de problemas frecuentes

### Maven no encuentra pom.xml

Ejecutar Maven desde la carpeta `backend-java`.

### Flutter no encuentra pubspec.yaml

Ejecutar Flutter desde `app_taller_mecanico`.

### MySQL muestra Access denied

Verificar usuario, contraseña, puerto y variables de entorno utilizadas por Java.

### Error No suitable driver

Comprobar que MySQL Connector/J esté incluido en el archivo `pom.xml` y que el controlador JDBC esté cargado.

### Address already in use

Detener el servidor Java anterior antes de iniciar una nueva instancia.

### Android no conecta al backend

Verificar que la aplicación utilice `10.0.2.2` cuando se ejecuta en el emulador Android.

### Error de caché Kotlin o Gradle

Detener Flutter y Gradle, y ejecutar:

```powershell
flutter clean
flutter pub get
flutter run
```

Si persiste el error de compilación incremental de Kotlin, puede probarse temporalmente la configuración `kotlin.incremental=false` en `android/gradle.properties`.

## 16. Consideraciones finales

El Último Arranque fue desarrollado como un proyecto académico de Programación II, utilizando una arquitectura organizada y tecnologías que permiten ejecutar el sistema tanto en Android como en un navegador web.

La aplicación utiliza Flutter para la presentación, Java para la lógica y MySQL para el almacenamiento de información.

El sistema está diseñado para ejecutarse localmente y demostrar los conocimientos de programación orientada a objetos, persistencia, validaciones, transacciones y generación de reportes.

Su alcance corresponde a un proyecto académico y no a una plataforma comercial de producción.

---

**Proyecto:** El Último Arranque  
**Curso:** Programación II  
**Tecnologías:** Flutter, Dart, Java, JDBC, MySQL y PDFBox  
**Modalidad:** Aplicación Android y web con backend local  
**Fecha de entrega:** 6 de noviembre de 2026
