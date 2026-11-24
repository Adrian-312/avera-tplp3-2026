package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.constants.ApiPaths;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Jugada;
import py.edu.uc.lp3.avera_tplp3_2026.exception.ErrorResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioResponse;
import py.edu.uc.lp3.avera_tplp3_2026.service.EscenarioService;

@RestController
@RequestMapping(ApiPaths.ESCENARIO)
@Tag(name = "Escenario cruzado", description = "Escenario propio de A1: consulta A2 y A3 y guarda la jugada sólo si ambos aceptan.")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @PostMapping("/explosion-validada")
    @Operation(summary = "Explosión aceptada por A2 y A3",
            description = "Consulta primero a A2 (escudos del jugador) y después a A3 (potencia de la explosión). Si ambos aceptan, A1 aplica sus reglas locales y persiste la jugada; si alguno rechaza, la base queda igual y el código de error nombra al compañero.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Jugada persistida después de ambas aceptaciones"),
            @ApiResponse(responseCode = "400",
                    description = "Rechazo de A2 (RECHAZO_A2), de A3 (RECHAZO_A3), REGLA_RECHAZADA si la condición previa falla, o dato local inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe el jugador o el monstruo indicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "A2 o A3 no respondió (FALLO_A2 / FALLO_A3)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EscenarioResponse> explosionValidada(@RequestBody EscenarioRequest request) {
        return ResponseEntity.status(201).body(escenarioService.ejecutar(request));
    }

    @GetMapping
    @Operation(summary = "Listar jugadas persistidas",
            description = "Permite comprobar que el escenario feliz quedó guardado y que un rechazo no agregó filas.")
    public List<Jugada> listar() {
        return escenarioService.listar();
    }
}
