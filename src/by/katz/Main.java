package by.katz;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;

import java.awt.*;
import java.util.Arrays;

import static by.katz.Utils.log;

public class Main {

    public static void main(String[] args)
          throws XInputNotLoadedException, AWTException, InterruptedException {
        var devices = XInputDevice14.getAllDevices();
        var connectedCount = Arrays.stream(devices)
              .filter(XInputDevice::isConnected)
              .count();
        log("Start app, devices connected: " + connectedCount);
        var robot = new Robot();
        var configControls = new ConfigControls();
        var mouseButtons = configControls.bindMouseButtons();
        var keyboardKeys = configControls.bindKeyboardKeys();
        configControls.initGamepadControls(devices, robot, mouseButtons, keyboardKeys);
        if (devices.length <= 0) {
            log("Devices not connected!");
            return;
        }
        var deviceController = new DeviceController(devices, robot);
        deviceController.mainLoop();
        deviceController.releaseKeys(keyboardKeys);
        log("Keys released");
        log("Exit...");
    }

}