package tech.blockchainers.circles.identityservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {

    private final String address;
    private final String name;
    private Source source;
    private String imgUrl;

}
