package com.horrorsoft.viotimer;

import com.horrorsoft.viotimer.bluetooth.BlueToothWriter;
import junit.framework.TestCase;
import com.horrorsoft.viotimer.bluetooth.TimerProtocol;

/**
 * Created by Admin on 03.04.2014.
 */
public class StartActivity__TestTest extends TestCase {
    public void testTestNumeric() throws Exception {

        TimerProtocol timerProtocol = new TimerProtocol();

        byte[] array = new byte[256];
        for (int x = 0; x < array.length; ++x) {
            array[x] = (byte) (x + 4);
        }
        byte crc = timerProtocol.calculateCrc8(array, -1);
        assertEquals(15, crc);

        short crc16 = timerProtocol.calculateCrc16(array, -1);
        assertEquals(33580, (int)(crc16 & 0xffff));

        for (int x = 0; x < array.length; ++x) {
            array[x] = 0;
        }

        crc16 = timerProtocol.calculateCrc16(array, 0x20);
        assertEquals(61772, (int)(crc16 & 0xffff));



    }
}
