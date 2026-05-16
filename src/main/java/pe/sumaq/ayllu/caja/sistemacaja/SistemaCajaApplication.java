package pe.sumaq.ayllu.caja.sistemacaja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SistemaCajaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaCajaApplication.class, args);
    }
}
