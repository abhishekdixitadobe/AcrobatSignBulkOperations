function navigateToPage(userIds, startDate, beforeDate, size, pageNumber) {
			    var form = document.createElement('form');
			    form.method = 'post';
			    form.action = '/multiuseragreements'; // Set your URL here
				// Split the userIds string and create a valid JSON array
			    var formattedUserIds = userIds
		        .replace('[', '[')
		        .replace(']', ']')
		        .replace(/ /g, '') // Remove spaces
		        .replace(/,/g, '", "') // Replace commas with ", "
		        .replace('[', '["')
		        .replace(']', '"]');
			    // Create hidden input fields for each parameter
			    addInput(form, 'userIds', formattedUserIds);
			    addInput(form, 'startDate', startDate);
			    addInput(form, 'beforeDate', beforeDate);
			    addInput(form, 'size', size);
			    addInput(form, 'page', pageNumber);
			
			    document.body.appendChild(form);
			    form.submit();
			}
			
			function addInput(form, name, value) {
			    var input = document.createElement('input');
			    input.type = 'hidden';
			    input.name = name;
			    input.value = value;
			    form.appendChild(input);
			}
$(document).ready(function () {
		$("#downloadList").on("click", function() {
			
				// Iterate through checkboxes to check if at least one is checked
			    var atLeastOneChecked = false;
			    $('input[type="checkbox"]').each(function() {
			        if ($(this).is(":checked")) {
			            atLeastOneChecked = true;
			            return false; // Exit the loop if at least one checkbox is checked
			        }
			    });
			    if(atLeastOneChecked){
					$("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
					$("#loading-overlay").show();
			        var selectedAgreements = [];
			        
			        var tableRows = $("#agreementDetails tbody tr");
			        tableRows.each(function(index) {
							var checkbox = $(this).find("input[type='checkbox']");
							 if (checkbox.is(":checked")) {
			                var agreement = {
			                    id: document.getElementsByName('id')[index].textContent,
			                    name: document.getElementsByName('name')[index].textContent,
			                    status: document.getElementsByName('status')[index].textContent,
			                    modifiedDate: document.getElementsByName('modifiedDate')[index] ?document.getElementsByName('modifiedDate')[index].textContent: '',
			                    userEmail: document.getElementsByName('userEmail')[index] ? document.getElementsByName('userEmail')[index].textContent : '',
			                    userId: document.getElementsByName('userId')[index] ? document.getElementsByName('userId')[index].textContent : ''
			                };
			                selectedAgreements.push(agreement);
			                }
			        });
			
			        $.ajax({
			            type: "POST",
			            contentType: "application/json",
			            url: "/downloadList", // Adjust the URL
			            data: JSON.stringify(selectedAgreements),
			            cache: false,
			            timeout: 600000,
				        success: function (data) {
							 $("#loading-overlay").hide();
			                console.log("SUCCESS:", data);
			                var blob = new Blob([data], { type: 'text/csv' });
				
				            // Use FileSaver.js to trigger the download
				            saveAs(blob, 'agreements.csv'); // You need to include the FileSaver.js library
				        },
				        error: function (jqXHR, textStatus, errorThrown) {
							$("#loading-overlay").hide();
				            console.log("ERROR : ", textStatus, errorThrown);
				        }
			        });
		        } else {
				 $("#checkboxSelectError").html("Please select at least one CheckBox").addClass("w3-panel w3-red");
		            return false;
				}
		    });
		    
	    	$("#cancelAgreements").on("click", function() {
			
				// Iterate through checkboxes to check if at least one is checked
			    var atLeastOneChecked = false;
			    $('input[type="checkbox"]').each(function() {
			        if ($(this).is(":checked")) {
			            atLeastOneChecked = true;
			            return false; // Exit the loop if at least one checkbox is checked
			        }
			    });
			    if(atLeastOneChecked){
					$("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
					$("#loading-overlay").show();
			        var selectedAgreements = [];
			        
			        var tableRows = $("#agreementDetails tbody tr");
			        tableRows.each(function(index) {
							var checkbox = $(this).find("input[type='checkbox']");
							 if (checkbox.is(":checked")) {
			                var agreement = {
			                    id: document.getElementsByName('id')[index].textContent,
			                    name: document.getElementsByName('name')[index].textContent,
			                    status: document.getElementsByName('status')[index].textContent,
			                    userEmail: document.getElementsByName('userEmail')[index] ? document.getElementsByName('userEmail')[index].textContent : '',
			                    userId: document.getElementsByName('userId')[index] ? document.getElementsByName('userId')[index].textContent : ''
			                };
			                selectedAgreements.push(agreement);
			                }
			        });
			
			        $.ajax({
			            type: "POST",
			            contentType: "application/json",
			            url: "/cancelagreements", // Adjust the URL
			            data: JSON.stringify(selectedAgreements),
			            cache: false,
			            timeout: 600000,
			            xhrFields: {
				            responseType: 'blob' // Set response type to blob
				        },
				        success: function (data, textStatus, jqXHR) {
							$("#loading-overlay").hide();
							$("#checkboxSelectError").html("Selected agreement(s) are cancelled.").addClass("w3-panel w3-red");
				        },
				        error: function (jqXHR, textStatus, errorThrown) {
							$("#loading-overlay").hide();
				            console.log("ERROR : ", textStatus, errorThrown);
				        }
			        });
		        } else {
				 $("#checkboxSelectError").html("Please select at least one CheckBox").addClass("w3-panel w3-red");
		            return false;
				}
		    });

		$("#downloadPDFButton").on("click", function() {
			
				// Iterate through checkboxes to check if at least one is checked
			    var atLeastOneChecked = false;
			    $('input[type="checkbox"]').each(function() {
			        if ($(this).is(":checked")) {
			            atLeastOneChecked = true;
			            return false; // Exit the loop if at least one checkbox is checked
			        }
			    });
			    if(atLeastOneChecked){
					$("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
					$("#loading-overlay").show();
			        var selectedAgreements = [];
			        
			        var tableRows = $("#agreementDetails tbody tr");
			        tableRows.each(function(index) {
							var checkbox = $(this).find("input[type='checkbox']");
							 if (checkbox.is(":checked")) {
			                var agreement = {
			                    id: document.getElementsByName('id')[index].textContent,
			                    name: document.getElementsByName('name')[index].textContent,
			                    status: document.getElementsByName('status')[index].textContent,
			                    userEmail: document.getElementsByName('userEmail')[index] ? document.getElementsByName('userEmail')[index].textContent : '',
			                    userId: document.getElementsByName('userId')[index] ? document.getElementsByName('userId')[index].textContent : ''
			                };
			                selectedAgreements.push(agreement);
			                }
			        });
			
			        $.ajax({
			            type: "POST",
			            contentType: "application/json",
			            url: "/downloadAgreements", // Adjust the URL
			            data: JSON.stringify(selectedAgreements),
			            cache: false,
			            timeout: 600000,
			            xhrFields: {
				            responseType: 'blob' // Set response type to blob
				        },
				        success: function (data, textStatus, jqXHR) {
							$("#loading-overlay").hide();
				            var blob = new Blob([data], { type: 'application/zip' });
				
				            // Use FileSaver.js to trigger the download
				            saveAs(blob, 'agreements.zip'); // You need to include the FileSaver.js library
				        },
				        error: function (jqXHR, textStatus, errorThrown) {
							$("#loading-overlay").hide();
				            console.log("ERROR : ", textStatus, errorThrown);
				        }
			        });
		        } else {
				 $("#checkboxSelectError").html("Please select at least one CheckBox").addClass("w3-panel w3-red");
		            return false;
				}
		    });
		    
		    $("#downloadFormFieldButton").on("click", function() {
			
				// Iterate through checkboxes to check if at least one is checked
			    var atLeastOneChecked = false;
			    $('input[type="checkbox"]').each(function() {
			        if ($(this).is(":checked")) {
			            atLeastOneChecked = true;
			            return false; // Exit the loop if at least one checkbox is checked
			        }
			    });
			    if(atLeastOneChecked){
					$("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
					$("#loading-overlay").show();
			        var selectedAgreements = [];
			        
			        var tableRows = $("#agreementDetails tbody tr");
			        tableRows.each(function(index) {
							var checkbox = $(this).find("input[type='checkbox']");
							 if (checkbox.is(":checked")) {
			                 var agreement = {
			                    id: document.getElementsByName('id')[index].textContent,
			                    name: document.getElementsByName('name')[index].textContent,
			                    status: document.getElementsByName('status')[index].textContent,
			                    userEmail: document.getElementsByName('userEmail')[index] ? document.getElementsByName('userEmail')[index].textContent : '',
			                    userId: document.getElementsByName('userId')[index] ? document.getElementsByName('userId')[index].textContent : ''
			                };
			                selectedAgreements.push(agreement);
			                }
			        });
			
			        $.ajax({
			            type: "POST",
			            contentType: "application/json",
			            url: "/downloadformfields", // Adjust the URL
			            data: JSON.stringify(selectedAgreements),
			            cache: false,
			            timeout: 600000,
			            xhrFields: {
				            responseType: 'blob' // Set response type to blob
				        },
				        success: function (data, textStatus, jqXHR) {
							$("#loading-overlay").hide();
				            var blob = new Blob([data], { type: 'application/zip' });
				
				            // Use FileSaver.js to trigger the download
				            saveAs(blob, 'formfields.zip'); // You need to include the FileSaver.js library
				        },
				        error: function (jqXHR, textStatus, errorThrown) {
							$("#loading-overlay").hide();
				            console.log("ERROR : ", textStatus, errorThrown);
				        }
			        });
		        } else {
				 $("#checkboxSelectError").html("Please select at least one CheckBox").addClass("w3-panel w3-red");
		            return false;
				}
		    });
        $("#selectAll").change(function () {
            $("input:checkbox").prop('checked', $(this).prop("checked"));
            $("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
        });
        
       $("#bulkform").on("submit", function(e) {
			 if ($('input:checked', this).length >= 1) {
						$("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
						var buttonName = e.originalEvent.submitter.id ;
						var buttonValue = e.originalEvent.submitter.defaultValue;
						var formData = $("#bulkform").serializeArray();
						formData.push({name: buttonName, value: buttonValue});
				        $.ajax({
					        type: "POST",
					        contentType: "application/json",
					        url: "/manageagreements",
					        data: JSON.stringify(formData),
					        dataType: 'json',
					        cache: false,
					        timeout: 600000,
					        success: function (data) {
								console.log("SUCCESS : ", data);
								},
				        	error: function (e) {
				            console.log("ERROR : ", e);
				
				        }
				    });
		   }else {
	            //alert('Please select at least 1 checkboxes!');
	            $("#checkboxSelectError").html("Please select at least one CheckBox").addClass("w3-panel w3-red");
	            return false;
	        }
     });
     
     $("#userAgreement").on("submit", function(e) {
			  //var userEmail = $.trim($('#userEmail').val());
			 // var userGroupValue = $.trim($('#userGroupValue').val());
			  // if (userEmail === '' & userGroupValue === '') {
				//	$("#checkboxSelectError").html("Please select at least one filter criteria:: User Email or Group").addClass("w3-panel w3-red");
		          //  return false;						
			   //}else {
		            //$("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
					var buttonName = e.originalEvent.submitter.id ;
					var buttonValue = e.originalEvent.submitter.defaultValue;
					var formData = $("#userAgreement").serializeArray();
					formData.push({name: buttonName, value: buttonValue});
			        $.ajax({
				        type: "POST",
				        contentType: "application/json",
				        url: "/agreements",
				        data: JSON.stringify(formData),
				        dataType: 'json',
				        cache: false,
				        timeout: 600000,
				        success: function (data) {
							console.log("SUCCESS : ", data);
							},
			        	error: function (e) {
			            console.log("ERROR : ", e);
			
			        }
			    });
		     //}
     	});
});