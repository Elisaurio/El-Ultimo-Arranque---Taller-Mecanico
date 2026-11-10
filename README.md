# 🚗 El Último Arranque — Sistema de Gestión de Taller Mecánico

## 📌 Descripción del proyecto

**El Último Arranque** es un sistema de gestión para talleres mecánicos desarrollado en Java, utilizando la arquitectura **Modelo-Vista-Controlador (MVC)**.

Su objetivo es facilitar la administración de clientes, vehículos, mecánicos, repuestos, servicios y órdenes de trabajo, además de generar reportes que apoyen el control administrativo del taller.

El sistema cuenta con una interfaz web y una integración con Android para consultar la información desde un dispositivo móvil.

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java | Lenguaje principal |
| Spring Boot | Desarrollo del servidor web |
| Spring MVC | Arquitectura Modelo-Vista-Controlador |
| Thymeleaf | Renderización de vistas HTML |
| HTML y CSS | Interfaz gráfica |
| MySQL | Base de datos relacional |
| JDBC | Acceso a datos |
| Maven | Gestión de dependencias |
| Jetty | Servidor web embebido |
| Apache PDFBox | Generación de reportes PDF |
| Android Studio | Desarrollo de la aplicación Android |
| Android Java / WebView | Integración móvil del sistema |

## 🏗️ Arquitectura del sistema

El proyecto utiliza **MVC**, complementada con capas de servicios y acceso a datos.

**Modelo (Model):** representa las entidades del taller y sus atributos.

**Vista (View):** presenta la información mediante páginas HTML y plantillas Thymeleaf.

**Controlador (Controller):** recibe las solicitudes HTTP y coordina las operaciones del sistema.

**Servicio (Service):** contiene la lógica de negocio, las validaciones y las operaciones necesarias para gestionar el taller.

**Repositorio (Repository):** realiza las consultas SQL y la comunicación con MySQL mediante JDBC y `PreparedStatement`.

### Flujo de funcionamiento

```text
Usuario
   ↓
Vista (Thymeleaf)
   ↓
Controlador (Spring MVC)
   ↓
Servicio (Lógica de negocio)
   ↓
Repositorio (JDBC)
   ↓
Base de datos MySQL
```

## 📂 Estructura del proyecto

```text
tallerMecanico/
│
├── backend-java/
│   ├── src/main/java/com/ultimoarranque/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── model/
│   │
│   ├── src/main/resources/
│   │   ├── view/
│   │   ├── static/
│   │   └── application.properties
│   │
│   └── pom.xml
│
├── android-base/
│   ├── app/
│   │   └── src/main/
│   │       ├── java/
│   │       ├── res/
│   │       └── AndroidManifest.xml
│   │
│   └── gradlew.bat
│
└── README.md
```

## ⚙️ Funcionalidades principales

### 1. Gestión de clientes
- Registrar nuevos clientes.
- Consultar información.
- Editar y eliminar registros.
- Asociar vehículos a cada cliente.

### 2. Gestión de vehículos
- Registrar vehículos.
- Asociarlos a sus propietarios.
- Modificar y consultar sus datos.
- Consultar su historial de mantenimiento.

### 3. Gestión de mecánicos
- Registrar mecánicos.
- Actualizar sus datos.
- Consultar las órdenes asignadas.

### 4. Gestión de repuestos
- Registrar repuestos.
- Administrar precios y existencias.
- Actualizar inventario.
- Identificar productos con inventario bajo.

### 5. Gestión de servicios
- Registrar servicios del taller.
- Definir y actualizar sus precios.
- Asociar servicios a órdenes de trabajo.

### 6. Órdenes de trabajo
- Crear órdenes asociadas a vehículos y mecánicos.
- Agregar servicios y repuestos.
- Administrar estados: pendiente, en proceso, finalizada y cancelada.
- Calcular costos.
- Controlar movimientos de inventario asociados a las órdenes.
- Generar comprobantes.

## 📊 Reportes

El sistema genera cinco tipos de reportes en PDF:

