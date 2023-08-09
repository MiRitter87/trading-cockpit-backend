package backend.tools;

import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

/**
 * Provides convenience methods for WebServices.
 *
 * @author Michael
 */
public final class WebServiceTools {
    /**
     * Constructor. Hidden, because WebServiceTools is a utility class.
     */
    private WebServiceTools() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if a WebServiceResult contains an error message.
     *
     * @param result The WebServiceResult.
     * @return true, if error messages are contained; false otherwise.
     */
    public static boolean resultContainsErrorMessage(final WebServiceResult result) {
        for (WebServiceMessage message : result.getMessages()) {
            if (message.getType() == WebServiceMessageType.E) {
                return true;
            }
        }

        return false;
    }
}
