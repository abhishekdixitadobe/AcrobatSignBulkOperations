package com.adobe.acrobatsign.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.adobe.acrobatsign.model.AgreementForm;
import com.adobe.acrobatsign.model.ExportWidget;
import com.adobe.acrobatsign.model.MultiUserWidgetDetails;
import com.adobe.acrobatsign.model.UserWidget;
import com.adobe.acrobatsign.service.UserService;
import com.adobe.acrobatsign.service.WebformService;
import com.adobe.acrobatsign.util.Constants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Controller
public class WebformController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebformController.class);

	@Value("${pageSize}")
	public String maxLimit;

	/** The adobe sign service. */
	@Autowired
	WebformService webformService;

	@Autowired
	UserService userService;

	@RequestMapping(value = Constants.GET_AGREEMENTS_FOR_WIDGET, method = RequestMethod.GET)
	public String agreementsForWidget(Model model, @PathVariable String widgetId, @PathVariable String email) {

		AgreementForm agreementForm = webformService.agreementsForWebforms(widgetId, email);
		model.addAttribute(Constants.AGREEMENT_FORM, agreementForm);
		if (null != agreementForm.getAgreementIdList() && agreementForm.getAgreementIdList().size() > 0) {
			model.addAttribute(Constants.AGREEMENT_ID_LIST, agreementForm.getAgreementIdList());
			model.addAttribute(Constants.AGREEMENT_LIST, agreementForm.getAgreementIdList());
			model.addAttribute(Constants.TOTAL_AGREEMENTS, agreementForm.getAgreementIdList().size());
		}
		model.addAttribute(Constants.USER_EMAIL, email);
		return Constants.AGREEMENTS_FOR_WEBFORM_LIST;
	}

	@RequestMapping(value = Constants.GET_WIDGET, method = RequestMethod.GET)
	public String allWorkflows(Model model) {
		// List<DetailedUserInfo> activeUserList = userService.activeUsers();
		// model.addAttribute(Constants.ACTIVE_USER_LIST, activeUserList);

		return Constants.ACTIVE_USER_LIST;
	}

	@RequestMapping(value = Constants.DOWNLOAD_WEBFORMS, method = RequestMethod.POST, params = "downloadWebforms")
	public void downloadWebforms(HttpServletResponse response, @RequestParam String userEmail,
			@ModelAttribute("agreementForm") AgreementForm agreementForm) {

		String filename = "WidgetList.csv";
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		try {
			StatefulBeanToCsv<ExportWidget> writer = new StatefulBeanToCsvBuilder<ExportWidget>(response.getWriter())
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withOrderedResults(false).build();

			writer.write(seletedList(agreementForm));
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			e.printStackTrace();
		}
	}

	private List<ExportWidget> seletedList(AgreementForm agreementForm) {
		final List<ExportWidget> seletedList = new ArrayList<>();
		for (final UserWidget widget : agreementForm.getWidgetList()) {
			if (widget.getIsChecked() != null) {
				ExportWidget exportWidget = new ExportWidget();
				exportWidget.setId(widget.getId());
				exportWidget.setName(widget.getName());
				exportWidget.setOwnerEmail(widget.getOwnerEmail());
				exportWidget.setStatus(widget.getStatus());
				exportWidget.setUrl(widget.getUrl());
				seletedList.add(exportWidget);
			}
		}
		return seletedList;
	}

	@RequestMapping(value = Constants.GET_USERS_WIDGET, method = RequestMethod.POST)
	public String usersWidget(Model model, @RequestParam(Constants.PARAM_FILE) MultipartFile file1) {
		AgreementForm agreementForm = new AgreementForm();
		List<String> activeUserList = new ArrayList<>();

		if (!file1.isEmpty()) {
			final byte[] bytes;
			try {
				final InputStream inputStream = file1.getInputStream();
				final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				activeUserList = br.lines().collect(Collectors.toList());
				activeUserList.remove(0);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		MultiUserWidgetDetails multiUserWidgetDetails = webformService.getWebforms(activeUserList);
		model.addAttribute(Constants.WIDGET_LIST, multiUserWidgetDetails.getWidgetList());
		model.addAttribute(Constants.ACTIVE_USER_LIST, multiUserWidgetDetails.getUserEmails());
		model.addAttribute(Constants.AGREEMENT_FORM, agreementForm);
		return "webformList";
	}

}
