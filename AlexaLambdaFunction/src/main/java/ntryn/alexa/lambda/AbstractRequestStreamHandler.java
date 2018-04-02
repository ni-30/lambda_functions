package ntryn.alexa.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ntryn.alexa.common.Constants.OBJECT_MAPPER;

public abstract class AbstractRequestStreamHandler<I, O> implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        O response = handle(OBJECT_MAPPER.readValue(IOUtils.toByteArray(inputStream), getInputClass()), context);
        if(response != null) {
            outputStream.write(OBJECT_MAPPER.writeValueAsBytes(response));
        }
    }

    protected abstract O handle(I request, Context context);

    protected abstract Class<I> getInputClass();
}
