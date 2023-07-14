package by.katz;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputButton;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static by.katz.Utils.log;

public class DeviceController {
    private static final int MAX_AXIS_BYTE_VALUE = 255;
    private static final int MAX_AXIS_SHORT_VALUE = 32767;
    private final XInputDevice14[] devices;
    private final Robot robot;

    public DeviceController(XInputDevice14[] devices, Robot robot) {
        this.devices = devices;
        this.robot = robot;
    }

    public void mainLoop() throws InterruptedException {
        do {
            Arrays.stream(devices)
                  .filter(XInputDevice::isConnected)
                  .forEach(d -> {
                      d.poll();
                      processMouseMove(d, robot);
                      processMouseWheel(d, robot);
                      if (processTriggers(d))
                          Toolkit.getDefaultToolkit().beep();
                  });
            TimeUnit.MILLISECONDS.sleep(5);
        } while (Arrays.stream(devices).anyMatch(XInputDevice14::isConnected));
    }

    private boolean processTriggers(XInputDevice14 d) {
        return d.getComponents().getAxes().rtRaw == MAX_AXIS_BYTE_VALUE
              && d.getComponents().getAxes().ltRaw == MAX_AXIS_BYTE_VALUE;
    }

    private void processMouseWheel(XInputDevice14 device, Robot robot) {
        var valueY = device.getComponents().getAxes().ryRaw;
        if (valueY == 0)
            return;
        if (valueY == MAX_AXIS_SHORT_VALUE) {
            log("MOUSE WHEEL UP " + valueY);
            robot.mouseWheel(-1);
        } else if (valueY == -MAX_AXIS_SHORT_VALUE - 1) {
            log("MOUSE WHEEL DOWN " + valueY);
            robot.mouseWheel(1);
        }
    }

    private void processMouseMove(XInputDevice14 d, Robot robot) {
        var curPos = MouseInfo.getPointerInfo().getLocation();
        var curPosX = curPos.getX();
        var curPosY = curPos.getY();
        var axes = d.getComponents().getAxes();

        if (axes.lxRaw != 0)
            curPosX += Utils.getMovePoints(axes.lxRaw);
        if (axes.lyRaw != 0)
            curPosY += Utils.getMovePoints(-axes.lyRaw);
        if (axes.lxRaw == 0 && axes.lyRaw == 0)
            return;

        robot.mouseMove((int) curPosX, (int) curPosY);
        log(axes.lxRaw + " " + axes.lyRaw);
    }

    public void releaseKeys(HashMap<XInputButton, Integer> keys) {
        keys.forEach((k, v) -> robot.keyRelease(v));
    }
}
