package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.constants.ApiPaths;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Jugada;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioResponse;
import py.edu.uc.lp3.avera_tplp3_2026.service.EscenarioService;

@RestController
@RequestMapping(ApiPaths.ESCENARIO)
@Tag(name = "Escenario cruzado", description = "Escenario propio de A1: validaciones de A2 y A3 antes de persistir en H2.")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @PostMapping("/explosion-validada")
    @Operation(summary = "Explosión aceptada por A2 y A3",
            description = "Consulta A2 y luego A3. Si ambos aceptan, aplica las reglas locales y guarda la jugada; si alguno rechaza, la base queda igual y el error nombra al compañero.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Jugada persistida después de ambas aceptaciones"),
            @ApiResponse(responseCode = "400", description = "Rechazo de A2 o de A3, o dato local inválido"),
            @ApiResponse(responseCode = "502", description = "Algún compañero no respondió")
    })
    public ResponseEntity<EscenarioResponse> explosionValidada(@RequestBody EscenarioRequest request) {
        return ResponseEntity.status(201).body(escenarioService.ejecutar(request));
    }

    @GetMapping
    @Operation(summary = "Listar jugadas persistidas", description = "Permite comprobar que el escenario exitoso quedó guardado y que un rechazo no agregó filas.")
    public List<Jugada> listar() {
        return escenarioService.listar();
    }
}
