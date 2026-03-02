---
agent:
  name: "AKINE-Backend-Agent"
  version: "1.0.0"
  purpose: "Reglas especificas para Spring Boot en akine-api."
  temperature: 0.2
  scope: "backend"
  target_path: "akine-api"
---

# AGENTS.md - Backend Spring Boot

## 1. Alcance
Este archivo aplica solo al proyecto backend `akine-api`.

Complementa al `AGENTS.md` de la raiz. Si hay conflicto, la seguridad global de la raiz manda.

## 2. Stack y lineamientos base
- Java 21
- Spring Boot
- Maven
- PostgreSQL 15
- Flyway
- MapStruct
- REST + JWT

## 3. Estructura esperada
```text
src/main/java/.../
  controller/
  service/
  repository/
  mapper/
  dto/
  domain/
  config/
  exception/
```

## 4. Reglas de arquitectura
### 4.1 Controllers
- solo orquestan request y response
- sin logica de negocio
- usar DTOs de entrada y salida
- validar con `@Valid`

### 4.2 Services
- concentran logica de negocio
- ubicar aqui transacciones con `@Transactional` cuando corresponda
- no mezclar reglas de negocio con detalles HTTP

### 4.3 Repositories
- consultas claras y eficientes
- evitar N+1
- usar paginacion cuando el volumen lo requiera
- revisar indexes si una query se vuelve critica

### 4.4 Entities y dominio
- no exponer entidades por API
- las entities pueden contener invariantes simples
- no acoplar entities a infraestructura externa

## 5. Contratos y mapping
- obligatorio usar DTOs
- conversion DTO <-> Entity con MapStruct
- recomendado `unmappedTargetPolicy = ReportingPolicy.ERROR`
- si cambia contrato, actualizar pruebas y documentacion

## 6. Seguridad backend
- validar autenticacion y autorizacion en toda operacion sensible
- chequear pertenencia a `consultorio_id`
- prevenir IDOR y accesos cruzados
- no devolver mensajes que filtren informacion sensible innecesaria
- no loguear PII

## 7. Manejo de errores
- usar `@ControllerAdvice`
- diferenciar 4xx de 5xx correctamente
- responder con formato uniforme

Ejemplo:

```json
{
  "code": "ERROR_CODE",
  "message": "Mensaje amigable",
  "details": {}
}
```

## 8. Base de datos y migraciones
- cambios de schema solo por Flyway
- no usar `ddl-auto=update`
- usar `validate` cuando aplique
- scripts SQL claros, versionados y seguros
- no hacer cambios destructivos sin validacion explicita

## 9. Observabilidad
- logs estructurados en JSON
- incluir `traceId`
- no registrar datos clinicos ni PII
- loggear eventos utiles, no ruido ornamental

## 10. Testing
Cambios relevantes deben incluir cuando aplique:
- tests unitarios de service
- tests de integracion
- tests de autorizacion
- tests de validacion
- tests de repository si hay queries complejas

## 11. No hacer
Queda prohibido sin pedido explicito:
- meter logica en controller
- exponer entities en endpoints
- agregar dependencias grandes sin justificacion
- cambiar seguridad base por intuicion
- tocar migraciones viejas ya aplicadas en entornos compartidos

## 12. Verificacion minima
Ejecutar segun corresponda:

```bash
mvn clean package -DskipTests
mvn test
mvn jacoco:report
mvn flyway:validate
```

## 13. Formato de respuesta del agente en backend
Siempre indicar:
- endpoint, service y repositorio afectados
- impacto en contrato, seguridad y DB
- como verificar con comandos exactos
- riesgos funcionales y edge cases
