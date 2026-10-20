# Migración Standing Offer (Bonita EE a Bonita CM)

Robot y orquestador en Spring Boot 2.7.18 (Java 1.8) empaquetado en formato WAR para despliegue en WildFly 10.1, encargado de la migración automatizada por etapas de casos de Standing Offer desde **Bonita Enterprise (EE)** hacia **Bonita Community (CM)** para BBVA Colombia.

---

## 🚀 Arquitectura y Pipeline

El servicio implementa un patrón **Pipeline / Chain of Responsibility** extensible y resiliente:

1. **Radicación (`POST .../instantiation`)**: Instancia el proceso en Bonita CM con el payload y variables del caso original en EE.
2. **Scoring (`POST .../execution`)**: Ejecuta la tarea humana de scoring bancario.
3. **Evaluación y Sanción (`POST .../execution`)**: Procesa y valida la decisión de crédito/sanción.
4. **VoBo Pagador (`POST .../execution`)**: Gestiona la aprobación y visto bueno de pagaduría/libranza.
5. **Formalización (`POST .../execution`)**: Cierra el ciclo contractual y operativo del caso.

### Características Clave
- **Concurrencia Configurable**: Pool de subprocesos (`migracion.hilos=8`) para procesar lotes masivos sin cuellos de botella.
- **Tolerancia a Fallos y Reintentos**: Reintentos automáticos configurables con backoff exponencial por etapa (`migracion.reintentos.max-intentos=3`).
- **Seguimiento y Auditoría**: Genera trazas y archivos de mapeo `relacion-ee-cm-*.json` para correlacionar los números de caso (`idCasoEE` vs `idCasoCM`).

---

## 🛠️ Tecnologías y Requisitos

- **Java**: 1.8 (OpenJDK / Oracle JDK)
- **Framework**: Spring Boot 2.7.18
- **Servidor de Aplicaciones**: JBoss WildFly 10.1 (`jboss-web.xml` context `/migracion-standing-offer`)
- **Bases de Datos**: Oracle (Esquemas `BBVA_BI` / `BONITA_EE` / `BONITA_CM`)
- **Gestor de Dependencias**: Maven 3.6+

---

## ⚙️ Configuración (`application.yml`)

El proyecto mapea las variables de entorno estándar del servidor (por ejemplo, `/etc/profile.d/ENV_BONITA.sh`):

| Variable de Entorno | Descripción | Valor por Defecto |
|---|---|---|
| `BONITA_CM_URL` | URL base de Bonita Community API | `http://localhost:8080/bonita` |
| `BONITA_CM_USER` | Usuario técnico API Bonita | `install` |
| `BONITA_CM_PASS` | Contraseña técnica API Bonita | `install` |
| `BONITA_CM_PROCESS_NAME` | Nombre del proceso en CM | `Standing Offer` |
| `BONITA_CM_PROCESS_VERSION` | Versión del proceso en CM | `1.0` |
| `DB_EE_URL` | JDBC URL Oracle Enterprise | `jdbc:oracle:thin:@...` |
| `DB_EE_USER` | Usuario Oracle Enterprise | `BBVA_BI` |
| `DB_EE_PASS` | Password Oracle Enterprise | - |
| `DB_CM_URL` | JDBC URL Oracle Community | `jdbc:oracle:thin:@...` |

---

## 📡 Endpoints de la API

Contexto base: `http://<host>:<port>/migracion-standing-offer/api/migracion`

- **Migrar un caso específico**:
  ```http
  POST /api/migracion/caso/{idCasoEE}
  ```
- **Migrar caso hasta una etapa puntual**:
  ```http
  POST /api/migracion/caso/{idCasoEE}?hastaEtapa=scoring
  ```
- **Migrar lote de casos (Multihilo)**:
  ```http
  POST /api/migracion/lote
  Content-Type: application/json

  [1001, 1002, 1003, 1004]
  ```
- **Consultar estado/etapas soportadas**:
  ```http
  GET /api/migracion/etapas
  ```

---

## 📦 Compilación y Empaque

Para generar el archivo `war` compatible con WildFly 10.1:

```bash
mvn clean package -DskipTests
```

El artefacto resultante se ubicará en `target/migracion-standing-offer-0.0.1-SNAPSHOT.war`.
