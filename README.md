# Proyecto Final · Ingeniería de Software III

Sistema académico basado en microservicios desarrollado como proyecto final del curso. Permite gestionar **estudiantes**, **cursos** y **matrículas** con autenticación por JWT y despliegue con Docker.

---

## Tabla de contenidos

1. [Descripción general](#1-descripción-general)
2. [Requisitos previos](#2-requisitos-previos)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Servicios y puertos](#4-servicios-y-puertos)
5. [Ejecución en local](#5-ejecución-en-local)
6. [Ejecución con Docker Compose](#6-ejecución-con-docker-compose)
7. [Swagger UI](#7-swagger-ui)
8. [Consola H2](#8-consola-h2)
9. [Usuarios de prueba](#9-usuarios-de-prueba)
10. [Datos semilla](#10-datos-semilla)
11. [Endpoints principales](#11-endpoints-principales)
12. [Flujo recomendado de prueba](#13-flujo-recomendado-de-prueba)
13. [Variables de entorno Docker](#14-variables-de-entorno-docker)
14. [Respuestas de seguridad](#15-respuestas-de-seguridad)
15. [Problemas comunes](#16-problemas-comunes)
16. [Comandos útiles](#17-comandos-útiles)

---

## 1. Descripción general

El proyecto implementa cuatro microservicios Spring Boot independientes que se comunican entre sí via HTTP:

| Servicio | Responsabilidad |
|---|---|
| `auth-service` | Autenticación de usuarios y emisión/validación de tokens JWT |
| `estudiantes-service` | CRUD de estudiantes con validación de token |
| `cursos-service` | CRUD de cursos con validación de token |
| `matriculas-service` | Registro y consulta de matrículas; valida con los otros tres servicios |

Cada servicio corre de forma autónoma con su propia base de datos H2 en memoria y expone documentación interactiva via Swagger.

---

## 2. Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| Docker Desktop / Docker Engine | cualquier versión reciente |
| Docker Compose | v2+ |

Verifica tu entorno:

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

## 3. Estructura del proyecto

```
proyecto-s3/
├── auth-service/               # Servicio de autenticación (puerto 8081)
│   ├── src/
│   └── Dockerfile
├── estudiantes-service/        # Servicio de estudiantes (puerto 8082)
│   ├── src/
│   └── Dockerfile
├── cursos-service/             # Servicio de cursos (puerto 8083)
│   ├── src/
│   └── Dockerfile
├── matriculas-service/         # Servicio de matrículas (puerto 8084)
│   ├── src/
│   └── Dockerfile
├── docker-compose.yml
├── index.html
└── README.md
```

---

## 4. Servicios y puertos

| Servicio | Puerto | Depende de |
|---|---|---|
| auth-service | 8081 | — |
| estudiantes-service | 8082 | auth-service |
| cursos-service | 8083 | auth-service |
| matriculas-service | 8084 | auth-service, estudiantes-service, cursos-service |

---

## 5. Ejecución en local

### Paso 1 — Compilar cada servicio

```bash
cd auth-service && mvn clean package -DskipTests && cd ..
cd estudiantes-service && mvn clean package -DskipTests && cd ..
cd cursos-service && mvn clean package -DskipTests && cd ..
cd matriculas-service && mvn clean package -DskipTests && cd ..
```

> Este paso es obligatorio incluso si luego usas Docker, porque los `Dockerfile` copian el `.jar` desde `target/`.

### Paso 2 — Levantar los servicios en orden

Abre una terminal distinta para cada servicio y ejecútalos en este orden:

```bash
# Terminal 1
cd auth-service && mvn spring-boot:run

# Terminal 2
cd estudiantes-service && mvn spring-boot:run

# Terminal 3
cd cursos-service && mvn spring-boot:run

# Terminal 4
cd matriculas-service && mvn spring-boot:run
```

---

## 6. Ejecución con Docker Compose

```bash
# 1. Compilar los .jar primero
cd auth-service && mvn clean package -DskipTests && cd ..
cd estudiantes-service && mvn clean package -DskipTests && cd ..
cd cursos-service && mvn clean package -DskipTests && cd ..
cd matriculas-service && mvn clean package -DskipTests && cd ..

# 2. Levantar todos los contenedores
docker compose up --build

# 3. Verificar que estén corriendo
docker ps
```

---

## 7. Swagger UI

| Servicio | URL |
|---|---|
| auth-service | http://localhost:8081/swagger-ui.html |
| estudiantes-service | http://localhost:8082/swagger-ui.html |
| cursos-service | http://localhost:8083/swagger-ui.html |
| matriculas-service | http://localhost:8084/swagger-ui.html |

Para probar endpoints protegidos: haz clic en **Authorize** en Swagger y pega `Bearer <tu_token>`.

---

## 8. Consola H2

Cada servicio expone su base de datos en memoria en `/h2-console`:

| Servicio | URL |
|---|---|
| auth-service | http://localhost:8081/h2-console |
| estudiantes-service | http://localhost:8082/h2-console |
| cursos-service | http://localhost:8083/h2-console |
| matriculas-service | http://localhost:8084/h2-console |

Parámetros de conexión:
- **Driver:** `org.h2.Driver`
- **User Name:** `sa`
- **Password:** *(vacío)*
- **JDBC URL:** ver el `application.yaml` de cada servicio

---

## 9. Usuarios de prueba

El `auth-service` carga automáticamente estos usuarios al iniciar:

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | ADMIN |
| docente | docente123 | DOCENTE |
| estudiante | estudiante123 | ESTUDIANTE |

---

## 10. Datos semilla

### estudiantes-service
- **Ana Martínez** · ana@correo.edu · 19 años (ID: 1)

### cursos-service
- **Ingeniería de Software III** · código `IS3-001` · 4 créditos (ID: 1)

---

## 11. Endpoints principales

### auth-service · puerto 8081

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/login` | Autenticarse y obtener token JWT | No |

### estudiantes-service · puerto 8082

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| GET | `/api/estudiantes` | Listar todos los estudiantes | Bearer |
| POST | `/api/estudiantes` | Crear un estudiante | Bearer (ADMIN) |
| PUT | `/api/estudiantes/{id}` | Actualizar un estudiante | Bearer (ADMIN) |
| DELETE | `/api/estudiantes/{id}` | Eliminar un estudiante | Bearer (ADMIN) |

### cursos-service · puerto 8083

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| GET | `/api/cursos` | Listar todos los cursos | Bearer |
| POST | `/api/cursos` | Crear un curso | Bearer (ADMIN) |
| PUT | `/api/cursos/{id}` | Actualizar un curso | Bearer (ADMIN) |
| DELETE | `/api/cursos/{id}` | Eliminar un curso | Bearer (ADMIN) |

### matriculas-service · puerto 8084

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| GET | `/api/matriculas` | Listar todas las matrículas | Bearer |
| POST | `/api/matriculas` | Registrar una matrícula | Bearer |
| GET | `/api/matriculas/por-estudiante/{estudianteId}` | **Nuevo** — Ver matrículas de un estudiante | Bearer |

---

## 12. Flujo recomendado de prueba

### 1. Obtener token

```http
POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Copia el token de la respuesta.

### 2. Registrar una matrícula

```http
POST http://localhost:8084/api/matriculas
Authorization: Bearer <token>
Content-Type: application/json

{
  "estudianteId": 1,
  "cursoId": 1
}
```

### 3. Consultar matrículas del estudiante (nueva funcionalidad)

```http
GET http://localhost:8084/api/matriculas/por-estudiante/1
Authorization: Bearer <token>
```

### Secuencia mínima de validación

1. Login exitoso con `admin`.
2. Login fallido con contraseña incorrecta → debe retornar `401`.
3. Acceso sin token a cualquier endpoint protegido → debe retornar `401`.
4. Creación de estudiante con rol `ADMIN`.
5. Creación de curso con rol `ADMIN`.
6. Registro de matrícula válido.
7. Consulta de matrículas por estudiante (nueva funcionalidad).
8. Intento de matrícula duplicada → debe rechazarse.
9. Intento de matrícula con `estudianteId` inexistente → debe retornar error.
10. Acceso con rol insuficiente → debe retornar `403`.

---

## 13. Variables de entorno Docker

Docker Compose inyecta las siguientes variables en cada servicio:

| Servicio | Variable | Valor |
|---|---|---|
| estudiantes-service | `AUTH_SERVICE_URL` | `http://auth-service:8081` |
| cursos-service | `AUTH_SERVICE_URL` | `http://auth-service:8081` |
| matriculas-service | `AUTH_SERVICE_URL` | `http://auth-service:8081` |
| matriculas-service | `ESTUDIANTES_SERVICE_URL` | `http://estudiantes-service:8082` |
| matriculas-service | `CURSOS_SERVICE_URL` | `http://cursos-service:8083` |

---

## 14. Respuestas de seguridad

**Sin token o token inválido (`401`):**
```json
{
  "success": false,
  "message": "Debe enviar un token Bearer válido",
  "errorCode": "AUTH_HEADER_MISSING",
  "status": 401,
  "path": "/api/recurso",
  "timestamp": "2026-04-11T15:30:00"
}
```

**Token válido pero rol insuficiente (`403`):** el servidor retorna `403 Forbidden`.

---

## 15. Problemas comunes

**`No such file or directory` al construir imagen Docker**
Causa: no se compiló el servicio antes de correr Docker.
Solución: ejecutar `mvn clean package -DskipTests` dentro de cada carpeta de servicio.

**`Connection refused` entre servicios**
Causa: un servicio dependiente aún no ha iniciado.
Solución: esperar unos segundos y reintentar; verificar que todos los contenedores estén en estado `Up`.

**`401` en servicios protegidos**
Causa posible: token expirado, header `Authorization` ausente, o formato incorrecto (debe ser `Bearer <token>`).

**`403` al intentar crear o eliminar recursos**
Causa: el usuario autenticado no tiene el rol `ADMIN`.

---

## 16. Comandos útiles

```bash
# Detener todos los contenedores
docker compose down

# Reconstruir desde cero
docker compose down
cd auth-service && mvn clean package -DskipTests && cd ..
cd estudiantes-service && mvn clean package -DskipTests && cd ..
cd cursos-service && mvn clean package -DskipTests && cd ..
cd matriculas-service && mvn clean package -DskipTests && cd ..
docker compose up --build

# Ver logs de un servicio específico
docker logs matriculas-service

# Ver todos los contenedores activos
docker ps
```
