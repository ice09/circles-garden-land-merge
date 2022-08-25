package tech.blockchainers.circles.identityservice;

import org.junit.jupiter.api.Test;
import tech.blockchainers.circles.identityservice.model.User;
import tech.blockchainers.circles.identityservice.transport.CirclesGardenImportService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CirclesGardensImportTest {

    @Test
    public void shouldImportExternalDataSourceFromCirclesGardens()  {
        CirclesGardenImportService circlesGardenImportService = new CirclesGardenImportService("https://circles-usernames.s3.eu-central-1.amazonaws.com/user-data-20220805.csv");
        List<User> users = circlesGardenImportService.readUsers();
        assertNotNull(users);
    }

}
