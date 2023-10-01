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
	
		$("#loadChatConversation").click(function (){
			    const conversationId = document.getElementById('conversationId').textContent;
			    console.log('conversationId--',conversationId);
			    document.getElementById("wrapper").style.display = "block";
				/* fetch(`/chat/conversations/${conversationId}`)
                    .then(response => response.json())
                    .then(data => {
                        console.log("Selected chat data:", data);
                        // Process the retrieved chat data as needed
                    })*/
		});
		
		// Add an event listener to each chat conversation element
		document.querySelectorAll('.chat-list-item').forEach((chatItem) => {
		    chatItem.addEventListener('click', () => {
		        // Extract the conversation ID from the data attribute
		        //const conversationId = chatItem.getAttribute('data-conversation-id');
		        const conversationId = document.getElementById("conversationId").textContent;
		       /* fetch(`/chat/conversations/${conversationId}`)
                    .then(response => response.json())
                    .then(data => {
                        console.log("Selected chat data:", data);
                        // Process the retrieved chat data as needed
                        $(".chat-logs").html(data);
                    })*/
                    
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
								    //$(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight}, 50);  
								}
				        },
				        error: function(error) {
					        // Handle errors if any
					        console.error('Error:', error);
					    }
			        });
		    
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