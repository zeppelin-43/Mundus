package is.hi.hbv501G.mundus.Mundus.Controllers;

import is.hi.hbv501G.mundus.Mundus.Entities.*;
import is.hi.hbv501G.mundus.Mundus.Services.AccountService;
import is.hi.hbv501G.mundus.Mundus.Services.PersonService;
import is.hi.hbv501G.mundus.Mundus.Services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
public class PersonController {

    private PersonService personService;
    private AccountService accountService;
    private QuestService questService;

    @Autowired
    public PersonController(PersonService personService, AccountService accountService, QuestService questService) {
        this.personService = personService;
        this.accountService = accountService;
        this.questService = questService;
    }

    /**
     * When an account has been logged into you will be redirected to the person page where you can select a person to log into
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/persons", method = RequestMethod.GET)
    public String loadPersons(Model model, HttpSession session) {
        if (session.getAttribute("AccountIdLoggedIn") == null) {
            return "redirect:/"; // This page is not accessible unless logged into an account
        }

        long accountId = (long) session.getAttribute("AccountIdLoggedIn"); // Get the id of the person logged in
        Account account = accountService.findAccountById(accountId); // The account which is logged in
        Parent parent = account.getParent(); // Get the parent of the account
        Set<Child> children = parent.getChildren(); // Get the children of this acount

        model.addAttribute("parent", parent); // Add all the children and parent to the model
        model.addAttribute("children", children);
        return "userSelect";
        //return "persons"; // Load the person page

    }

    /**
     * When a person has been selected you must enter a pin
     *
     * @param id
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/pin-page", method = RequestMethod.POST)
    public String loadPinPage(@RequestParam("id") long id, Model model, HttpSession session) {
        session.setAttribute("PersonIdLoggedIn", null);
        Person person = personService.findPersonById(id);

        model.addAttribute("person", person);
        model.addAttribute("id", id);
        return "pinPage";
    }

    /**
     * A method that authenticates a pin for a particular person
     *
     * @param id
     * @param pin
     * @param session
     * @return
     */
    @RequestMapping(value = "/pin-page-auth", method = RequestMethod.POST)
    public String authenticatePinPost(@RequestParam("id") long id, @RequestParam("pin") String pin, HttpSession session) {

        long personId;

        try {
            personId = personService.authenticatePin(id, pin); // Try to authenticate the pin
            session.setAttribute("PersonIdLoggedIn", personId); // Then add the id to the session
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    /**
     * TODO færa kóða yfir í service
     * Loads all the information of a person
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/quests", method = RequestMethod.GET)
    public String loadPerson(Model model, HttpSession session) {
        try {
            questService.deleteExpired();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Couldn't delete expired quests");
        }
        if (session.getAttribute("PersonIdLoggedIn") == null) {
            return "redirect:/";
        }
        long personId = (long) session.getAttribute("PersonIdLoggedIn"); // Get the id of the person
        Person person = personService.findPersonById(personId); // Find the person

        if (person instanceof Child) { // If the person is a child it will se the quest page from the viewpoint of a child
            Child child = personService.findChildById(personId);
            Parent parent = child.getParent();
            Set<Quest> availableQuests = parent.getQuests();
            Set<Quest> allAssignedQuests = new HashSet<Quest>();
            Set<Child> allChildren = parent.getChildren();
            for (Child c : allChildren) {
                for (Quest q : c.getQuests()) {
                    allAssignedQuests.add(q);
                }
            }


            Set<Quest> assignedQuests = child.getQuests();
            availableQuests.removeAll(allAssignedQuests);
            Set<Quest> doneQuests = new HashSet<Quest>();
            Set<Quest> finishedQuests = new HashSet<Quest>(); // Don't show quests that are finished
            for (Quest q : assignedQuests) {
                if (q.getIsDone() && !q.getIsConfirmed()) {
                    doneQuests.add(q);
                }
                if (q.getIsConfirmed()) {
                    finishedQuests.add(q);
                }
            }
            assignedQuests.removeAll(doneQuests);
            assignedQuests.removeAll(finishedQuests);
            model.addAttribute("child", child);
            model.addAttribute("assignedQuests", assignedQuests); // Add the quests of the child to the model
            model.addAttribute("availableQuests", availableQuests);
            model.addAttribute("doneQuests", doneQuests);
            return "questViewChild2";
        } else if (person instanceof Parent) {
            Parent parent = personService.findParentById(personId);
            Set<Quest> availableQuests = parent.getQuests();
            Set<Quest> onGoingQuests = new HashSet<Quest>();
            Set<Child> children = parent.getChildren();
            if (children.isEmpty()) {
                return "redirect:/champions";
            }

            for (Child child : children) {
                onGoingQuests.addAll(child.getQuests());
            }
            Set<Quest> doneQuests = new HashSet<Quest>();
            Set<Quest> finishedQuests = new HashSet<Quest>();
            for (Quest q : onGoingQuests) {
                if (q.getIsDone() && !q.getIsConfirmed()) {
                    doneQuests.add(q);
                }
                if (q.getIsConfirmed()) {
                    finishedQuests.add(q);
                }
            }
            availableQuests.removeAll(onGoingQuests);
            onGoingQuests.removeAll(doneQuests);
            onGoingQuests.removeAll(finishedQuests);
            model.addAttribute("parent", parent);
            model.addAttribute("availableQuests", availableQuests);
            model.addAttribute("onGoingQuests", onGoingQuests);
            model.addAttribute("doneQuests", doneQuests);
            return "questViewParent2";
        } else {
            return "redirect:/";
        }

    }

    /**
     * Assign a child to a parent
     *
     * @param id    of the parent
     * @param model
     * @param child
     * @return
     */
    @RequestMapping(value = "/assignChildToParent", method = RequestMethod.POST)
    public String addChild(@RequestParam("id") long id, Model model, Child child) {
        try {
            personService.assignChildToParent(child, id);
        } catch (Exception e) {
            System.out.println("Not able to assign child to parent");
        }
        return "redirect:/profile";
    }

    @RequestMapping(value = "/createChild", method = RequestMethod.POST)
    public String createChildPOST(@Valid Child child, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            long personId = (long) session.getAttribute("PersonIdLoggedIn");
            Person person = personService.findPersonById(personId);
            Parent parent = (Parent) person;
            model.addAttribute("children", parent.getChildren());
            return "createUser";
        }
        long parentId = (long) session.getAttribute("PersonIdLoggedIn"); // Get the id of the parent creating this reward
        //Parent maker = personService.findParentById(parentId);
        //reward.setMaker(maker);
        try {
            personService.assignChildToParent(child, parentId); // Create a new reward
        } catch (Exception e) {
            return "redirect:/createUser";
        }
        return "redirect:/quests";
    }

    @RequestMapping(value = "/champions", method = RequestMethod.GET)
    public String createChildGET(Child child, HttpSession session, Model model) {
        if (session.getAttribute("PersonIdLoggedIn") != null) {
            long personId = (long) session.getAttribute("PersonIdLoggedIn");
            Person person = personService.findPersonById(personId);

            if (person instanceof Child) {
                return "redirect:/";
            } else {
                Parent parent = (Parent) person;
                model.addAttribute("children", parent.getChildren());
                return "createUser";
            }
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/removeChild", method = RequestMethod.POST)
    public String removeChild(long childId, HttpSession session, Model model) {
        if (session.getAttribute("PersonIdLoggedIn") != null) {
            long personId = (long) session.getAttribute("PersonIdLoggedIn");
            Person person = personService.findPersonById(personId);

            if (person instanceof Child) {
                return "redirect:/";
            } else {
                Parent parent = (Parent) person;
                Child child = personService.findChildById(childId);
                personService.delete(child);
                return "redirect:/champions";
            }
        }
        return "redirect:/";
    }


}
