package com.akine_api.interfaces.api.v1.consultorio.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConsultorioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String name,

        @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
        String description,

        @Size(max = 500, message = "La URL del logo no puede superar 500 caracteres")
        String logoUrl,

        @Size(max = 13, message = "El CUIT no puede superar 13 caracteres")
        String cuit,

        @Size(max = 255, message = "La razon social no puede superar 255 caracteres")
        String legalName,

        @Size(max = 500, message = "La direccion no puede superar 500 caracteres")
        String address,

        @Size(max = 500, message = "La direccion georreferenciada no puede superar 500 caracteres")
        String geoAddress,

        @Size(max = 255, message = "La referencia de acceso no puede superar 255 caracteres")
        String accessReference,

        @Size(max = 120, message = "El piso, unidad o box no puede superar 120 caracteres")
        String floorUnit,

        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String phone,

        @Email(message = "El email debe tener un formato valido")
        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        String email,

        @Size(max = 255, message = "El responsable administrativo no puede superar 255 caracteres")
        String administrativeContact,

        @Size(max = 1000, message = "Las observaciones internas no pueden superar 1000 caracteres")
        String internalNotes,

        @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
        @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
        BigDecimal mapLatitude,

        @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
        @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
        BigDecimal mapLongitude,

        @Size(max = 500, message = "La URL de Google Maps no puede superar 500 caracteres")
        String googleMapsUrl,

        @Size(max = 255, message = "El nombre para documentos no puede superar 255 caracteres")
        String documentDisplayName,

        @Size(max = 255, message = "El subtitulo para documentos no puede superar 255 caracteres")
        String documentSubtitle,

        @Size(max = 500, message = "La URL del logo documental no puede superar 500 caracteres")
        String documentLogoUrl,

        @Size(max = 1000, message = "El pie documental no puede superar 1000 caracteres")
        String documentFooter,

        Boolean documentShowAddress,
        Boolean documentShowPhone,
        Boolean documentShowEmail,
        Boolean documentShowCuit,
        Boolean documentShowLegalName,
        Boolean documentShowLogo,

        @Size(max = 120, message = "El numero de habilitacion no puede superar 120 caracteres")
        String licenseNumber,

        @Size(max = 120, message = "El tipo de habilitacion no puede superar 120 caracteres")
        String licenseType,

        LocalDate licenseExpirationDate,

        @Size(max = 255, message = "El responsable profesional no puede superar 255 caracteres")
        String professionalDirectorName,

        @Size(max = 120, message = "La matricula profesional no puede superar 120 caracteres")
        String professionalDirectorLicense,

        @Size(max = 1000, message = "La referencia documental no puede superar 1000 caracteres")
        String legalDocumentSummary,

        @Size(max = 1000, message = "Las observaciones legales no pueden superar 1000 caracteres")
        String legalNotes,

        @Size(max = 20, message = "El estado no puede superar 20 caracteres")
        String status
) {}
