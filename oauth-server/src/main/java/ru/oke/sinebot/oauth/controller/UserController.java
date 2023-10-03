package ru.oke.sinebot.oauth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.oke.sinebot.oauth.dto.UserRequestDto;
import ru.oke.sinebot.oauth.exception.AlreadyExistsException;
import ru.oke.sinebot.oauth.service.UserRegistrationService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRegistrationService userRegistrationService;

    @GetMapping("/user/login")
    public String getLoginPage(Model model) {
        return "custom-login";
    }

    @GetMapping("/user/registration")
    public String getRegisterPage(Model model) {
        if (!model.containsAttribute("user")) {
            UserRequestDto userDto = new UserRequestDto();
            model.addAttribute("user", userDto);
        }
        return "registration";
    }

    @PostMapping("/user/registration")
    public String createUser(@Valid @ModelAttribute("user") UserRequestDto user, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        System.out.println(user);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/user/registration";
        }
        try {
            this.userRegistrationService.registerUser(user);
            return "redirect:/user/login";
        } catch (AlreadyExistsException e) {
            bindingResult.addError(new FieldError("user", "login", e.getMessage()));
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/user/registration";
        }
    }


}
