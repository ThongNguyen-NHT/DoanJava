package com.example.DoanJava.controller;

import com.example.DoanJava.model.User;
import com.example.DoanJava.repository.UserRepository;
import com.example.DoanJava.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return "redirect:/login";
        
        model.addAttribute("user", user);
        if (!model.containsAttribute("changePasswordRequest")) {
            model.addAttribute("changePasswordRequest", new com.example.DoanJava.util.ChangePasswordRequest());
        }
        return "auth/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user, 
                                BindingResult result, 
                                RedirectAttributes ra,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("changePasswordRequest", new com.example.DoanJava.util.ChangePasswordRequest());
            return "auth/profile";
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User existingUser = userRepository.findByUsername(username).orElseThrow();
        
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());
        
        userRepository.save(existingUser);
        ra.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") com.example.DoanJava.util.ChangePasswordRequest request,
                                 BindingResult result,
                                 RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest", result);
            ra.addFlashAttribute("changePasswordRequest", request);
            ra.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin đổi mật khẩu.");
            return "redirect:/profile#changePasswordSection";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            ra.addFlashAttribute("error", "Mật khẩu cũ không chính xác.");
            return "redirect:/profile#changePasswordSection";
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            ra.addFlashAttribute("error", "Xác nhận mật khẩu mới không khớp.");
            return "redirect:/profile#changePasswordSection";
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        ra.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}
