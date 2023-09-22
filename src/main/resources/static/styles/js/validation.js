$(document).ready(function () {

	    //$("#chatevent").click(function (){
			//document.getElementById("chatcontainer").style.display = "block";
			 var socket = new SockJS('/gs-guide-websocket');
		    stompClient = Stomp.over(socket);
		    stompClient.connect({}, function (frame) {
		        setConnected(true);
		        console.log('Connected: ' + frame);
		        stompClient.subscribe('/topic/chat', function (greeting) {
		            showGreeting(JSON.parse(greeting.body).content);
		        });
		    });
		//});
		
		 $("#closeform").click(function (){
			document.getElementById("chatcontainer").style.display = "none";
		});
		
        $("#selectAll").change(function () {
            $("input:checkbox").prop('checked', $(this).prop("checked"));
            $("#checkboxSelectError").html("").removeClass("w3-panel w3-red");
        });
        
       /*$("#bulkform").on("submit", function(e) {
	     //$("#bulkform button").click(function (e) {
            e.preventDefault();
             if ($('input:checked', this).length >= 1) {
					var buttonName = e.originalEvent.submitter.id ;
					var buttonValue = e.originalEvent.submitter.defaultValue;
					var formaction = ($("#download").attr("name") + ':'+ $("#download").attr("value"));
					//$(this).append(formaction);
					var formData = $("#bulkform").serializeArray();
					formData.push({name: buttonName, value: buttonValue});
					$.post("/deleteagreements",formData,
			            function(response){
							var isErr = 'hasError';
			                        // when there are an error then show error
			                        if (response.indexOf(isErr) > -1) {
			                            $("#yourPanel").html(response);
			                        } else {
			                           window.location.replace(window.URL);
			                        }
						});
	        } else {
	            alert('Please select at least 1 checkboxes!');
	            return false;
	        }
           
        });*/
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
					        url: "/deleteagreements",
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
});