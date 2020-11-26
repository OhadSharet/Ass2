package bgu.spl.mics;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;

public class OldMessageBusTest {

    private MessageBusImpl messageBusObject;

    @BeforeEach
    public void setUp(){
        messageBusObject = new MessageBusImpl();
    }

    @Test
    public void testSubscribeEvent()
    {
        assertDoesNotThrow(() -> messageBusObject.sendEvent(null));
        String[] exampleEventReceiverList = new String[]{"1"};
        String[] exampleEventSenderList = new String[]{"event"};
        ExampleEventHandlerService exampleMicroServiceObject = new ExampleEventHandlerService("EventMicroService", exampleEventReceiverList);
        ExampleMessageSenderService exampleMicroServiceSenderObject = new ExampleMessageSenderService("EventMicroServiceSender", exampleEventSenderList);
        //ExampleEvent exampleEventObject = new ExampleEvent(exampleMicroServiceSenderObject.getName());
        //assertEquals(exampleMicroServiceSenderObject.getName(), "EventMicroServiceSender");
        messageBusObject.register(exampleMicroServiceObject);
        //assertEquals(exampleMicroServiceObject.getName(), "EventMicroService");
        messageBusObject.subscribeEvent(ExampleEvent.class ,exampleMicroServiceObject);
        assertEquals(exampleMicroServiceObject.getName(), "EventMicroService");
        //messageBusObject.sendEvent(exampleEventObject);
        exampleMicroServiceSenderObject.run();
        exampleMicroServiceObject.run();
        //How to check that event received?
    }

    @Test
    public void testSubscribeBroadcast() {
        assertDoesNotThrow(() -> messageBusObject.sendBroadcast(null));
        String[] exampleBroadcastReceiverList = new String[]{"1"};
        String[] exampleBroadcastSenderList = new String[]{"broadcast"};
        ExampleBroadcastListenerService exampleMicroServiceObject = new ExampleBroadcastListenerService("BroadcastMicroService", exampleBroadcastReceiverList);
        ExampleMessageSenderService exampleMicroServiceSenderObject = new ExampleMessageSenderService("BroadcastMicroServiceSender", exampleBroadcastSenderList);
        //ExampleBroadcast exampleBroadObject = new ExampleBroadcast(exampleMicroServiceSenderObject.getName());
        //assertEquals(exampleMicroServiceSenderObject.getName(), "EventMicroServiceSender");
        messageBusObject.register(exampleMicroServiceObject);
        //assertEquals(exampleMicroServiceObject.getName(), "BroadcastMicroService");
        messageBusObject.subscribeBroadcast(ExampleBroadcast.class ,exampleMicroServiceObject);
        assertEquals(exampleMicroServiceObject.getName(), "BroadcastMicroService");
        //messageBusObject.sendBroadcast(exampleBroadObject);
        exampleMicroServiceSenderObject.run();
        exampleMicroServiceObject.run();
        //How to check that broadcast received?
    }

    @Test
    public void testComplete() {

    }

    @Test
    public void testSendBroadcast() {
        assertDoesNotThrow(() -> messageBusObject.sendBroadcast(null));
        String[] exampleEventReceiverList = new String[]{"1"};
        String[] exampleEventSenderList = new String[]{"broadcast"};
        ExampleBroadcastListenerService exampleMicroServiceObject = new ExampleBroadcastListenerService("EventMicroService", exampleEventReceiverList);
        ExampleMessageSenderService exampleMicroServiceSenderObject = new ExampleMessageSenderService("EventMicroServiceSender", exampleEventSenderList);
        ExampleBroadcast exampleBroadcastObject = new ExampleBroadcast(exampleMicroServiceSenderObject.getName());
        messageBusObject.register(exampleMicroServiceObject);
        messageBusObject.subscribeBroadcast(ExampleBroadcast.class ,exampleMicroServiceObject);
        messageBusObject.sendBroadcast(exampleBroadcastObject);

        exampleMicroServiceSenderObject.run();
        exampleMicroServiceObject.run();
    }

    @Test
    public void testSendEvent() {
        assertDoesNotThrow(() -> messageBusObject.sendEvent(null));
        String[] exampleEventReceiverList = new String[]{"1"};
        String[] exampleEventSenderList = new String[]{"event"};
        ExampleEventHandlerService exampleMicroServiceObject = new ExampleEventHandlerService("EventMicroService", exampleEventReceiverList);
        ExampleMessageSenderService exampleMicroServiceSenderObject = new ExampleMessageSenderService("EventMicroServiceSender", exampleEventSenderList);
        ExampleEvent exampleEventObject = new ExampleEvent(exampleMicroServiceSenderObject.getName());
        messageBusObject.register(exampleMicroServiceObject);
        messageBusObject.subscribeEvent(ExampleEvent.class ,exampleMicroServiceObject);
        messageBusObject.sendEvent(exampleEventObject);

        exampleMicroServiceSenderObject.run();
        exampleMicroServiceObject.run();
    }

    @Test
    public void testRegister() {
        assertDoesNotThrow(() -> messageBusObject.register(null));
        String[] exampleEventReceiverList = new String[]{"1"};
        ExampleEventHandlerService exampleMicroServiceObject = new ExampleEventHandlerService("EventMicroService", exampleEventReceiverList);
        messageBusObject.register(exampleMicroServiceObject);
        assertEquals(exampleMicroServiceObject.getName(), "EventMicroService");
        messageBusObject.unregister(exampleMicroServiceObject);
        assertEquals(exampleMicroServiceObject.getName(), "EventMicroService");
    }

    //No need to test
    @Test
    public void testUnregister() {
        String[] exampleEventReceiverList = new String[]{"1"};
        ExampleEventHandlerService exampleMicroServiceObject = new ExampleEventHandlerService("EventMicroService", exampleEventReceiverList);
        try {
            messageBusObject.unregister(null);
            messageBusObject.unregister(exampleMicroServiceObject);
            fail("Unregistered Microservice was received but no exception was sent");
        }
        catch (IllegalStateException e) {
            messageBusObject.register(exampleMicroServiceObject);
            assertEquals(exampleMicroServiceObject.getName(), "EventMicroService");
            messageBusObject.unregister(exampleMicroServiceObject);
            assertEquals(exampleMicroServiceObject.getName(), "EventMicroService");
        }
    }

    @Test
    public void testAwaitMessage() {
        String[] exampleEventReceiverList = new String[]{"1"};
        ExampleEventHandlerService exampleMicroServiceObject = new ExampleEventHandlerService("EventMicroService", exampleEventReceiverList);
        try {
            messageBusObject.awaitMessage(null);
            messageBusObject.awaitMessage(exampleMicroServiceObject);
            fail("Unregistered Microservice was received but no exception was sent");
        }
        catch (IllegalStateException | InterruptedException e1) {
            messageBusObject.register(exampleMicroServiceObject);
            try {
                messageBusObject.awaitMessage(exampleMicroServiceObject);
            }
            catch (IllegalStateException | InterruptedException e2) {

                fail("Microservice was registered but still an exception was thrown");
            }
        }
    }
}