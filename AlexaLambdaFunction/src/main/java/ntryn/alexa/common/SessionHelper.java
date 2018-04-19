package ntryn.alexa.common;

import com.amazon.speech.speechlet.Session;

public class SessionHelper {
    private final Session session;

    public SessionHelper(Session session) {
        this.session = session;
        this.session.setAttribute("version", 1);
    }

    public boolean isNewSession() {
        return session.isNew();
    }

    public boolean isSkillLinked() {
        return session.getUser().getAccessToken() != null;
    }

    public boolean isConsoleLinked() {
        Boolean value = (Boolean) session.getAttribute("isConsoleLinked");
        return value != null && value;
    }

    public void setIsConsoleLinked(boolean isLinked) {
        session.setAttribute("isConsoleLinked", isLinked);
    }

    public void setUid(String uid) {
        session.setAttribute("uid", uid);
    }

    public String getUid() {
        return (String) session.getAttribute("uid");
    }

    public void setGcmEndpointArn(String arn) {
        session.setAttribute("gcmEndpointArn", arn);
    }

    public String getGcmEndpointArn() {
        return (String) session.getAttribute("gcmEndpointArn");
    }

    public void setGame(Game game) {
        session.setAttribute("game", game == null ? null : game.name());
    }

    public Game getGame() {
        String game = (String) session.getAttribute("game");
        return game == null ? null : Game.valueOf(game);
    }

    public void setStage(Stage stage) {
        session.setAttribute("stage", stage == null ? null : stage.name());
    }

    public Stage getStage() {
        String stage = (String) session.getAttribute("stage");
        return stage == null ? null : Stage.valueOf(stage);
    }

    public void setTrigger(Trigger trigger, String triggerRequestId) {
        session.setAttribute("trigger", trigger == null ? null : trigger.name());
        setTriggerRequestId(triggerRequestId);
    }

    public Trigger getTrigger() {
        String trigger = (String) session.getAttribute("trigger");
        return trigger == null ? null : Trigger.valueOf(trigger);
    }

    private void setTriggerRequestId(String requestId) {
        session.setAttribute("triggerRequestId", requestId);
    }

    public String getTriggerRequestId() {
        return (String) session.getAttribute("triggerRequestId");
    }

    public void setPreviousRequestId(String requestId) {
        session.setAttribute("previousRequestId", requestId);
    }

    public String getPreviousRequestId() {
        return (String) session.getAttribute("previousRequestId");
    }

    public void setConfirmation(Confirmation confirmation) {
        session.setAttribute("confirmation", Utils.serialize(confirmation));
    }

    public Confirmation getConfirmation() {
        return Utils.deserializeFromJsonString((String) session.getAttribute("confirmation"), Confirmation.class);
    }

    public void removeConfirmation() {
        session.removeAttribute("confirmation");
    }
}
