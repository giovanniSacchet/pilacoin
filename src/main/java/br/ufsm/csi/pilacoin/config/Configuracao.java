package br.ufsm.csi.pilacoin.config;

import br.ufsm.csi.pilacoin.shared.KeyUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configuracao {
    @Bean
    public OpenAPI customOpenAPI() throws Exception {
        KeyUtil.carregarChavePrivada();
        KeyUtil.carregarChavePublica();
        return new OpenAPI().info(new Info().title("Pilacoin_backend").description("Backend Projeto Pilacoin").version("@"));
    }
}
