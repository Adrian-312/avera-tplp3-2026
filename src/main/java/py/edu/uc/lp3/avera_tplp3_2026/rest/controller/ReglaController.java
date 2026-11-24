package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

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
import py.edu.uc.lp3.avera_tplp3_2026.exception.ErrorResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.AbsorcionEscudoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.CondicionPreviaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.VeredictoRegla;
import py.edu.uc.lp3.avera_tplp3_2026.service.ReglaService;

@RestController
@RequestMapping(ApiPaths.REGLAS)
@Tag(name = "Reglas publicadas por A1", description = "Las dos reglas de Minecraft que un compañero consulta desde este OpenAPI: condición previa de la explosión y efecto de los escudos sobre el jugador. Ninguna modifica la base.")
public class ReglaController {

    private final ReglaService service;

    public ReglaController(ReglaService service) {
        this.service = service;
    }

    @PostMapping("/condicion-previa")
    @Operation(summary = "REGLA 1 - Condición previa de la explosión",
            description = "Antes de ejecutar una explosión, comprueba en la base de A1 que el jugador y el monstruo existan, que sean PersonajeJugable y Monstruo y que ambos estén vivos. La misma validación la usa el escenario cruzado. No cambia el estado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La condición previa se cumple; el veredicto trae los ids, tipos y vidas"),
            @ApiResponse(responseCode = "400",
                    description = "REGLA_RECHAZADA si falta alguno, el tipo es incorrecto o alguno está muerto; ENTIDAD_INVALIDA si falta un id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public VeredictoRegla condicionPrevia(@RequestBody CondicionPreviaRequest request) {
        return service.validarCondicionPrevia(request);
    }

    @PostMapping("/absorcion-escudo")
    @Operation(summary = "REGLA 2 - Efecto sobre el jugador y sus escudos",
            description = "Cada escudo absorbe 5 puntos de la onda y se consume. A partir de la potencia, los escudos disponibles y la vida recibida, devuelve escudosConsumidos, danioCalculado y vidaDespues. Si se declara un daño distinto del calculado, la regla rechaza. No modifica la base.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daño y vida resultantes calculados a partir de los escudos"),
            @ApiResponse(responseCode = "400",
                    description = "REGLA_RECHAZADA si el daño declarado no coincide; ENTIDAD_INVALIDA si falta un campo o es negativo",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public VeredictoRegla absorcionEscudo(@RequestBody AbsorcionEscudoRequest request) {
        return service.absorcionEscudo(request);
    }
}
