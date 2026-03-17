package xyz.jwizard.jwl.common;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ModuleIdentityTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    void shouldIdentifyModule() {
        // given
        final String moduleName = getModuleName();
        final String packageName = getClass().getPackageName();
        // when
        log.info("running smoke test for module: {} (package: {})", moduleName, packageName);
        // then
        assertThat(moduleName).isNotBlank();
        assertThat(packageName).startsWith("xyz.jwizard.jwl");
    }

    protected abstract String getModuleName();
}
