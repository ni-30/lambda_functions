package ntryn.alexa.service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import ntryn.alexa.AwsConfig;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.Stage;
import ntryn.alexa.dto.UserEntity;
import java.util.Map;

public class DatabaseService {
    private static final String FUSION_PLAY_CONSOLE_TABLE_NAME = "fusionPlayConsole";

    private final DynamoDB dynamoDB;

    public DatabaseService() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(AwsConfig.region));
        this.dynamoDB = new DynamoDB(client);
    }

    public void create(String uid, Map<String, Object> data) throws InvalidEntityDataException {
        Item item = new Item()
                            .withPrimaryKey("uid", uid)
                            .withString("emailId", data.get("emailId").toString())
                            .withString("gcmEndpointArn", data.get("gcmEndpointArn").toString());

        item.withBoolean("isConsoleLinked", true);

        if(data.containsKey("stage")) {
            String stage = (String) data.get("stage");
            if(stage != null) {
                try {
                    Stage.valueOf(stage);
                } catch (Exception e) {
                    throw new InvalidEntityDataException("invalid stage");
                }
            }
            item.withString("stage", stage);
        }

        if(data.containsKey("game")) {
            String game = (String) data.get("game");
            if(game != null) {
                try {
                    Game.valueOf(game);
                } catch (Exception e) {
                    throw new InvalidEntityDataException("invalid game name");
                }
            }
            item.withString("game", game);
        }

        Table table = dynamoDB.getTable(FUSION_PLAY_CONSOLE_TABLE_NAME);
        table.putItem(item);
    }

    public void update(String uid, Map<String, Object> data) throws InvalidEntityDataException, UserNotFoundException {
        StringBuilder updateExpressionBuilder = new StringBuilder().append("set ");
        final NameMap nameMap = new NameMap();
        final ValueMap valueMap = new ValueMap();
        if(data.containsKey("emailId")) {
            updateExpressionBuilder.append("#key1=:val1, ");
            nameMap.with("#key1", "emailId");
            valueMap.withString(":val1", data.get("emailId").toString());
        }

        if(data.containsKey("gcmEndpointArn")) {
            updateExpressionBuilder.append("#key2=:val2, ");
            nameMap.with("#key2", "gcmEndpointArn");
            valueMap.withString(":val2", data.get("gcmEndpointArn").toString());
        }

        if(data.containsKey("isConsoleLinked")) {
            Boolean isConsoleLinked = (Boolean) data.get("isConsoleLinked");
            if(isConsoleLinked == null) {
                throw new InvalidEntityDataException("isConsoleLinked cannot be null");
            }
            updateExpressionBuilder.append("#key3=:val3, ");
            nameMap.with("#key3", "isConsoleLinked");
            valueMap.withBoolean(":val3", isConsoleLinked);
        }

        if(data.containsKey("stage")) {
            String stage = (String) data.get("stage");
            if(stage != null) {
                try {
                    Stage.valueOf(stage);
                } catch (Exception e) {
                    throw new InvalidEntityDataException("invalid stage");
                }
            }
            updateExpressionBuilder.append("#key4=:val4, ");
            nameMap.with("#key4", "stage");
            valueMap.withString(":val4", stage);
        }

        if(data.containsKey("game")) {
            String game = (String) data.get("game");
            if(game != null) {
                try {
                    Game.valueOf(game);
                } catch (Exception e) {
                    throw new InvalidEntityDataException("invalid game name");
                }
            }
            updateExpressionBuilder.append("#key5=:val5, ");
            nameMap.with("#key5", "game");
            valueMap.withString(":val5", game);
        }

        if(nameMap.isEmpty()) {
            throw new InvalidEntityDataException("invalid update data");
        }

        String updateExpression = updateExpressionBuilder.toString().trim();
        updateExpression = updateExpression.substring(0, updateExpression.length() - 1);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uid", uid)
                                                            .withUpdateExpression(updateExpression)
                                                            .withNameMap(nameMap)
                                                            .withValueMap(valueMap)
                                                            .withReturnValues(ReturnValue.ALL_NEW);

        Table table = dynamoDB.getTable(FUSION_PLAY_CONSOLE_TABLE_NAME);
        table.updateItem(updateItemSpec);
    }

    public UserEntity get(String uid) {
        Table table = dynamoDB.getTable(FUSION_PLAY_CONSOLE_TABLE_NAME);
        Item item = table.getItem("uid", uid);
        if(item == null) return null;

        UserEntity entity = new UserEntity();
        entity.setUid(item.getString("uid"));
        entity.setEmailId(item.getString("emailId"));
        entity.setGcmEndpointArn(item.getString("gcmEndpointArn"));
        entity.setGame(item.getString("game") == null ? null : Game.valueOf(item.getString("game")));
        entity.setIsConsoleLinked(item.getBoolean("isConsoleLinked"));
        entity.setStage(item.getString("stage") == null ? null : Stage.valueOf(item.getString("stage")));

        return entity;
    }

    public String convertAlexaTokenToUid(String token) {
        return token;
    }

}
