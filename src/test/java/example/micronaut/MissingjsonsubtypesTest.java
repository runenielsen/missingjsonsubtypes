package example.micronaut;

import com.example.openapi.model.BookAvailability;
import com.example.openapi.model.BookInfo;
import com.example.openapi.model.DetailedBookInfo;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;

import java.io.IOException;

@MicronautTest
class MissingjsonsubtypesTest {

    private final ObjectMapper objectMapper;

    public MissingjsonsubtypesTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testSerdeForDetailedBookInfo() throws IOException {

        String bookInfoString = objectMapper.writeValueAsString(
                new DetailedBookInfo(
                        "Michael Ende",
                        "SOME_ISBN",
                        "Never-ending Story",
                        BookAvailability.AVAILABLE,
                        null
                )
        );

        System.out.println(bookInfoString);

        var bookInfo = objectMapper.readValue(bookInfoString, BookInfo.class);

        System.out.println(bookInfo);

        Assertions.assertTrue(bookInfo instanceof DetailedBookInfo);
        Assertions.assertEquals(bookInfoString, "{\"type\":\"DETAILED\",\"name\":\"Never-ending Story\",\"availability\":\"available\",\"author\":\"Michael Ende\",\"ISBN\":\"SOME_ISBN\"}");
    }
}
