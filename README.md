# Task Manager - Backend

Backend del proyecto Task Manager, una aplicación full-stack para gestión de tareas desarrollada con Java y Spring Boot.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)

## 🎯 Objetivo del Proyecto

Este proyecto tiene como objetivo poner en práctica los conocimientos adquiridos en desarrollo backend, incluyendo: 
- Desarrollo de APIs RESTful con Spring Boot
- Gestión de bases de datos relacionales
- Autenticación y autorización
- Envío de correos electrónicos transaccionales
- Containerización con Docker
- Despliegue en la nube

## 🚀 Demo

**Aplicación en producción:** [https://taskmanagerfront-absm.onrender.com/home](https://taskmanagerfront-absm.onrender.com/home)

**Repositorio Frontend:** [https://github.com/CastoldiThiago/TaskManagerFront](https://github.com/CastoldiThiago/TaskManagerFront)

## 🛠️ Tecnologías

- **Java 17**: Versión LTS del lenguaje de programación
- **Spring Boot 3.5.3**: Framework para desarrollo de aplicaciones empresariales
- **PostgreSQL**: Base de datos relacional desplegada en Render
- **Brevo (Sendinblue)**: Servicio de envío de correos electrónicos transaccionales
- **Docker**: Containerización para despliegues consistentes
- **Render**: Platform as a Service para el hosting

## 📋 Características

- ✅ API RESTful para gestión de tareas
- ✅ Conexión a base de datos PostgreSQL
- ✅ Autenticación y autorización de usuarios
- ✅ Envío de emails transaccionales con Brevo
- ✅ Login con Google implementado en código (no activo por costos)
- ✅ Despliegue automatizado con Docker
- ✅ Configuración para ambientes de desarrollo y producción

## 🔧 Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- Docker (opcional, para containerización)
- PostgreSQL (para desarrollo local)
- Cuenta en Brevo para envío de emails

## 💻 Instalación y Configuración Local

### 1. Clonar el repositorio

```bash
git clone https://github.com/CastoldiThiago/TaskManagerBack.git
cd TaskManagerBack
```

### 2. Configurar las variables de entorno

Crea un archivo `application.properties` o `application.yml` en `src/main/resources/` con las siguientes configuraciones:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080

# Brevo Email Configuration
brevo.api.key=tu_api_key_de_brevo
brevo.sender.email=emailvalidadodebrevo@gmail.com

# Google OAuth (opcional)
# spring.security.oauth2.client.registration.google.client-id=tu_client_id
# spring.security.oauth2.client.registration.google.client-secret=tu_client_secret
```

### 3. Instalar dependencias y compilar

```bash
mvn clean install
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

## 🐳 Despliegue con Docker

### Construir la imagen

```bash
docker build -t taskmanager-backend .
```

### Ejecutar el contenedor

```bash
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://tu-host:5432/taskmanager \
  -e DATABASE_USERNAME=tu_usuario \
  -e DATABASE_PASSWORD=tu_contraseña \
  -e BREVO_API_KEY=tu_api_key_de_brevo \
  -e BREVO_SENDER_EMAIL=emailvalidadodebrevo@gmail.com \
  taskmanager-backend
```

## 📁 Estructura del Proyecto

```
TaskManagerBack/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/taskmanager/
│   │   │       ├── controller/     # Controladores REST
│   │   │       ├── service/        # Lógica de negocio
│   │   │       ├── repository/     # Acceso a datos
│   │   │       ├── model/          # Entidades JPA
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       ├── config/         # Configuraciones
│   │   │       ├── security/       # Seguridad y autenticación
│   │   │       └── email/          # Servicio de emails con Brevo
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```

## 📧 Gestión de Emails con Brevo

El proyecto utiliza Brevo (anteriormente Sendinblue) para el envío de correos electrónicos transaccionales, incluyendo: 

- ✉️ Emails de bienvenida
- 🔑 Recuperación de contraseñas
- ✅ Confirmación de registro
- 📬 Notificaciones de tareas

### Configuración de Brevo

1. Crea una cuenta en [Brevo](https://www.brevo.com/)
2. Genera una API Key desde el panel de administración
3. Valida tu email de remitente
4. Configura las credenciales en `application.properties`

## 🔒 Autenticación

El proyecto incluye implementación de login con Google OAuth 2.0, aunque actualmente no está activo en producción debido a costos. El código está preparado para activarse configurando las credenciales correspondientes.

## 🌐 API Endpoints

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/logout` - Cerrar sesión

### Tareas
- `GET /api/tasks` - Listar todas las tareas
- `GET /api/tasks/{id}` - Obtener tarea por ID
- `POST /api/tasks` - Crear nueva tarea
- `PUT /api/tasks/{id}` - Actualizar tarea
- `DELETE /api/tasks/{id}` - Eliminar tarea

## 🚀 Despliegue en Render

El backend está desplegado en Render, utilizando: 
- **Web Service** para la aplicación Spring Boot
- **PostgreSQL** como base de datos administrada
- **Docker** para el proceso de build y despliegue
- **Variables de entorno** para configuración de Brevo y credenciales

## 📝 Licencia

Este proyecto es de código abierto y está disponible bajo la [MIT License](LICENSE).

## 👤 Autor

**Thiago Castoldi**

- GitHub: [@CastoldiThiago](https://github.com/CastoldiThiago)
- Demo: [Task Manager](https://taskmanagerfront-absm.onrender.com/home)

## 🙏 Agradecimientos

Este proyecto fue desarrollado como parte de mi aprendizaje en desarrollo full-stack, poniendo en práctica conceptos de:
- Arquitectura de APIs REST
- Patrones de diseño en Spring Boot
- Gestión de bases de datos relacionales
- Integración con servicios de terceros (Brevo)
- DevOps y containerización
- Despliegue en la nube

---

⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub!