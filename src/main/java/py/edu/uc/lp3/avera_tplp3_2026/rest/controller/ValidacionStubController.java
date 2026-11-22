package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.ValidacionExternaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.ValidacionExternaResponse;

@RestController
@RequestMapping("/integracion/stub")
@Tag(name = "Stubs temporales A2/A3", description = "Reemplazos locales de los compañeros mientras se definen los grupos. Se desactivan cambiando integracion.a2.base-url y integracion.a3.base-url.")
public class ValidacionStubController {

    private final int escudosMinimos;
    private final int potenciaMaxima;

    public ValidacionStubController(
            @Value("${integracion.stub.a2.escudos-minimos}") int escudosMinimos,
            @Value("${integracion.stub.a3.potencia-maxima}") int potenciaMaxima) {
        this.escudosMinimos = escudosMinimos;
        this.potenciaMaxima = potenciaMaxima;
    }

    @PostMapping("/a2/validar")
    @Operation(summary = "Stub de A2", description = "Acepta si el jugador lleva al menos la cantidad de escudos configurada.")
    public ValidacionExternaResponse validarA2(@RequestBody ValidacionExternaRequest request) {
        if (request.escudosDisponibles() < escudosMinimos) {
            return new ValidacionExternaResponse(false,
                    "A2 exige al menos " + escudosMinimos + " escudo(s) antes de la explosión", "SIN_ESCUDO");
        }
        return new ValidacionExternaResponse(true, "A2 aceptó: escudos suficientes", "OK_A2");
    }

    @PostMapping("/a3/validar")
    @Operation(summary = "Stub de A3", description = "Acepta si la potencia no supera el máximo configurado.")
    public ValidacionExternaResponse validarA3(@RequestBody ValidacionExternaRequest request) {
        if (request.potencia() > potenciaMaxima) {
            return new ValidacionExternaResponse(false,
                    "A3 admite como máximo potencia " + potenciaMaxima, "POTENCIA_ALTA");
        }
        return new ValidacionExternaResponse(true, "A3 aceptó: potencia dentro del límite", "OK_A3");
    }
}
