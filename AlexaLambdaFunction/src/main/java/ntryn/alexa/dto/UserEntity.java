package ntryn.alexa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.Stage;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntity {
    private String uid;
    private String emailId;
    private String gcmEndpointArn;
    private Boolean isConsoleLinked;
    private Stage stage;
    private Game game;
}
