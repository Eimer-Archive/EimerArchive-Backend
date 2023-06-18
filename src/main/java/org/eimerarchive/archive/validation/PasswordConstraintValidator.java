package org.eimerarchive.archive.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Cleanup;
import org.passay.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * A custom jakarta.validation validator for passwords.
 * Uses the Passay library to make validation easy.
 *
 * @author steviebeenz
 * @see ValidPassword
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    private PasswordValidator validator;
    @Override
    public void initialize(final ValidPassword arg0) {
        // Load the messages for passay validation
        // This file is shaded with passay
        MessageResolver messageResolver;
        try {
            Properties props = new Properties();
            URL propertiesURL = getClass()
                    .getClassLoader().getResource("passay.properties");
            final @Cleanup InputStream inputStream = propertiesURL.openStream();
            props.load(inputStream);
            messageResolver = new PropertiesMessageResolver(props);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load passay messages", e);
        }

        // Create the validator object (a spring DI based version of this would be nice)
        this.validator = new PasswordValidator(messageResolver, List.of(
                // length must be at least 8 and no more than 32
                // prevents short passwords and spam
                new LengthRule(8, 32),
                /*
                the below four rules help encourage the use of random passwords.
                this helps also stop people from using their name but
                first letter capitalised, some number somewhere and a dot at the end.
                */
                // we want to ensure there is an uppercase character
                new CharacterRule(EnglishCharacterData.UpperCase, 2),
                // we want people to not go all upper-case
                new CharacterRule(EnglishCharacterData.LowerCase, 2),
                // we want people to use numbers, but not just swap one character
                new CharacterRule(EnglishCharacterData.Digit, 2),
                // we want people to use special characters, but not just a dot at the end.
                new CharacterRule(EnglishCharacterData.Special, 2),
                // stop number spam
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 4, false),
                // stop people just using QWERTY or similar
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 4, false)
        ));

    }
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        RuleResult result = this.validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = this.validator.getMessages(result);
        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}