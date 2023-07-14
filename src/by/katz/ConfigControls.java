package by.katz;

import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputBatteryDeviceType;
import com.github.strikerx3.jxinput.enums.XInputButton;
import com.github.strikerx3.jxinput.listener.XInputDeviceListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;

import static by.katz.Utils.log;


public class ConfigControls {
    HashMap<XInputButton, Integer> bindKeyboardKeys() {
        var keys = new HashMap<XInputButton, Integer>();
        keys.put(XInputButton.DPAD_DOWN, KeyEvent.VK_DOWN);
        keys.put(XInputButton.DPAD_UP, KeyEvent.VK_UP);
        keys.put(XInputButton.DPAD_LEFT, KeyEvent.VK_LEFT);
        keys.put(XInputButton.DPAD_RIGHT, KeyEvent.VK_RIGHT);
        keys.put(XInputButton.A, KeyEvent.VK_A);
        keys.put(XInputButton.B, KeyEvent.VK_B);
        keys.put(XInputButton.X, KeyEvent.VK_X);
        keys.put(XInputButton.Y, KeyEvent.VK_Y);
        keys.put(XInputButton.BACK, KeyEvent.VK_ESCAPE);
        keys.put(XInputButton.START, KeyEvent.VK_ENTER);
        keys.put(XInputButton.LEFT_THUMBSTICK, KeyEvent.VK_SPACE);
        keys.put(XInputButton.RIGHT_THUMBSTICK, KeyEvent.VK_BACK_SPACE);
        return keys;
    }

    public HashMap<XInputButton, Integer> bindMouseButtons() {
        var mouseButtons = new HashMap<XInputButton, Integer>();
        mouseButtons.put(XInputButton.LEFT_SHOULDER, InputEvent.BUTTON1_DOWN_MASK);
        mouseButtons.put(XInputButton.RIGHT_SHOULDER, InputEvent.BUTTON3_DOWN_MASK);
        return mouseButtons;
    }

    public void initGamepadControls(XInputDevice14[] devices, Robot robot, HashMap<XInputButton, Integer> mouseButtons, HashMap<XInputButton, Integer> keys) {
        Arrays.stream(devices).forEach(gamepad -> gamepad.addListener(new XInputDeviceListener() {
            @Override public void connected() {
                var battInfo = gamepad.getBatteryInformation(XInputBatteryDeviceType.GAMEPAD);
                log("add listener for " + gamepad.getPlayerNum() + (battInfo == null ? "" : " battery: " + battInfo.getLevel()));
                log("Gamepad" + gamepad.getPlayerNum() + " connected");
            }

            @Override public void disconnected() {
                log("Gamepad" + gamepad.getPlayerNum() + " disconnected");
            }

            @Override public void buttonChanged(XInputButton gamepadButton, boolean isPressed) {
                log("Gamepad" + gamepad.getPlayerNum() + " change " + gamepadButton.name() + " " + (isPressed ? "+" : "-"));
                keys.entrySet().stream()
                      .filter(e -> e.getKey().equals(gamepadButton))
                      .forEach(e -> {
                          if (isPressed)
                              robot.keyPress(e.getValue());
                          else robot.keyRelease(e.getValue());
                      });
                mouseButtons.entrySet().stream()
                      .filter(e -> e.getKey().equals(gamepadButton))
                      .forEach(e -> {
                          if (isPressed)
                              robot.mousePress(e.getValue());
                          else robot.mouseRelease(e.getValue());
                      });
            }
        }));
    }
}
