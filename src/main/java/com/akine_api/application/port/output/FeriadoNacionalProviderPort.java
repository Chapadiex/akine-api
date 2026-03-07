package com.akine_api.application.port.output;

import java.time.LocalDate;
import java.util.List;

public interface FeriadoNacionalProviderPort {

    List<FeriadoNacionalItem> findByYear(int year);

    record FeriadoNacionalItem(LocalDate fecha, String nombre, String tipo) {}
}
