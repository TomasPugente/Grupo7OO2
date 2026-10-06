package com.example.unla.grupo7.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.unla.grupo7.helper.ViewRouteHelper;
@Controller 
public class IndexController {
    
    @GetMapping("index")
    public String index(){
        return ViewRouteHelper.INDEX;
    }
}
