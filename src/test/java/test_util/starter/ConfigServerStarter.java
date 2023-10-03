package test_util.starter;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(parallel = true)
public interface ConfigServerStarter {

    @Container
    @SuppressWarnings("resource")
    GenericContainer<?> configServer = new GenericContainer<>(DockerImageName.parse("eann1s/cloud-config-server:latest"))
            .withExposedPorts(8888)
            .waitingFor(Wait.forHttp("/authentication-integration_tests.service/test").forStatusCode(200));

    @BeforeAll
    static void setEnvironmentVariables() {
        System.setProperty("CONFIG_SERVER_URL", configServer.getHost() + ":" + configServer.getFirstMappedPort());
    }
}
