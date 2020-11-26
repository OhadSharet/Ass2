package bgu.spl.mics.example.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;

import java.util.concurrent.TimeUnit;

public class ExampleMicroservice extends MicroService {

    private int mbt;
    private final boolean event;
    private final boolean broadcast;
    private final boolean sender;
    private final boolean receiver;
    private boolean testResult = false;

    public ExampleMicroservice(String name, String[] args) {
        super(name);

        if (args.length != 3 || !args[0].matches("broadcast|event") || !args[1].matches("sender|receiver")) {
            throw new IllegalArgumentException("Microservice expecting to get: type of message to handle, is sender or receiver, mbt");
        }
        try {
            mbt = Integer.parseInt(args[2]);
            if (mbt <= 0) {
                    throw new IllegalArgumentException("expecting the argument mbt to be a number > 0, instead received: " + args[0]);
            }
            else {
                this.event = args[0].equals("event");
                this.broadcast = args[0].equals("broadcast");
                this.sender = args[1].equals("sender");
                this.receiver = args[1].equals("receiver");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Microservice expecting to get: mbt > 0 but instead received: " + args[2]);
        }
    }

    @Override
    protected void initialize() {
        System.out.println("Microservice " + getName() + " started");
        if (broadcast && sender) {
            //Send broadcast
            sendBroadcast(new ExampleBroadcast(getName()));
            System.out.println("Microservice " + getName() + " publish an event and terminate");
            testResult = true;
            terminate();
        } else if (event && sender) {
            //Send event
            Future<String> futureObject = (Future<String>) sendEvent(new ExampleEvent(getName()));
            if (futureObject != null) {
                String resolved = futureObject.get(100, TimeUnit.MILLISECONDS);
                if (resolved != null) {
                    System.out.println("Completed processing the event, its result is \"" + resolved + "\" - success");
                    testResult = true;
                } else {
                    System.out.println("Time has elapsed, no services has resolved the event - terminating");
                }
            } else {
                System.out.println("No Micro-Service has registered to handle ExampleEvent events! The event cannot be processed");
            }
            terminate();
        } else if (event && receiver) {
            //Receive event
            subscribeEvent(ExampleEvent.class, ev -> {
                mbt--;
                System.out.println("Microservice " + getName() + " got a new event from " + ev.getSenderName() + "! (mbt: " + mbt + ")");
                complete(ev, "Hello from " + getName());
                if (mbt == 0) {
                    System.out.println("Microservice " + getName() + " terminating.");
                    testResult = true;
                    terminate();
                }
            });
        }
        else if (broadcast && receiver) {
            //Receive broadcast
            subscribeBroadcast(ExampleBroadcast.class, message -> {
                mbt--;
                System.out.println("Microservice " + getName() + " got a new message from " + message.getSenderId() + "! (mbt: " + mbt + ")");
                if (mbt == 0) {
                    System.out.println("Microservice " + getName() + " terminating.");
                    testResult = true;
                    terminate();
                }
            });
        }
        else {
            //No task was found
            System.out.println("Microservice " + getName() + " found no task to do");
            terminate();
        }
    }

    public boolean getTestResult() {
        return testResult;
    }
}