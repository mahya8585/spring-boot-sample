package com.techbookstore.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.jpa.defer-datasource-initialization=true",
    "spring.security.user.name=admin",
    "spring.security.user.password=admin",
    "spring.security.user.roles=ADMIN"
})
class MinimalApplicationTest {

    @Test
    void contextLoads() {
        // Basic smoke test to verify application context loads
        // This test ensures our code quality improvements don't break the application startup
    }
}