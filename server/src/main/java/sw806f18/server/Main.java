package sw806f18.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

/**
 * Main class.
 */
@SpringBootApplication
public class Main {
    /**
     * Main method.
     *
     * @param args Arguments.
     * @throws IOException Exception.
     */
    public static void main(String[] args) throws IOException {
        if (!(args.length == 1)) {
            System.out.println("Usage: java -jar [file].jar [config].json");
            return;
        }

        Configurations.instance = new Configurations(args[0]);
        SpringApplication.run(Main.class, args);
    }

    /**
     * CORS Config.
     * @return Configuration.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedHeaders("*")  // TODO: DONT DO THIS
                    .allowedOrigins("*")
                    .allowedMethods("*");
            }
        };
    }
}
