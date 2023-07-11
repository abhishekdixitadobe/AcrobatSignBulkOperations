package com.adobe.acrobatsign.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.adobe.acrobatsign.model.LibraryDocuments;
import com.adobe.acrobatsign.service.LibraryTemplateService;
import com.adobe.acrobatsign.util.Constants;

@Controller
public class LibraryTemplateController {
	
	/** The adobe sign service. */
	@Autowired
	LibraryTemplateService libraryTemplateService;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryTemplateController.class);

	@Value("${pageSize}")
	public String maxLimit;
	
	@RequestMapping(value = Constants.GET_LIBRARY_TEMPLATE, method = RequestMethod.GET)
	public String getLibraryTemplate(Model model) {
		final List<LibraryDocuments> libraryDocuments = libraryTemplateService.fetchLibraryTemplate();
		model.addAttribute("libraryTemplate", libraryDocuments);
		return "libraryTemplate";
	}

}
