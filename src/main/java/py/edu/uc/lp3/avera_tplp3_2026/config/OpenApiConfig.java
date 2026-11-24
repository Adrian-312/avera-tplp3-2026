package py.edu.uc.lp3.avera_tplp3_2026.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import py.edu.uc.lp3.avera_tplp3_2026.constants.ApiPaths;
import py.edu.uc.lp3.avera_tplp3_2026.constants.Contacto;

@Configuration
public class OpenApiConfig {

    @Value("${app.puerto}")
    private String puerto;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("POO-09 - Minecraft (A1)")
                        .version("1.0.0")
                        .description("CRUD de entidades, simulación de explosiones, las reglas publicadas para los compañeros (daño por caída y absorción de escudos) y el escenario cruzado: A1 consulta a A2 (Yamil Padilla) y a A3 (Javier Atlasovich) antes de persistir la jugada en H2.")
                        .contact(new Contact().name(Contacto.A1).url(Contacto.GITHUB)))
                .servers(List.of(
                        new Server().url("http://localhost:" + puerto).description("Servicio A1 local"),
                        new Server().url("http://localhost:" + puerto + ApiPaths.API_DOCS)
                                .description("Documento OpenAPI de A1")));
    }
}
