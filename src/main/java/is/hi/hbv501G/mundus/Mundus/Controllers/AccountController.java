package is.hi.hbv501G.mundus.Mundus.Controllers;

import is.hi.hbv501G.mundus.Mundus.Entities.Account;
import is.hi.hbv501G.mundus.Mundus.Entities.Parent;
import is.hi.hbv501G.mundus.Mundus.Entities.Person;
import is.hi.hbv501G.mundus.Mundus.Services.AccountService;
import is.hi.hbv501G.mundus.Mundus.Services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;


@Controller
public class AccountController {

    private AccountService accountService;
    private PersonService personService;

    @Autowired
    public AccountController(AccountService accountService, PersonService personService) {
        this.accountService = accountService;
        this.personService = personService;
    }

    /**
     * A POST method for logging in.
     *
     * @param email
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPost(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session) {
        long accountId;

        try {
            accountId = accountService.login(email, password);
            session.setAttribute("AccountIdLoggedIn", accountId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    /**
     * A GET method for logging in
     *
     * @return the login page
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    /**
     * A method for logging out of the account
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/logoutAccount", method = RequestMethod.GET)
    public String logOutAccount(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /**
     * A method for logging out of a person
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/logoutPerson", method = RequestMethod.GET)
    public String logOutPerson(HttpSession session) {
        if (session.getAttribute("PersonIdLoggedIn") != null) {
            session.removeAttribute("PersonIdLoggedIn");
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUpPOST(@Valid Account account, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signUp";
        }

        try {
            accountService.createAccount(account, account.getParent());
        } catch (Exception e) {
            return "signUp";
        }

        return "redirect:/";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUpGET(Model model) {
        model.addAttribute("account", new Account());
        return "signUp";
    }
}
