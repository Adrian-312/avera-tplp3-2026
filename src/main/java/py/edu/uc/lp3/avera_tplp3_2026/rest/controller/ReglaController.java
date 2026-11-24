package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.constants.ApiPaths;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.AbsorcionEscudoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.CaidaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.VeredictoRegla;
import py.edu.uc.lp3.avera_tplp3_2026.service.ReglaService;

@RestController
@RequestMapping(ApiPaths.REGLAS)
@Tag(name = "Reglas publicadas por A1", description = "Reglas de Minecraft que los compañeros consultan desde el OpenAPI de A1: daño por caída y absorción de escudos. La simulación de explosiones sigue en /minecraft/simulacion/explosion.")
public class ReglaController {

    private final ReglaService service;

    public ReglaController(ReglaService service) {
        this.service = service;
    }

    @PostMapping("/danio-caida")
    @Operation(summary = "REGLA 1 - Daño por caída", description = "Calcula los bloques caídos entre alturaInicial y alturaFinal. Los primeros 3 bloques no hacen daño; a partir del cuarto, cada bloque quita 1 punto. Si danioDeclarado no coincide, la regla rechaza.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daño calculado a partir de la caída"),
            @ApiResponse(responseCode = "400", description = "REGLA_RECHAZADA o ENTIDAD_INVALIDA")
    })
    public VeredictoRegla danioCaida(@RequestBody CaidaRequest request) {
        return service.danioCaida(request);
    }

    @PostMapping("/absorcion-escudo")
    @Operation(summary = "REGLA 2 - Absorción de escudo", description = "Cada escudo absorbe 5 puntos. Si se declara danioDeclarado y no coincide con el cálculo, la regla rechaza. No modifica la base.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daño calculado a partir de los escudos"),
            @ApiResponse(responseCode = "400", description = "REGLA_RECHAZADA o ENTIDAD_INVALIDA")
    })
    public VeredictoRegla absorcionEscudo(@RequestBody AbsorcionEscudoRequest request) {
        return service.absorcionEscudo(request);
    }
}
