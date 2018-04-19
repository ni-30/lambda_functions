package ntryn.alexa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsoleMessage implements GcmMsg {
    private long timestamp = System.currentTimeMillis();
    private Stage stage;
    private Game game;
    private Trigger trigger;
}
