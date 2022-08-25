package tech.blockchainers.circles.identityservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.blockchainers.circles.identityservice.model.Trust;
import tech.blockchainers.circles.identityservice.model.User;
import tech.blockchainers.circles.identityservice.transport.CirclesGardenImportService;
import tech.blockchainers.circles.identityservice.transport.CirclesLandImportService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
public class UserSyncService {

    private Map<String, User> userMapping;
    private List<Trust> trusts;

    private final CirclesLandImportService circlesLandImportService;
    private final CirclesGardenImportService circlesGardenImportService;

    public UserSyncService(CirclesLandImportService circlesLandImportService, CirclesGardenImportService circlesGardenImportService) {
        this.circlesLandImportService = circlesLandImportService;
        this.circlesGardenImportService = circlesGardenImportService;
        userMapping = new HashMap<>();
    }

    @PostConstruct
    private void init() {
        readTrusts();
        readGardenUser();
        mergeLandUsers();
    }

    private void readTrusts() {
        try {
            trusts = circlesLandImportService.importTrustsFromCirclesLand();
        } catch (IOException ex) {
            log.error("Error during trusts retrieval", ex);
        }
    }

    private void readGardenUser() {
        List<User> users = circlesGardenImportService.readUsers();
        for (User user : users) {
            userMapping.put(user.getAddress(), user);
        }
    }

    private void mergeLandUsers() {
        try {
            List<User> landUsers = circlesLandImportService.importUsersFromCirclesLand();
            for (User user : landUsers) {
                userMapping.put(user.getAddress(), user);
            }
        } catch (IOException ex) {
            log.error("Error during land users retrieval", ex);
        }
    }

    public void exportToCsv(String fileName) {
        try {
            File outCsv = new File(fileName);
            outCsv.createNewFile();
            StringBuilder content = new StringBuilder("truster_address,truster_name,truster_image_url,trustee_address,trustee_name,trustee_image_url,amount" + System.lineSeparator());
            Set<String> uniqueMissing = new HashSet<>();
            int index = 0;
            for (Trust trust : trusts) {
                if ((++index % 100000) == 0) {
                    log.info("index: {}", index);
                }
                StringBuilder csvLine = new StringBuilder();
                User truster = userMapping.get(trust.getTruster());
                if (truster == null) {
                    //log.error("Not truster found for {}", trust.getTruster());
                    uniqueMissing.add(trust.getTruster());
                }
                User trustee = userMapping.get(trust.getTrustee());
                if (trustee == null) {
                    //log.error("Not trustee found for {}", trust.getTrustee());
                    uniqueMissing.add(trust.getTrustee());
                }
                csvLine.append(trust.getTruster());
                csvLine.append(",");
                csvLine.append(((truster != null) && truster.getName() != null) ? truster.getName() : "");
                csvLine.append(",");
                csvLine.append(((truster != null) && truster.getImgUrl() != null) ? truster.getImgUrl() : "");
                csvLine.append(",");
                csvLine.append(trust.getTrustee());
                csvLine.append(",");
                csvLine.append(((trustee != null) && trustee.getName() != null) ? trustee.getName() : "");
                csvLine.append(",");
                csvLine.append(((trustee != null) && trustee.getImgUrl() != null) ? trustee.getImgUrl() : "");
                csvLine.append(",");
                csvLine.append(trust.getAmount());
                csvLine.append(System.lineSeparator());
                content.append(csvLine);
            }
            Files.write(outCsv.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));
            log.info("Unique addresses not found: {}", uniqueMissing.size());
            log.info("Successfully exported {} Trusts with merged Gardens and Land Users to merged-export.csv", trusts.size());
        } catch (Exception ex) {
            log.error("Could not write file", ex);
        }
    }



}
