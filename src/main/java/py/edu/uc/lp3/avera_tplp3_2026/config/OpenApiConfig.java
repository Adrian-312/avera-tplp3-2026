package py.edu.uc.lp3.avera_tplp3_2026.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import py.edu.uc.lp3.avera_tplp3_2026.constants.Contacto;

@Configuration
public class OpenApiConfig {

    @Value("${app.puerto}")
    private String puerto;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("POO-08 / POO-09 - Minecraft")
                        .version("1.0.0")
                        .description("CRUD REST, simulación de explosiones y escenario cruzado con validaciones externas.")
                        .contact(new Contact().name("Adrian-312").email(Contacto.A1)))
                .servers(List.of(
                        new Server().url("http://localhost:" + puerto).description("Servicio A1 local")));
    }
}
