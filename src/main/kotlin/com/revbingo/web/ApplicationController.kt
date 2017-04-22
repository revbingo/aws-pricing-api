package com.revbingo.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/")
class ApplicationController {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun helloWorld(): ModelAndView {
        val mav = ModelAndView("index")
        mav.addObject("siteName", "")
        return mav
    }
}