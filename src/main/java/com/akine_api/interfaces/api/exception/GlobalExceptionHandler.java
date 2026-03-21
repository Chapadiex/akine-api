package com.akine_api.interfaces.api.exception;

import com.akine_api.domain.exception.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:user-already-exists"));
        pd.setTitle("Usuario ya existe");
        return pd;
    }

    @ExceptionHandler(CompanyAlreadyExistsException.class)
    public ProblemDetail handleCompanyAlreadyExists(CompanyAlreadyExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:company-already-exists"));
        pd.setTitle("Empresa ya existe");
        return pd;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:invalid-credentials"));
        pd.setTitle("Credenciales inválidas");
        return pd;
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ProblemDetail handleUserNotActive(UserNotActiveException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:user-not-active"));
        pd.setTitle("Cuenta inactiva");
        return pd;
    }

    @ExceptionHandler(SubscriptionNotActiveException.class)
    public ProblemDetail handleSubscriptionNotActive(SubscriptionNotActiveException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:subscription-not-active"));
        pd.setTitle("Suscripción no vigente");
        return pd;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(InvalidTokenException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:invalid-token"));
        pd.setTitle("Token inválido");
        return pd;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:not-found"));
        pd.setTitle("No encontrado");
        return pd;
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ProblemDetail handleRoleNotFound(RoleNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:not-found"));
        pd.setTitle("Rol no encontrado");
        return pd;
    }

    @ExceptionHandler({
            ConsultorioNotFoundException.class,
            BoxNotFoundException.class,
            ProfesionalNotFoundException.class,
            EspecialidadNotFoundException.class,
            EmpleadoNotFoundException.class,
            ProfesionalConsultorioNotFoundException.class,
            ConsultorioHorarioNotFoundException.class,
            DisponibilidadProfesionalNotFoundException.class,
            TurnoNotFoundException.class,
            ObraSocialNotFoundException.class,
            SubscriptionNotFoundException.class,
            SesionClinicaNotFoundException.class,
            DiagnosticoClinicoNotFoundException.class,
            AdjuntoClinicoNotFoundException.class,
            CasoAtencionNotFoundException.class
    })
    public ProblemDetail handleEntityNotFound(DomainException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:not-found"));
        pd.setTitle("No encontrado");
        return pd;
    }

    @ExceptionHandler(SubscriptionStateException.class)
    public ProblemDetail handleSubscriptionState(SubscriptionStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:subscription-state"));
        pd.setTitle("Estado de suscripción inválido");
        return pd;
    }

    @ExceptionHandler(DisponibilidadFueraDeHorarioException.class)
    public ProblemDetail handleDisponibilidadFueraDeHorario(DisponibilidadFueraDeHorarioException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:disponibilidad-fuera-horario"));
        pd.setTitle("Disponibilidad fuera de horario");
        return pd;
    }

    @ExceptionHandler(DisponibilidadSolapamientoException.class)
    public ProblemDetail handleDisponibilidadSolapamiento(DisponibilidadSolapamientoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:disponibilidad-solapamiento"));
        pd.setTitle("Disponibilidad solapada");
        return pd;
    }

    @ExceptionHandler(TurnoConflictException.class)
    public ProblemDetail handleTurnoConflict(TurnoConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:turno-conflicto"));
        pd.setTitle("Conflicto de turno");
        return pd;
    }

    @ExceptionHandler(PacienteDuplicadoException.class)
    public ProblemDetail handlePacienteDuplicado(PacienteDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:paciente-duplicado"));
        pd.setTitle("Paciente duplicado");
        return pd;
    }

    @ExceptionHandler(PacienteYaVinculadoException.class)
    public ProblemDetail handlePacienteYaVinculado(PacienteYaVinculadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:paciente-ya-vinculado"));
        pd.setTitle("Paciente ya vinculado");
        return pd;
    }

    @ExceptionHandler(PacienteNotFoundException.class)
    public ProblemDetail handlePacienteNotFound(PacienteNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:paciente-not-found"));
        pd.setTitle("Paciente no encontrado");
        return pd;
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ProblemDetail handleTransicionEstadoInvalida(TransicionEstadoInvalidaException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:transicion-estado-invalida"));
        pd.setTitle("Transición de estado inválida");
        return pd;
    }

    @ExceptionHandler(SlotNoDisponibleException.class)
    public ProblemDetail handleSlotNoDisponible(SlotNoDisponibleException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:slot-no-disponible"));
        pd.setTitle("Slot no disponible");
        return pd;
    }

    @ExceptionHandler(FeriadoException.class)
    public ProblemDetail handleFeriado(FeriadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:feriado"));
        pd.setTitle("Día feriado");
        return pd;
    }

    @ExceptionHandler(TurnoPacienteSolapadoException.class)
    public ProblemDetail handleTurnoPacienteSolapado(TurnoPacienteSolapadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:turno-paciente-solapado"));
        pd.setTitle("Turno de paciente solapado");
        return pd;
    }

    @ExceptionHandler(HistoriaClinicaConflictException.class)
    public ProblemDetail handleHistoriaClinicaConflict(HistoriaClinicaConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:historia-clinica-conflict"));
        pd.setTitle("Conflicto de historia clinica");
        return pd;
    }

    @ExceptionHandler(HistoriaClinicaValidationException.class)
    public ProblemDetail handleHistoriaClinicaValidation(HistoriaClinicaValidationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:historia-clinica-validation"));
        pd.setTitle("Validacion de historia clinica");
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:conflict"));
        pd.setTitle("Conflicto de datos");
        return pd;
    }

    @ExceptionHandler(PlanDowngradeNotAllowedException.class)
    public ProblemDetail handlePlanDowngradeNotAllowed(PlanDowngradeNotAllowedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:plan-downgrade-not-allowed"));
        pd.setTitle("Downgrade de plan no permitido");
        return pd;
    }

    @ExceptionHandler(PlanLimitExceededException.class)
    public ProblemDetail handlePlanLimitExceeded(PlanLimitExceededException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:plan-limit-exceeded"));
        pd.setTitle("Límite de plan alcanzado");
        return pd;
    }

    @ExceptionHandler(FeatureNotAvailableException.class)
    public ProblemDetail handleFeatureNotAvailable(FeatureNotAvailableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:feature-not-available"));
        pd.setTitle("Funcionalidad no disponible en el plan");
        return pd;
    }

    @ExceptionHandler(ConsultorioInactiveException.class)
    public ProblemDetail handleConsultorioInactive(ConsultorioInactiveException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:consultorio-inactive"));
        pd.setTitle("Consultorio inactivo");
        return pd;
    }

    @ExceptionHandler(PlanDuplicadoException.class)
    public ProblemDetail handlePlanDuplicado(PlanDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:plan-duplicado"));
        pd.setTitle("Plan duplicado");
        return pd;
    }

    @ExceptionHandler(FinanciadorDuplicadoException.class)
    public ProblemDetail handleFinanciadorDuplicado(FinanciadorDuplicadoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:financiador-duplicado"));
        pd.setTitle("Financiador duplicado");
        return pd;
    }

    @ExceptionHandler(ObraSocialConflictException.class)
    public ProblemDetail handleObraSocialConflict(ObraSocialConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:obra-social-conflict"));
        pd.setTitle("Conflicto de obra social");
        return pd;
    }

    @ExceptionHandler(ObraSocialValidationException.class)
    public ProblemDetail handleObraSocialValidation(ObraSocialValidationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:obra-social-validation"));
        pd.setTitle("Validacion de obra social");
        return pd;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Acceso denegado");
        pd.setType(URI.create("urn:akine:error:access-denied"));
        pd.setTitle("Acceso denegado");
        return pd;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "inválido",
                        (a, b) -> a
                ));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, "Errores de validación");
        pd.setType(URI.create("urn:akine:error:validation"));
        pd.setTitle("Validación fallida");
        pd.setProperty("fields", fieldErrors);
        return ResponseEntity.unprocessableEntity().body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        logger.error("Error inesperado", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
        pd.setType(URI.create("urn:akine:error:internal"));
        pd.setTitle("Error interno");
        return pd;
    }
}
