package tech.blockchainers.circles.identityservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trust {

    private String truster;
    private String trustee;
    private Integer amount;

}
