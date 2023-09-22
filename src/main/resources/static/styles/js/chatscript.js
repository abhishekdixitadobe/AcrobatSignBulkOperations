try {
  var SpeechRecognition = this.SpeechRecognition || this.webkitSpeechRecognition;
  var recognition = new SpeechRecognition();
}
catch(e) {
  console.error(e);
  $('.no-browser-support').show();
  $('.app').hide();
}

var started = 0;
var noteTextarea = $('#chat-input');
var instructions = $('#recording-instructions');
var notesList = $('ul#notes');

var noteContent = '';

// Get all notes from previous sessions and display them.
var notes = getAllNotes();
renderNotes(notes);

/*-----------------------------
      Voice Recognition 
------------------------------*/

// If false, the recording will stop after a few seconds of silence.
// When true, the silence period is longer (about 15 seconds),
// allowing us to keep recording even when the user pauses. 
recognition.continuous = true;

// This block is called every time the Speech APi captures a line. 
recognition.onresult = function(event) {

  // event is a SpeechRecognitionEvent object.
  // It holds all the lines we have captured so far. 
  // We only need the current one.
  var current = event.resultIndex;

  // Get a transcript of what was said.
  var transcript = event.results[current][0].transcript;

  // Add the current transcript to the contents of our Note.
  // There is a weird bug on mobile, where everything is repeated twice.
  // There is no official solution so far so we have to handle an edge case.
  var mobileRepeatBug = (current == 1 && transcript == event.results[0][0].transcript);

  if(!mobileRepeatBug) {
    noteContent = transcript;
    noteTextarea.val(noteContent);
    sendMessage();
  }
};

recognition.onstart = function() { 
  instructions.text('Voice recognition activated. Try speaking into the microphone.');
}

recognition.onspeechend = function() {
  instructions.text('You were quiet for a while so voice recognition turned itself off.');
}

recognition.onerror = function(event) {
  if(event.error == 'no-speech') {
    instructions.text('No speech was detected. Try again.');  
  };
}



/*-----------------------------
      App buttons and input 
------------------------------*/

$('#start-record-btn').on('click', function(e) {
  if (noteContent.length) {
    noteContent += ' ';
  }
  if(started==1)
  {
	 started = 0;
	 recognition.stop();
	 $(".fa-microphone-slash").removeClass("fa fa-microphone-slash").addClass("fa fa-microphone");
	 instructions.text('Voice recognition paused.');
  }
  else
  {
	  started = 1;
	  recognition.start();
	  //$(".fa-microphone").removeClass("fa fa-microphone").addClass("fa fa-microphone-slash");
	  $(".fa-microphone-slash").removeClass("fa fa-microphone-slash").addClass("fa fa-microphone");
	   //$(".fa fa-microphone-slash").removeClass("fa fa-microphone-slash").addClass("fa fa-microphone");
	  instructions.text('Voice recognition started.');
  }
});


$('#pause-record-btn').on('click', function(e) {
  recognition.stop();
  $('#pause-record-btn').attr('id', 'start-record-btn');
  $(".fa-microphone-slash").removeClass("fa fa-microphone-slash").addClass("fa fa-microphone");
  instructions.text('Voice recognition paused.');
});

// Sync the text inside the text area with the noteContent variable.
noteTextarea.on('input', function() {
  noteContent = $(this).val();
})

$('#save-note-btn').on('click', function(e) {
  recognition.stop();

  if(!noteContent.length) {
    instructions.text('Could not save empty note. Please add a message to your note.');
  }
  else {
    // Save note to localStorage.
    // The key is the dateTime with seconds, the value is the content of the note.
    saveNote(new Date().toLocaleString(), noteContent);

    // Reset variables and update UI.
    noteContent = '';
    renderNotes(getAllNotes());
    noteTextarea.val('');
    instructions.text('Note saved successfully.');
  }
      
})


notesList.on('click', function(e) {
  e.preventDefault();
  var target = $(e.target);

  // Listen to the selected note.
  if(target.hasClass('listen-note')) {
    var content = target.closest('.note').find('.content').text();
    readOutLoud(content);
  }

  // Delete note.
  if(target.hasClass('delete-note')) {
    var dateTime = target.siblings('.date').text();  
    deleteNote(dateTime);
    target.closest('.note').remove();
  }
});



/*-----------------------------
      Speech Synthesis 
------------------------------*/

function readOutLoud(message) {
	var speech = new SpeechSynthesisUtterance();

  // Set the text and voice attributes.
	speech.text = message;
	speech.volume = 1;
	speech.rate = 1;
	speech.pitch = 1;
  
	window.speechSynthesis.speak(speech);
}



/*-----------------------------
      Helper Functions 
------------------------------*/

function renderNotes(notes) {
  var html = '';
  if(notes.length) {
    notes.forEach(function(note) {
      html+= `<li class="note">
        <p class="header">
          <span class="date">${note.date}</span>
          <a href="#" class="listen-note" title="Listen to Note">Listen to Note</a>
          <a href="#" class="delete-note" title="Delete">Delete</a>
        </p>
        <p class="content">${note.content}</p>
      </li>`;    
    });
  }
  else {
    html = '<li><p class="content">You don\'t have any notes yet.</p></li>';
  }
  notesList.html(html);
}


function saveNote(dateTime, content) {
  localStorage.setItem('note-' + dateTime, content);
}


