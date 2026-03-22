package com.akine_api.infrastructure.persistence.entity.cobro;

import com.akine_api.domain.model.cobro.MedioPago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cobro_cobro_paciente_detalle")
@Getter
@Setter
@NoArgsConstructor
public class CobroPacienteDetalleEntity {

    @Id
    private UUID id;

    @Column(name = "cobro_paciente_id", nullable = false)
    private UUID cobroPacienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago", nullable = false, length = 30)
    private MedioPago medioPago;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal importe;

    @Column(name = "referencia_operacion", length = 100)
    private String referenciaOperacion;

    @Column
    private Integer cuotas;

    @Column(length = 100)
    private String banco;

    @Column(name = "marca_tarjeta", length = 50)
    private String marcaTarjeta;

    @Column(name = "numero_ultimos_4", length = 4)
    private String numeroUltimos4;

    @Column(name = "fecha_acreditacion")
    private LocalDate fechaAcreditacion;

    @Column
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
    }
}
