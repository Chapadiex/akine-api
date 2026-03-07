package com.akine_api.infrastructure.integration.argentinadatos;

import com.akine_api.application.port.output.FeriadoNacionalProviderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
public class ArgentinaDatosFeriadoProviderAdapter implements FeriadoNacionalProviderPort {

    private final RestClient restClient;

    public ArgentinaDatosFeriadoProviderAdapter(
            @Value("${app.feriados.argentina-datos.base-url:https://api.argentinadatos.com}") String baseUrl
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public List<FeriadoNacionalItem> findByYear(int year) {
        try {
            List<ArgentinaDatosFeriadoItem> payload = restClient.get()
                    .uri("/v1/feriados/{year}", year)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (payload == null || payload.isEmpty()) {
                return List.of();
            }

            return payload.stream()
                    .filter(item -> item.fecha() != null && item.nombre() != null && !item.nombre().isBlank())
                    .map(item -> new FeriadoNacionalItem(item.fecha(), item.nombre().trim(), safeTrim(item.tipo())))
                    .toList();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudieron obtener feriados nacionales para " + year, ex);
        }
    }

    private String safeTrim(String value) {
        if (value == null) return null;
        String out = value.trim();
        return out.isBlank() ? null : out;
    }

    private record ArgentinaDatosFeriadoItem(
            LocalDate fecha,
            String tipo,
            String nombre
    ) {}
}
