package uk.gov.justice.services.messaging;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.UUID;

import static javax.json.JsonValue.NULL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.CAUSATION;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.CLIENT_CORRELATION;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.CONTEXT;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.ID;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.NAME;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.SESSION_ID;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.STREAM;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.STREAM_ID;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.USER_ID;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.VERSION;
import static uk.gov.justice.services.messaging.JsonObjectMetadata.metadataFrom;

/**
 * Unit tests for the {@link JsonObjectMetadata} class.
 */
public class JsonObjectMetadataTest {

    private static final String UUID_ID = "d04885b4-9652-4c2a-87c6-299bda0a87d4";
    private static final String UUID_CLIENT_CORRELATION = "8d67ed44-ecfb-43ce-867c-53077abf97a6";
    private static final String UUID_CAUSATION = "49ef76bc-df4f-4b91-8ca7-21972c30ee4c";
    private static final String UUID_USER_ID = "182a8f83-faa0-46d6-96d0-96999f05e3a2";
    private static final String UUID_SESSION_ID = "f0132298-7b79-4397-bab6-f2f5e27915f0";
    private static final String UUID_STREAM_ID = "f29e0415-3a3b-48d8-b301-d34faa58662a";
    private static final String MESSAGE_NAME = "logical.message.name";
    private static final Long STREAM_VERSION = 99L;

    private JsonObject jsonObject;
    private Metadata metadata;

    @Before
    public void setup() {
        jsonObject = Json.createObjectBuilder()
                .add(ID, UUID_ID)
                .add(NAME, MESSAGE_NAME)
                .add(CLIENT_CORRELATION[0], Json.createObjectBuilder()
                        .add(CLIENT_CORRELATION[1], UUID_CLIENT_CORRELATION)
                )
                .add(CAUSATION, Json.createArrayBuilder()
                        .add(UUID_CAUSATION)
                )
                .add(CONTEXT, Json.createObjectBuilder()
                        .add(USER_ID[1], UUID_USER_ID)
                        .add(SESSION_ID[1], UUID_SESSION_ID)
                )
                .add(STREAM, Json.createObjectBuilder()
                        .add(STREAM_ID[1], UUID_STREAM_ID)
                        .add(VERSION[1], STREAM_VERSION)
                )
                .build();
        metadata = metadataFrom(jsonObject);
    }

    @Test
    public void shouldReturnId() throws Exception {
        assertThat(metadata.id(), equalTo(UUID.fromString(UUID_ID)));
    }

    @Test
    public void shouldReturnName() throws Exception {
        assertThat(metadata.name(), equalTo(MESSAGE_NAME));
    }

    @Test
    public void shouldReturnClientCorrelationId() throws Exception {
        assertThat(metadata.clientCorrelationId().isPresent(), is(true));
        assertThat(metadata.clientCorrelationId().get(), equalTo(UUID_CLIENT_CORRELATION));
    }

    @Test
    public void shouldReturnCausation() throws Exception {
        assertThat(metadata.causation(), equalTo(ImmutableList.of(UUID.fromString(UUID_CAUSATION))));
    }

    @Test
    public void shouldReturnUserId() throws Exception {
        assertThat(metadata.userId().isPresent(), is(true));
        assertThat(metadata.userId().get(), equalTo(UUID_USER_ID));
    }

    @Test
    public void shouldReturnSessionId() throws Exception {
        assertThat(metadata.sessionId().isPresent(), is(true));
        assertThat(metadata.sessionId().get(), equalTo(UUID_SESSION_ID));
    }

    @Test
    public void shouldReturnStreamId() throws Exception {
        assertThat(metadata.streamId().isPresent(), is(true));
        assertThat(metadata.streamId().get(), equalTo(UUID.fromString(UUID_STREAM_ID)));
    }

    @Test
    public void shouldReturnStreamVersion() throws Exception {
        assertThat(metadata.version().isPresent(), is(true));
        assertThat(metadata.version().get(), equalTo(STREAM_VERSION));
    }

    @Test
    public void shouldReturnJsonObject() throws Exception {
        assertThat(metadata.asJsonObject(), equalTo(jsonObject));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfIdIsMissing() throws Exception {
        metadataFrom(Json.createObjectBuilder()
                .build()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfIdIsNotUUID() throws Exception {
        metadataFrom(Json.createObjectBuilder()
                .add(ID, "blah")
                .build()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfIdIsNull() throws Exception {
        metadataFrom(Json.createObjectBuilder()
                .add(ID, NULL)
                .build()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNameIsMissing() throws Exception {
        metadataFrom(Json.createObjectBuilder()
                .add(ID, UUID_ID)
                .build()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNameIsEmpty() throws Exception {
        metadataFrom(Json.createObjectBuilder()
                .add(ID, UUID_ID)
                .add(NAME, "")
                .build()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNameIsNull() throws Exception {
        metadataFrom(Json.createObjectBuilder()
                .add(ID, UUID_ID)
                .add(NAME, NULL)
                .build()
        );
    }
}
