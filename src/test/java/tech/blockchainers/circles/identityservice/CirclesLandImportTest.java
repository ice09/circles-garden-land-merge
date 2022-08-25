package tech.blockchainers.circles.identityservice;

import org.junit.jupiter.api.Test;
import tech.blockchainers.circles.identityservice.transport.CirclesLandImportService;

import java.io.IOException;

public class CirclesLandImportTest {

    @Test
    public void shouldImportExternalDataSourceFromCirclesLand() throws IOException {
        CirclesLandImportService circlesLandImportService = new CirclesLandImportService("https://dev.api.circles.land");
        circlesLandImportService.importUsersFromCirclesLand();
        //circlesLandImportService.importTrustsFromCirclesLand();
    }

}
