package br.com.furb.rotasegura.configurations.envs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "master.user")
public class MasterUserEnvironment {

    private String name;
    private String password;
    private String email;

}