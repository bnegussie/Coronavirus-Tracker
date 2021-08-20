package io.javabrains.coronavirustracker.controllers;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.javabrains.coronavirustracker.services.CoronavirusDataService;

@Controller
public class HomeController {

    @Autowired
    CoronavirusDataService covidDataService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("coronavirusData", covidDataService.getCountryCovidData());

        return "home";
    }
}
