package tech.blockchainers.circles.identityservice.transport;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import tech.blockchainers.circles.identityservice.model.Source;
import tech.blockchainers.circles.identityservice.model.User;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class CirclesGardenImportService {

    private final String csvUrl;

    public CirclesGardenImportService(@Value("${circles.gardens.csv.url}") String csvUrl) {
        Objects.requireNonNull(csvUrl, "circles.gardens.csv.url must be set.");
        this.csvUrl = csvUrl;
    }

    public List<User> readUsers() {
        log.info("Reading Users from Garden");
        List<User> usersRead = new ArrayList<>();
        try {
            URI csvUri = URI.create(csvUrl);
            Stream<String> lines = Arrays.stream(IOUtils.toString(csvUri, StandardCharsets.UTF_8).split("\n"));
            for (String line : lines.skip(1).toList()) {
                String[] suser = line.split(",");
                User user = new User(Keys.toChecksumAddress(suser[1]), suser[0]);
                user.setSource(Source.GARDENS);
                usersRead.add(user);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return usersRead;
    }

}
