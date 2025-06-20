package info.martinmarinov.drivers.usb.cxusb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import info.martinmarinov.drivers.DeviceFilter;
import info.martinmarinov.drivers.DvbException;
import info.martinmarinov.drivers.tools.SleepUtils;
import info.martinmarinov.drivers.usb.DvbFrontend;
import info.martinmarinov.drivers.usb.DvbTuner;
import info.martinmarinov.drivers.usb.DvbUsbIds;
import info.martinmarinov.drivers.usb.silabs.Si2157;
import info.martinmarinov.drivers.usb.silabs.Si2168;

import static info.martinmarinov.drivers.usb.silabs.Si2157.Type.SI2157_CHIPTYPE_SI2157;

class MygicaPT362 extends CxUsbDvbDevice {
    private final static String MYGICA_NAME = "Mygica PT362 DVB-T/T2 (Type-C)";
    final static DeviceFilter MYGICA_PT362 = new DeviceFilter(DvbUsbIds.USB_VID_CONEXANT, DvbUsbIds.USB_PID_MYGICA_PT362, MYGICA_NAME);

    private Si2168 frontend;

    MygicaPT362(UsbDevice usbDevice, Context context) throws DvbException {
        super(usbDevice, context, MYGICA_PT362);
    }

    @Override
    public String getDebugString() {
        return MYGICA_NAME;
    }

    @Override
    synchronized protected void powerControl(boolean onoff) throws DvbException {
        cxusb_d680_dmb_power_ctrl(onoff);
        cxusb_streaming_ctrl(onoff);
    }

    private void cxusb_d680_dmb_power_ctrl(boolean onoff) throws DvbException {
        cxusb_power_ctrl(onoff);
        if (!onoff) return;

        SleepUtils.mdelay(128);
        cxusb_ctrl_msg(CMD_DIGITAL, new byte[0], 0, new byte[1], 1);
        SleepUtils.mdelay(100);
    }

    @Override
    protected void readConfig() throws DvbException {
        // no-op for PT362
    }

    @Override
    protected DvbFrontend frontendAttatch() throws DvbException {
        // PT362 Type-C uses Si2168 demodulator with specific configuration
        return frontend = new Si2168(this, resources, i2CAdapter, 0x64, SI2168_TS_PARALLEL, true, SI2168_TS_CLK_AUTO_FIXED, false);
    }

    @Override
    protected DvbTuner tunerAttatch() throws DvbException {
        // PT362 Type-C uses Si2157 tuner
        return new Si2157(resources, i2CAdapter, frontend.gateControl(), 0x60, false, SI2157_CHIPTYPE_SI2141);
    }

    @Override
    protected void init() throws DvbException {
        // PT362 Type-C specific initialization if needed
        // no-op for now
    }
}
