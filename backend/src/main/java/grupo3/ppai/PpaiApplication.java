package grupo3.ppai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PpaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PpaiApplication.class, args);
		System.out.println("Hola mundo");
		
		grupo3.ppai.entidades.Rol rolGCM = new grupo3.ppai.entidades.Rol("GCM", "GCM");
	}


}