1. **Órdenes por estado:** consulta las órdenes según su situación.
2. **Historial por vehículo:** muestra los trabajos realizados a un vehículo.
3. **Inventario bajo:** identifica repuestos con existencias menores o iguales a cinco unidades.
4. **Órdenes por mecánico:** muestra los trabajos asociados a cada mecánico.
5. **Ingresos por fechas:** presenta los ingresos registrados durante un período seleccionado.

Los reportes se generan desde el backend utilizando Apache PDFBox.

## 🗄️ Base de datos

Se utiliza **MySQL**, mediante una base de datos denominada:

```sql
taller_mecanico
```

Las principales tablas son:

- `clientes`
- `vehiculos`
- `mecanicos`
- `repuestos`
- `servicios`
- `ordenes_trabajo`
- `orden_servicios`
- `orden_repuestos`

Las relaciones permiten gestionar los vehículos de cada cliente, las órdenes realizadas, los mecánicos asignados y los servicios y repuestos utilizados.

El acceso a la base de datos se realiza mediante JDBC, utilizando consultas preparadas y transacciones cuando corresponde.

## 🚀 Instalación y ejecución

### Requisitos

- Java y JDK compatible con el proyecto.
- Maven.
- MySQL.
- Visual Studio Code o IntelliJ IDEA.
- Android Studio para ejecutar la aplicación móvil.

### 1. Clonar el repositorio

```bash
git clone https://github.com/Elisaurio/El-Ultimo-Arranque---Taller-Mecanico.git
```

Acceder a la carpeta del proyecto:

```bash
cd El-Ultimo-Arranque---Taller-Mecanico
```

### 2. Preparar la base de datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS taller_mecanico;
```

Ejecutar el script SQL del proyecto para crear sus tablas.

### 3. Configurar la conexión

Configurar los datos de conexión a MySQL en la configuración local del backend.

Ejemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taller_mecanico
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASENA
```

Las credenciales reales no deben incluirse en el repositorio público.

### 4. Iniciar el servidor

Desde la carpeta `backend-java`:

```bash
mvn spring-boot:run
```

### 5. Abrir el sistema web

Acceder desde el navegador a:

```text
http://localhost:8080/
```

## 📱 Aplicación Android

El proyecto también incluye una aplicación Android desarrollada en Java.

Se implementó una pantalla de consulta de inventario bajo mediante una API REST y una integración mediante **WebView** para visualizar el sistema web MVC dentro de Android.

El backend sigue siendo responsable de la lógica de negocio y del acceso a MySQL.

### Ejecutar en Android Studio

1. Abrir la carpeta `android-base`.
2. Sincronizar Gradle.
3. Iniciar un emulador Android.
4. Mantener el servidor Spring Boot ejecutándose.
5. Presionar **Run** para instalar y abrir la aplicación.

Para conectarse desde el emulador Android al servidor local de la computadora se utiliza:

```text
http://10.0.2.2:8080/
```

Esta dirección es específica del emulador Android. Para ejecutar la aplicación desde un dispositivo físico es necesario configurar una dirección de red accesible.

## 🔐 Validaciones y seguridad

El sistema incorpora:

- Validación de información ingresada.
- Consultas SQL mediante `PreparedStatement`.
- Separación entre interfaz, lógica de negocio y persistencia.
- Manejo de errores.
- Transacciones para operaciones que modifican el inventario.

## 🎯 Objetivo académico

Este proyecto permite aplicar conocimientos de:

- Programación orientada a objetos.
- Arquitectura MVC.
- Desarrollo web con Java.
- Bases de datos relacionales.
- Operaciones CRUD.
- JDBC y consultas preparadas.
- Transacciones.
- Generación de reportes PDF.
- Integración de aplicaciones móviles con sistemas web.

## ✅ Conclusión

**El Último Arranque** integra los procesos principales de un taller mecánico en un sistema organizado y modular.

La implementación de MVC permite separar las responsabilidades del código, facilitar su mantenimiento y reutilizar la lógica de negocio para distintos tipos de interfaces.

---

**Proyecto final — Curso de Programación**

**El Último Arranque | Sistema de Gestión de Taller Mecánico**