package uk.gov.justice.services.eventsourcing.source.api.service.core;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.eventsourcing.repository.jdbc.Direction.BACKWARD;
import static uk.gov.justice.services.eventsourcing.repository.jdbc.Direction.FORWARD;
import static uk.gov.justice.services.eventsourcing.source.api.service.core.Position.first;
import static uk.gov.justice.services.eventsourcing.source.api.service.core.Position.head;
import static uk.gov.justice.services.eventsourcing.source.api.service.core.Position.sequence;

import uk.gov.justice.services.eventsourcing.repository.jdbc.eventstream.EventStream;
import uk.gov.justice.services.eventsourcing.repository.jdbc.eventstream.EventStreamJdbcRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventStreamServiceTest {

    @Mock
    private EventStreamJdbcRepository repository;

    @InjectMocks
    private EventStreamService service;

    @Test
    public void shouldReturnHeadEvents() throws Exception {

        final long pageSize = 2L;

        final UUID streamId3 = randomUUID();
        final UUID streamId4 = randomUUID();

        final EventStream eventStream3 = new EventStream(streamId3, 3L);
        final EventStream eventStream4 = new EventStream(streamId4, 4L);

        final Stream.Builder<EventStream> eventStreamBuilder = Stream.builder();

        eventStreamBuilder.add(eventStream4);
        eventStreamBuilder.add(eventStream3);

        when(repository.head(pageSize)).thenReturn(eventStreamBuilder.build());

        final List<EventStreamEntry> entries = service.eventStream(head(), BACKWARD, pageSize);

        assertThat(entries, hasSize(2));

        assertThat(entries.get(0).getStreamId(), is(streamId4.toString()));

        assertThat(entries.get(0).getSequenceNumber(), is(4L));

        assertThat(entries.get(1).getStreamId(), is(streamId3.toString()));

        assertThat(entries.get(1).getSequenceNumber(), is(3L));
    }

    @Test
    public void shouldReturnFirstEvents() throws Exception {
        final long pageSize = 2L;

        final UUID streamId1 = randomUUID();
        final UUID streamId2 = randomUUID();

        final Stream.Builder<EventStream> eventStreamBuilder = Stream.builder();

        final EventStream eventStream1 = new EventStream(streamId1, 1L);
        final EventStream eventStream2 = new EventStream(streamId2, 2L);

        eventStreamBuilder.add(eventStream2);
        eventStreamBuilder.add(eventStream1);

        when(repository.first(pageSize)).thenReturn(eventStreamBuilder.build());

        final List<EventStreamEntry> eventStreamEntries = service.eventStream(first(), FORWARD, pageSize);

        assertThat(eventStreamEntries, hasSize(2));

        assertThat(eventStreamEntries.get(0).getStreamId(), is(streamId2.toString()));

        assertThat(eventStreamEntries.get(0).getSequenceNumber(), is(2L));

        assertThat(eventStreamEntries.get(1).getStreamId(), is(streamId1.toString()));

        assertThat(eventStreamEntries.get(1).getSequenceNumber(), is(1L));
    }

    @Test
    public void shouldReturnPreviousEvents() throws Exception {

        final long pageSize = 2L;

        final UUID streamId2 = randomUUID();
        final UUID streamId3 = randomUUID();

        final Stream.Builder<EventStream> eventStreamBuilder = Stream.builder();

        final EventStream eventStream2 = new EventStream(streamId2, 2L);
        final EventStream eventStream3 = new EventStream(streamId3, 3L);

        eventStreamBuilder.add(eventStream3);
        eventStreamBuilder.add(eventStream2);

        final long sequenceId = 3L;

        when(repository.backward(sequenceId, pageSize)).thenReturn(eventStreamBuilder.build());

        final List<EventStreamEntry> eventEntries = service.eventStream(sequence(sequenceId), BACKWARD, pageSize);

        assertThat(eventEntries, hasSize(2));

        assertThat(eventEntries.get(0).getStreamId(), is(streamId3.toString()));

        assertThat(eventEntries.get(0).getSequenceNumber(), is(3L));

        assertThat(eventEntries.get(1).getStreamId(), is(streamId2.toString()));

        assertThat(eventEntries.get(1).getSequenceNumber(), is(2L));
    }

    @Test
    public void shouldReturnNextEvents() throws Exception {


        final long pageSize = 2L;

        final UUID streamId5 = randomUUID();
        final UUID streamId4 = randomUUID();

        final Stream.Builder<EventStream> eventStreamBuilder = Stream.builder();

        final EventStream eventStream5 = new EventStream(streamId5, 5L);
        final EventStream eventStream4 = new EventStream(streamId4, 4L);

        eventStreamBuilder.add(eventStream5);
        eventStreamBuilder.add(eventStream4);

        final long sequenceId = 3L;

        when(repository.forward(sequenceId, 2L)).thenReturn(eventStreamBuilder.build());

        final List<EventStreamEntry> eventEntries = service.eventStream(sequence(sequenceId), FORWARD, pageSize);

        assertThat(eventEntries, hasSize(2));

        assertThat(eventEntries.get(0).getStreamId(), is(streamId5.toString()));

        assertThat(eventEntries.get(0).getSequenceNumber(), is(5L));

        assertThat(eventEntries.get(1).getStreamId(), is(streamId4.toString()));

        assertThat(eventEntries.get(1).getSequenceNumber(), is(4L));
    }

    @Test
    public void shouldReturnRecordExists() throws Exception {
        final long sequenceId = 3L;
        when(repository.recordExists(sequenceId)).thenReturn(true);

        assertThat(service.recordExists( sequenceId),is(true));
    }
}