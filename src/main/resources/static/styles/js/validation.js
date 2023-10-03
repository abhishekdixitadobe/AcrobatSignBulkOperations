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
		        stompClient.subscribe('/topic/statefullchat', function (greeting) {
		            showGreeting(JSON.parse(greeting.body).content);
		        });
		    });
		  
		//});
		
		// Handle click events for existing chat items
		    $(".chat-list").on("click", ".chat-list-item", function() {
		        // Remove the highlight class from all items
		        $(".chat-list-item").removeClass("selected");
		        
		        // Add the highlight class to the clicked item
		        $(this).addClass("selected");
		          var conversationId = $(this).find(".conversation-id").text(); 
                  $.ajax({
			            type: 'GET',
			            contentType: "application/json",
			            url: '/chat/conversations/'+ conversationId, // Adjust the URL
			            cache: false,
			            timeout: 600000,
			            dataType: "json",
			            success: function(response) {
							$("#loading-overlay").hide();
							console.log(response);
							if ($.trim(response)){   
								//$("#checkboxSelectError").html("Reminders cancelled for the selected agreement(s).").addClass("w3-panel w3-red");
								 for (var i = 0; i < response.length; i++) {
	           						 var item = response[i];
	           						 var queryName = item.queryName;
	           						 var message = item.message
	           						 console.log("queryName::",item.queryName);
	           						 $(".chat-logs").append(queryName);
	           						  INDEX++;
								     var str="";
									    str += "<div id='cm-msg-"+INDEX+"' class=\"chat-msg user\">";
									    str += "          <span class=\"msg-avatar\">";
									    str += "            <img src=\"/styles/images/robot.svg\">";
									    str += "          <\/span>";
									    str += "          <div id='ct_"+INDEX+"' class=\"cm-msg-text\">";
									    str += message;
									    str += "          <span id='sp_"+INDEX+"' style='cursor:pointer;font-size:18px;' class=\"fa fa-volume-up\" onclick='speaktext("+INDEX+")'></\span><\/div>";
									    str += "        <\/div>";
									    $(".chat-logs").append(str);
									    $("#cm-msg-"+INDEX).hide().fadeIn(300);  
									    $(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight}, 10000); 
									     
									}
									$(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight}, 10000); 
								} else {
									    $(".chat-logs").html("");
									    $("#cm-msg-"+INDEX).hide().fadeIn(300); 
								}
							    document.getElementById("wrapper").style.display = "block";
				        },
				        error: function(error) {
					        // Handle errors if any
					        console.error('Error:', error);
					    }
			        });
		    });
	
		$("#newChat").click(function (){
			 //const conversationId = document.getElementById("conversationId").textContent;
			 $.ajax({
	            type: 'POST',
	            contentType: "application/json",
	            url: '/chat/conversations/', // Adjust the URL
	            headers: { 'username':'abhishekd@adobe.com', "password":"aabbhhii" },
	            cache: false,
	            timeout: 600000,
	            success: function(response) {
					$("#loading-overlay").hide();
					console.log(response);
					// window.location.reload();
					document.getElementById("wrapper").style.display = "block";
					 // Create a new list item
			        var newItem = $("<li>", {
			            class: "chat-list-item",
			            text: response.conversationName
			        });
			
			        // Append it to the chat list
			        $(".chat-list").append(newItem);
			
			        // Highlight the new item
			        newItem.addClass("selected");
			        // Scroll to the newly added item if needed
			        var container = $(".chat-list");
			        var scrollTo = newItem;
			        container.scrollTop(
			            scrollTo.offset().top - container.offset().top + container.scrollTop()
			        );

		        },
		        error: function(error) {
			        // Handle errors if any
			        console.error('Error:', error);
			    }
	        });
		    
		});
		
		// Add an event listener to each chat conversation element
		document.querySelectorAll('.chat-list-item').forEach((chatItem) => {
		    chatItem.addEventListener('click', () => {
			
				//$('.chat-list-item').addClass("selected");
		        //const conversationId = document.getElementById("conversationId").textContent;
                  
		    
		    });
		});
		
		$("#chatevent").click(function (){
			document.getElementById("chatcontainer").style.display = "block";
			});
			
		$("#openChatEvent").click(function (){
			document.getElementById("wrapper").style.display = "block";
			});
			
	   $("#closeChatEvent").click(function (){
			document.getElementById("wrapper").style.display = "none";
			});
		
		document.getElementById("wrapper").style.display = "none";
		
		
		 $("#closeform").click(function (){
			document.getElementById("chatcontainer").style.display = "none";
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