package tech.blockchainers.circles.identityservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CirclesIdentityServiceApplication implements CommandLineRunner {

	private final UserSyncService userSyncService;
	private final ApplicationContext applicationContext;

	public CirclesIdentityServiceApplication(UserSyncService userSyncService, ApplicationContext applicationContext) {
		this.userSyncService = userSyncService;
		this.applicationContext = applicationContext;
	}

	public static void main(String[] args) {
		SpringApplication.run(CirclesIdentityServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userSyncService.exportToCsv("merged-export.csv");
		SpringApplication.exit(applicationContext, (ExitCodeGenerator) () -> 0);
	}

}
