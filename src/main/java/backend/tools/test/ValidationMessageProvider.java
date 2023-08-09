package backend.tools.test;

import java.util.ResourceBundle;

/**
 * Provides localized validation messages for several annotations of the Bean Validation framework.
 * <p>
 *
 * In order to make this concept work, the property file "ValidationMessages.properties" has to store properties in a
 * defined form:<br>
 * -Each property starts with the name of the model class in lowerCamelCase followed by a dot.<br>
 * -The next part of the property is the name of the attribute in lowerCamelCase followed by a dot.<br>
 * -Then follows the name of the validation annotation in lowerCamelCase followed by a dot.<br>
 * -Lastly comes the word message
 * <p>
 *
 * Example of a correct property name: department.code.size.message.<br>
 * The class is Department. Its attribute is code. This is validated by the @Size annotation.
 *
 * @author Michael
 */
public class ValidationMessageProvider {
    /**
     * Access to localized validation resources.
     */
    private ResourceBundle resources;

    /**
     * Initializes the ValidationMessageProvider.
     */
    public ValidationMessageProvider() {
        this.resources = ResourceBundle.getBundle("ValidationMessages");
    }

    /**
     * Provides the localized validation message of the @Size annotation.
     *
     * @param className     The name of the model class.
     * @param attributeName The name of the attribute.
     * @param actualValue   The actual value of an attribute.
     * @param minValue      The minimum value as defined by the Size annotation.
     * @param maxValue      The maximum value as defined by the Size annotation.
     * @return The localized validation message with the given attributes.
     */
    public String getSizeValidationMessage(final String className, final String attributeName, final String actualValue,
            final String minValue, final String maxValue) {

        StringBuilder builder = new StringBuilder();
        String message;

        // Build the property name for access.
        builder.append(className);
        builder.append(".");
        builder.append(attributeName);
        builder.append(".size.message");

        // Get the validation message.
        message = resources.getString(builder.toString());

        // Substitute parameters of the validation message with concrete values.
        message = message.replace("${validatedValue.length()}", actualValue);
        message = message.replace("{min}", minValue);
        message = message.replace("{max}", maxValue);

        return message;
    }

    /**
     * Provides the localized validation message of the @NotNull annotation.
     *
     * @param className     The name of the model class.
     * @param attributeName The name of the attribute.
     * @return The localized validation message.
     */
    public String getNotNullValidationMessage(final String className, final String attributeName) {
        StringBuilder builder = new StringBuilder();
        String message;

        // Build the property name for access.
        builder.append(className);
        builder.append(".");
        builder.append(attributeName);
        builder.append(".notNull.message");

        // Get the validation message.
        message = resources.getString(builder.toString());

        return message;
    }

    /**
     * Provides the localized validation message of the @Min annotation.
     *
     * @param className     The name of the model class.
     * @param attributeName The name of the attribute.
     * @param minValue      The minimal value as defined in the annotation.
     * @return The localized validation message.
     */
    public String getMinValidationMessage(final String className, final String attributeName, final String minValue) {
        StringBuilder builder = new StringBuilder();
        String message;

        // Build the property name for access.
        builder.append(className);
        builder.append(".");
        builder.append(attributeName);
        builder.append(".min.message");

        // Get the validation message.
        message = resources.getString(builder.toString());

        // Substitute parameters of the validation message bei concrete values.
        message = message.replace("{value}", minValue);

        return message;
    }

    /**
     * Provides the localized validation message of the @Max annotation.
     *
     * @param className     The name of the model class.
     * @param attributeName The name of the attribute.
     * @param maxValue      The maximal value as defined in the annotation.
     * @return The localized validation message.
     */
    public String getMaxValidationMessage(final String className, final String attributeName, final String maxValue) {
        StringBuilder builder = new StringBuilder();
        String message;

        // Build the property name for access.
        builder.append(className);
        builder.append(".");
        builder.append(attributeName);
        builder.append(".max.message");

        // Get the validation message.
        message = resources.getString(builder.toString());

        // Substitute parameters of the validation message bei concrete values.
        message = message.replace("{value}", maxValue);

        return message;
    }

    /**
     * Provides the localized validation message of the @NotEmpty annotation.
     *
     * @param className     The name of the model class.
     * @param attributeName The name of the attribute.
     * @return The localized validation message.
     */
    public String getNotEmptyValidationMessage(final String className, final String attributeName) {
        StringBuilder builder = new StringBuilder();
        String message;

        // Build the property name for access.
        builder.append(className);
        builder.append(".");
        builder.append(attributeName);
        builder.append(".notEmpty.message");

        // Get the validation message.
        message = resources.getString(builder.toString());

        return message;
    }

    /**
     * Provides the localized validation message of the @DecimalMin annotation.
     *
     * @param className       The name of the model class.
     * @param attributeName   The name of the attribute.
     * @param decimalMinValue The minimal value as defined in the annotation.
     * @return The localized validation message.
     */
    public String getDecimalMinValidationMessage(final String className, final String attributeName,
            final String decimalMinValue) {
        StringBuilder builder = new StringBuilder();
        String message;

        // Build the property name for access.
        builder.append(className);
        builder.append(".");
        builder.append(attributeName);
        builder.append(".decimalMin.message");

        // Get the validation message.
        message = resources.getString(builder.toString());

        // Substitute parameters of the validation message bei concrete values.
        message = message.replace("{value}", decimalMinValue);

        return message;
    }
}
