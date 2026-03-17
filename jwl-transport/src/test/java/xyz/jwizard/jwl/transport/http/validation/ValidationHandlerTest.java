package xyz.jwizard.jwl.transport.http.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.jwizard.jwl.transport.http.TestEnvelope;
import xyz.jwizard.jwl.transport.http.TestUser;
import xyz.jwizard.jwl.transport.http.validation.validator.LengthValidator;
import xyz.jwizard.jwl.transport.http.validation.validator.NotNullValidator;
import xyz.jwizard.jwl.transport.http.validation.validator.RangeValidator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ValidationHandlerTest {
    private ValidationHandler validationHandler;

    @BeforeEach
    void setUp() {
        validationHandler = new ValidationHandler(Set.of(
            new NotNullValidator(),
            new LengthValidator(),
            new RangeValidator()
        ));
    }

    @Test
    @DisplayName("should pass validation for correct data")
    void shouldPassValidation() {
        // given
        final TestUser validTestUser = new TestUser("JWizard", 40);
        // when & then
        assertThatCode(() -> validationHandler.validate(validTestUser))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should throw ValidationException when @NotNull field is missing")
    void shouldFailOnNull() {
        // given
        final TestUser invalidTestUser = new TestUser(null, 20);
        // when & then
        assertThatThrownBy(() -> validationHandler.validate(invalidTestUser))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Field 'name' must not be null");
    }

    @Test
    @DisplayName("Should fail when @Length is too short")
    void shouldFailOnLength() {
        // given
        final TestUser invalidTestUser = new TestUser("JW", 20);
        // when & then
        assertThatThrownBy(() -> validationHandler.validate(invalidTestUser))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Field 'name' length must be between 3 and");
    }

    @Test
    @DisplayName("Should fail when @Range is below minimum")
    void shouldFailOnRange() {
        // given
        final TestUser invalidTestUser = new TestUser("JWizard", 15);
        // when & then
        assertThatThrownBy(() -> validationHandler.validate(invalidTestUser))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Field 'age' must be between 18 and");
    }

    @Test
    @DisplayName("should work correctly with cache (second execution)")
    void shouldWorkWithCache() {
        // given
        final TestUser testUser = new TestUser("JWizard", 40);
        // when
        validationHandler.validate(testUser);
        // when & then
        assertThatCode(() -> validationHandler.validate(testUser))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should fail when nested User age is below 18")
    void shouldFailWhenNestedUserAgeIsInvalid() {
        // given
        final TestUser underageTestUser = new TestUser("Jwizard", 15);
        final TestEnvelope testEnvelope = new TestEnvelope("REQ-101", underageTestUser);
        // when & then
        assertThatThrownBy(() -> validationHandler.validate(testEnvelope))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Field 'age'")
            .hasMessageContaining("must be between 18 and");
    }

    @Test
    @DisplayName("should fail when nested User name is too short")
    void shouldFailWhenNestedUserNameIsTooShort() {
        // given
        final TestUser shortNameTestUser = new TestUser("Jo", 20);
        final TestEnvelope testEnvelope = new TestEnvelope("REQ-102", shortNameTestUser);
        // when & then
        assertThatThrownBy(() -> validationHandler.validate(testEnvelope))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Field 'name'")
            .hasMessageContaining("between 3 and");
    }

    @Test
    @DisplayName("should pass validation when Envelope and nested User are valid")
    void shouldPassWhenEverythingIsValid() {
        // given
        final TestUser validTestUser = new TestUser("Jwizard", 25);
        final TestEnvelope validTestEnvelope = new TestEnvelope("REQ-103", validTestUser);
        // when & then
        assertThatCode(() -> validationHandler.validate(validTestEnvelope))
            .doesNotThrowAnyException();
    }
}
