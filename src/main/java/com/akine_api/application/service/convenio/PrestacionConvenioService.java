package com.akine_api.application.service.convenio;

import com.akine_api.domain.model.convenio.Prestacion;
import com.akine_api.domain.repository.convenio.PrestacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PrestacionConvenioService {

    private final PrestacionRepositoryPort prestacionRepository;

    @Transactional(readOnly = true)
    public List<Prestacion> findAll() {
        return prestacionRepository.findAllActivas();
    }

    @Transactional(readOnly = true)
    public Prestacion findById(UUID id) {
        return prestacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestacion no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Prestacion findByCodigo(String codigo) {
        return prestacionRepository.findByCodigoNomenclador(codigo)
                .orElseThrow(() -> new RuntimeException("Prestacion no encontrada con codigo: " + codigo));
    }
}
