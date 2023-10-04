package com.adobe.acrobatsign.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.adobe.acrobatsign.model.Conversation;
import com.adobe.acrobatsign.model.ConversationQuery;
import com.adobe.acrobatsign.model.UserConversation;
import com.adobe.acrobatsign.service.ChatBotService;
import com.adobe.acrobatsign.service.CustomUserDetails;
import com.adobe.acrobatsign.util.Constants;
import com.adobe.pdfservices.operation.ExecutionContext;
import com.adobe.pdfservices.operation.auth.Credentials;
import com.adobe.pdfservices.operation.exception.ServiceApiException;
import com.adobe.pdfservices.operation.exception.ServiceUsageException;
import com.adobe.pdfservices.operation.io.FileRef;
import com.adobe.pdfservices.operation.pdfops.DocumentMergeOperation;
import com.adobe.pdfservices.operation.pdfops.options.documentmerge.DocumentMergeOptions;
import com.adobe.pdfservices.operation.pdfops.options.documentmerge.OutputFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class StatefullChatController {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(StatefullChatController.class);
	
	@Autowired
	ChatBotService chatService; 
	
	@Value("${DocGenAPI}")
	private String docGenAPI;
	
	@Value("${pdfservices.client_id}")
	private String clientId;
	
	@Value("${pdfservices.client_secret}")
	private String clientSecret;
	
	String input_file = "./sample.docx";

	String output_file = "./generatedReceipt.pdf";
	
	@RequestMapping(value = Constants.GET_CONVERSATION, method = RequestMethod.GET)
	public List<ConversationQuery> conversationQueries(Model model, @ModelAttribute String queries , HttpServletResponse response, @PathVariable Long conversations) {
		LOGGER.info("conversation--"+conversations);
		List<ConversationQuery> queryList =  chatService.getQueries(conversations);
		ObjectMapper mapper = new ObjectMapper();
		try {
			queries = mapper.writeValueAsString(queryList);
			model.addAttribute("queries", mapper.writeValueAsString(queries));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}
	
	 //Generates a string containing a directory structure and file name for the output file.
    public static String createOutputFilePath(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String timeStamp = dateTimeFormatter.format(now);
        return("output/FAQ-" + timeStamp + ".pdf");
    }
    
	@RequestMapping(value = Constants.GET_PDF, method = RequestMethod.GET)
	public ResponseEntity<StreamingResponseBody> generatePDF(Model model, @ModelAttribute String queries , HttpServletResponse response, @PathVariable Long conversations) {
		LOGGER.info("conversation--"+conversations);
		List<ConversationQuery> queryList =  chatService.getQueries(conversations);
		ObjectMapper mapper = new ObjectMapper();
		ResponseEntity<StreamingResponseBody> body = null;
		try {
			
			
			  JSONObject object = new JSONObject(); 
			  object.put("converasations", queryList); 
			  //queries = mapper.writeValueAsString(queryList); 
			 // object.put("converasations", queryList);
			  //JSONObject finalObj = new JSONObject(queries);
			 
			
			  //Path jsonPath = Paths.get("./sample.json");
			  
			  //String json = new String(Files.readAllBytes(jsonPath)); JSONObject
			  //jsonDataForMerge = new JSONObject(json);
			 
			 // JSONObject  jsonDataForMerge = new JSONObject(object.toString());
			FileRef result = generateFileFromTemplate(object);
			 // Save the result to the specified location.
            String outputFilePath = createOutputFilePath();
            result.saveAs(outputFilePath);
			
            body = new ResponseEntity<StreamingResponseBody>(HttpStatus.OK);
			return body;
		}  catch (ServiceUsageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
		
	}
	
	/**
	 * Gets the context.
	 *
	 * @return the context
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ExecutionContext getContext() throws IOException {
		/*
		 * final Credentials credentials =
		 * Credentials.serviceAccountCredentialsBuilder()
		 * .fromFile("pdfservices-api-credentials.json").build();
		 */
		
		Credentials credentials = Credentials.servicePrincipalCredentialsBuilder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .build();
		

		// Create an ExecutionContext using credentials
		// and create a new operation instance.
		return ExecutionContext.create(credentials);
	}
	
	private FileRef generateFileFromTemplate(JSONObject jsonDataForMerge) {
		ExecutionContext executionContext;
		try {
			executionContext = this.getContext();
		

			Files.deleteIfExists(Paths.get(output_file));
	
			////Path jsonPath = Paths.get("./receipt.json");
	
			//String json = new String(Files.readAllBytes(jsonPath));
			//JSONObject jsonDataForMerge = new JSONObject(json);
	
			DocumentMergeOptions documentMergeOptions = new DocumentMergeOptions(jsonDataForMerge, OutputFormat.PDF);
	
			DocumentMergeOperation documentMergeOperation = DocumentMergeOperation.createNew(documentMergeOptions);
	
			// Provide an input FileRef for the operation
			FileRef source = FileRef.createFromLocalFile(input_file);
			documentMergeOperation.setInput(source);
		

			// Execute the operation
			return documentMergeOperation.execute(executionContext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceUsageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	@RequestMapping(value = Constants.SET_CONVERSATION, method = RequestMethod.POST)
	public UserConversation setConversation(Model model, HttpServletResponse response) {
		Conversation conversation = null;
		UserConversation userConversation = null;
		try {
			conversation = chatService.setConversation();
			Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
			if(null != loggedInUser) {
				CustomUserDetails customUserDetails = (CustomUserDetails) loggedInUser.getPrincipal();
				
				userConversation = chatService.updateConversation(customUserDetails,conversation);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userConversation;
	}

}
