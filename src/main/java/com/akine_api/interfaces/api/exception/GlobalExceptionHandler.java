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
            ProfesionalConsultorioNotFoundException.class,
            ConsultorioHorarioNotFoundException.class,
            DisponibilidadProfesionalNotFoundException.class
    })
    public ProblemDetail handleEntityNotFound(DomainException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:not-found"));
        pd.setTitle("No encontrado");
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("urn:akine:error:conflict"));
        pd.setTitle("Conflicto de datos");
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
