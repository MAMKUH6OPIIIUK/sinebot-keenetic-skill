package ru.oke.sinebot.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DevicePageController {
    @GetMapping("/")
    public String defaultRedirect() {
        return "redirect:/device";
    }

    @GetMapping("/device")
    public String listDevicePage(Model model) {
        return "devices";
    }

    @GetMapping("/device/{id}")
    public String deviceInfoPage(@PathVariable Long id, Model model) {
        model.addAttribute("deviceId", id);
        return "device-info";
    }

    @GetMapping("/device/create")
    public String createDevicePage(Model model) {
        return "edit-device";
    }

    @GetMapping("/device/edit/{id}")
    public String editDevicePage(@PathVariable Long id, Model model) {
        model.addAttribute("deviceId", id);
        return "edit-device";
    }
}
