package bgu.spl.mics;
import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EwokTest {

    private Ewok ewokObject;

    @BeforeEach
    public void setUp(){
        ewokObject = new Ewok();
    }

    @Test
    public void testAcquire()
    {
        int ewokSerialNumber = ewokObject.getSerialNumber();
        ewokObject.acquire();
        assertFalse(ewokObject.isAvailable());
        assertEquals(ewokObject.getSerialNumber(), ewokSerialNumber);
    }

    @Test
    public void testRelease()
    {
        int ewokSerialNumber = ewokObject.getSerialNumber();
        ewokObject.release();
        assertTrue(ewokObject.isAvailable());
        assertEquals(ewokObject.getSerialNumber(), ewokSerialNumber);
    }

}
