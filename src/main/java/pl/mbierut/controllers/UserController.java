package pl.mbierut.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.mbierut.exceptions.UserAlreadyExistsException;
import pl.mbierut.models.enums.Currency;
import pl.mbierut.models.requests.UserRegistrationRequest;
import pl.mbierut.services.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }


    @GetMapping("/")
    public String sendHome(Model model) {
        List<String[]> listOfCurrencies = new ArrayList<>();
        for (int i = 0; i < Currency.values().length; i++) {
            if (Currency.values()[i].equals(Currency.PLN)){
                continue;
            }
            listOfCurrencies.add(Currency.values()[i].getCurrencyAndRates());
        }
        model.addAttribute("listOfCurrencies", listOfCurrencies);

        return "index";
    }

    @GetMapping("/register")
    public String goToRegistration() {
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@RequestParam(name = "userName") String userName,
                                  @RequestParam(name = "email") String email,
                                  @RequestParam(name = "password") String password) {

        UserRegistrationRequest request = new UserRegistrationRequest(userName, email, password);
        try {
            service.registerNewUser(request);
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping("/wallet")
    public String showWallet(Model model) {
        model.addAttribute("email", "");
        return "wallet";
    }

    @PostMapping("/wallet")
    public String showWalletForEmail(Model model, @RequestParam(name = "email") String email) {
        String wallet = service.showWallet(email);
        model.addAttribute("wallet", wallet);
        return "wallet";
    }
}
