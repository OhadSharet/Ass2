package bgu.spl.mics;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import bgu.spl.mics.example.services.ExampleMicroservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBusTest {

    private MessageBusImpl messageBus;
    private ExampleBroadcastListenerService broadcastListenerService;
    private ExampleEventHandlerService eventHandlerService;
    private ExampleMessageSenderService messageSenderService;
    private ExampleBroadcast broadcast;
    private ExampleEvent event;
    private Future<String> future;

    @BeforeEach
    public void setUp(){
        messageBus = new MessageBusImpl();
        broadcastListenerService = new ExampleBroadcastListenerService("broadcastListenerService", new String[]{"1"});
        eventHandlerService = new ExampleEventHandlerService("eventHandlerService", new String[]{"1"});
        future = new Future<>();
    }

    //No need to test
    @Test
    public void testSubscribeEvent()
    {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.subscribeEvent(event.getClass(),null));
        messageBus.register(eventHandlerService);
        assertNull(messageBus.sendEvent(event));
        assertDoesNotThrow(() -> messageBus.subscribeEvent(event.getClass() ,eventHandlerService));
        messageBus.sendEvent(event);
    }

    //No need to test
    @Test
    public void testSubscribeBroadcast() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"broadcast"});
        broadcast = new ExampleBroadcast(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.subscribeBroadcast(broadcast.getClass() , null));
        messageBus.register(broadcastListenerService);
        assertDoesNotThrow(() -> messageBus.subscribeBroadcast(broadcast.getClass() ,broadcastListenerService));
        messageBus.sendBroadcast(broadcast);
    }

    @Test
    public void testComplete() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        future = messageBus.sendEvent(event);
        messageBus.complete(event, "completed");
        assertEquals(future.get(), "completed");
    }

    @Test
    public void testSendBroadcast() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"broadcast"});
        broadcast = new ExampleBroadcast(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.sendBroadcast(null));
        messageBus.register(broadcastListenerService);
        messageBus.subscribeBroadcast(ExampleBroadcast.class ,broadcastListenerService);
        messageBus.sendBroadcast(broadcast);
        assertDoesNotThrow(() -> messageBus.sendBroadcast(broadcast));
    }

    @Test
    public void testSendEvent() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.sendEvent(null));
        assertNull(messageBus.sendEvent(event));
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        assertNotNull(messageBus.sendEvent(event));
    }

    //No need to test
    @Test
    public void testRegister() {
        assertDoesNotThrow(() -> messageBus.register(null));
        messageBus.register(eventHandlerService);
        assertEquals(eventHandlerService.getName(), "EventMicroService");
        try {
            messageBus.unregister(eventHandlerService);
            assertEquals(eventHandlerService.getName(), "EventMicroService");
        }
        catch (IllegalStateException e) {
            fail("Microservice was registered before unregistered but still an exception was sent");
        }
    }

    //No need to test
    @Test
    public void testUnregister() {
        try {
            messageBus.unregister(null);
            messageBus.unregister(eventHandlerService);
            fail("Unregistered Microservice was received but no exception was sent");
        }
        catch (IllegalStateException e) {
            messageBus.register(eventHandlerService);
            assertEquals(eventHandlerService.getName(), "EventMicroService");
            messageBus.unregister(eventHandlerService);
            assertEquals(eventHandlerService.getName(), "EventMicroService");
        }
    }

    @Test
    public void testAwaitMessage() {
        try {
            messageBus.awaitMessage(null);
            fail("Null Microservice was received but no exception was sent");
        }
        catch (IllegalStateException | InterruptedException e1) {
            try {
                messageBus.awaitMessage(eventHandlerService);
                fail("Unregistered Microservice was received but no exception was sent");
            }
            catch (IllegalStateException | InterruptedException e2) {
                messageBus.register(eventHandlerService);
                try {
                    messageBus.awaitMessage(eventHandlerService);
                } catch (IllegalStateException | InterruptedException e3) {
                    fail("Microservice was registered but still an exception was thrown");
                }
            }
        }
    }
}