function getAllNotes() {
  var notes = [];
  var key;
  for (var i = 0; i < localStorage.length; i++) {
    key = localStorage.key(i);

    if(key.substring(0,5) == 'note-') {
      notes.push({
        date: key.replace('note-',''),
        content: localStorage.getItem(localStorage.key(i))
      });
    } 
  }
  return notes;
}


function deleteNote(dateTime) {
  localStorage.removeItem('note-' + dateTime); 
}

//Chat Web API Script
var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
         $(".chat-box").show().fadeIn(300);   
    }
    else {
         $(".chat-box").hide().fadeOut(300);   
         recognition.stop();
         $(".fa-microphone-slash").removeClass("fa fa-microphone-slash").addClass("fa fa-microphone");
    }
    $(".chat-logs").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chat', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        stompClient = null;
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
	if(stompClient != null){
		var msg = $("#chat-input").val();
		request_message(msg);
		 if (compare(utterances, answers, msg)) {
		    // Search for exact match in triggers
		    product = compare(utterances, answers, msg);
		    //addChatEntry(input, product);
		    response_message(product)
		   	msg.value ="";
		  } else {
	    	stompClient.send("/app/chat", {}, JSON.stringify({'message': msg}));
	    	msg.value ="";
	    }
    }
    else {
    	var msg = "Kindly connect to web server";
		request_message(msg);
    }
}

function compare(utterancesArray, answersArray, string) {
  let reply;
  let replyFound = false;
  for (let x = 0; x < utterancesArray.length; x++) {
    for (let y = 0; y < utterancesArray[x].length; y++) {
      if (utterancesArray[x][y] === string) {
        let replies = answersArray[x];
        reply = replies[Math.floor(Math.random() * replies.length)];
        replyFound = true;
        break;
      }
    }
    if (replyFound) {
      break;
    }
  }
  return reply;
}

function showGreeting(message) {
	response_message(message);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#chat-submit" ).click(function() {
		var msg = $("#chat-input").val();
		if (compare(utterances, answers, msg)) {
		    // Search for exact match in triggers
		    product = compare(utterances, answers, msg);
		    //addChatEntry(input, product);
		    response_message(product)
		   	msg.value ="";
		   	}{
			sendMessage(); 
		}
	  
	 });
});


$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});

//Chat Bot design manage Code
$(".chat-box").hide();   
var INDEX = 0;
function request_message(msg) {
    INDEX++;
    $(".chat-logs").append(msg);
} 

function response_message(msg) {

	if(msg=="")
		msg = "Hi! Sorry i beg your pardon.";
    INDEX++;
    var str="";
    str += "<div id='cm-msg-"+INDEX+"' class=\"chat-msg user\">";
    str += "          <span class=\"msg-avatar\">";
    str += "            <img src=\"/styles/images/robot.svg\">";
    str += "          <\/span>";
    str += "          <div id='ct_"+INDEX+"' class=\"cm-msg-text\">";
    str += msg;
    str += "          <span id='sp_"+INDEX+"' style='cursor:pointer;font-size:18px;' class=\"fa fa-volume-up\" onclick='speaktext("+INDEX+")'></\span><\/div>";
    str += "        <\/div>";
    $(".chat-logs").append(str);
    $("#cm-msg-"+INDEX).hide().fadeIn(300);  
    $(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight}, 1000);    
    //readOutLoud(msg);
}

function speaktext(e){
	var msg =  $("#ct_"+e).html();  
	readOutLoud(msg);
}


// input options
const utterances = [
 
  ["how are you", "how is life", "how are things"],
  ["hi", "hey", "hello", "good morning", "good afternoon"],
  ["what are you doing", "what is going on", "what is up"],
  ["how old are you"],
  ["who are you", "are you human", "are you bot", "are you human or bot"],
  ["who created you", "who made you"],
  [
    "your name please",
    "your name",
    "may i know your name",
    "what is your name",
    "what call yourself"
  ],
  ["happy", "good", "fun", "wonderful", "fantastic", "cool"],
  ["bad", "bored", "tired"],
  ["help me", "tell me story", "tell me joke"],
  ["ah", "yes", "ok", "okay", "nice"],
  ["bye", "good bye", "goodbye", "see you later"],
  ["what should i eat today"],
  ["what", "why", "how", "where", "when"],
  ["no", "not sure", "maybe", "no thanks"],
  [""],
  ["haha", "ha", "lol", "hehe", "funny", "joke"],
  ["send", "send agreement", "please send", "send document"]
];

// Possible responses corresponding to triggers

const answers = [
   [
    "Fine... how are you?",
    "Pretty well, how are you?",
    "Fantastic, how are you?"
  ],
  [
    "Hello!", "Hi!", "Hey!", "Hi there!", "Howdy"
  ],
  [
    "Nothing much",
    "About to go to sleep",
    "Can you guess?",
    "I don't know actually"
  ],
  ["I am infinite"],
  ["I am just a bot", "I am a bot. What are you?"],
  ["The one true God, JavaScript"],
  ["I am nameless", "I don't have a name"],
  ["Have you ever felt bad?", "Glad to hear it"],
  ["Why?", "Why? You shouldn't!"],
  ["What about?", "Once upon a time..."],
  ["Tell me a story", "Tell me a joke", "Tell me about yourself"],
  ["Bye", "Goodbye", "See you later"],
  ["Pasta", "Burger"],
  ["Great question"],
  ["That's ok", "What do you want to talk about?"],
  ["Please say something :("],
  ["Haha!", "Good one!"],
  ["Please provide the sender email and template id", "We need more detals. Please provide sender name and library template"]
];

// Random for any other user input

const alternatives = [
  "Go on...",
  "Try again",
];
  