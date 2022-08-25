package tech.blockchainers.circles.identityservice.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import tech.blockchainers.circles.identityservice.model.Source;
import tech.blockchainers.circles.identityservice.model.Trust;
import tech.blockchainers.circles.identityservice.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class CirclesLandImportService {

    private final String circlesLandUrl;

    public CirclesLandImportService(@Value("${circles.land.gql.url}") String circlesLandUrl) {
        Objects.requireNonNull(circlesLandUrl, "circles.land.gql.url must be set.");
        this.circlesLandUrl = circlesLandUrl;
    }

    public List<User> importUsersFromCirclesLand() throws IOException {
        log.info("Reading Users from Land");
        List<User> userProfiles = new ArrayList<>();
        JsonNode node = readProfiles();
        JSONArray array = node.getArray();
        JSONObject object = (JSONObject) array.iterator().next();
        String allProfiles = ((JSONObject) object.get("data")).get("allProfiles").toString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> map = mapper.readValue(allProfiles, List.class);
        for (Map<String, String> entry: map) {
            User user = new User(Keys.toChecksumAddress(entry.get("circlesAddress")), entry.get("displayName"));
            user.setImgUrl(entry.get("avatarUrl"));
            user.setSource(Source.LAND);
            userProfiles.add(user);
        }
        return userProfiles;
    }

    public List<Trust> importTrustsFromCirclesLand() throws IOException {
        log.info("Reading Trusts from Land");
        List<Trust> allTrusts = new ArrayList<>();
        JsonNode rels = readRelations();
        String allProfiles = ((JSONObject) rels.getObject().get("data")).get("allTrusts").toString();

        ObjectMapper mapper = new ObjectMapper();
        List map = mapper.readValue(allProfiles, List.class);
        log.info("Trusts read: {}", map.size());
        for (Object entry : map) {
            Map<String, Object> mentry = (Map<String, Object>) entry;
            Trust trust = new Trust(Keys.toChecksumAddress(mentry.get("trusterAddress").toString()), Keys.toChecksumAddress(mentry.get("trusteeAddress").toString()), (Integer) mentry.get("trustLimit"));
            if (!mentry.get("trustLimit").equals(0)) {
                allTrusts.add(trust);
            } else {
                allTrusts.remove(trust);
            }
        }
        return allTrusts;
    }

    private JsonNode readProfiles() {
        String dataRaw =
                """
                {"operationName":null,"variables":{},"query":"{\\n  allProfiles {\\n    lastChange\\n    circlesAddress\\n    displayName\\n    avatarUrl\\n  }\\n}\\n"}
                """;
        JsonNode node =
                Unirest.post(circlesLandUrl)
                        .header("Content-Type", "application/json")
                        .body(dataRaw).asJson().getBody();
        return node;
    }

    private JsonNode readRelations() {
        String dataRaw =
                """
                {"operationName":null,"variables":{},"query":"{\\n  allTrusts {\\n    lastChange\\n   trusterAddress\\n    trusteeAddress\\n    trustLimit\\n  }\\n}\\n"}
                """;
        JsonNode node =
                Unirest.post(circlesLandUrl)
                        .socketTimeout(60 * 60 * 2 * 1000)
                        .connectTimeout(60 * 60 * 2 * 1000)
                        .header("Content-Type", "application/json")
                        .body(dataRaw).asJson().getBody();
        return node;
    }

}
