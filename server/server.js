const express = require("express");
const bodyParser = require("body-parser");
const csv = require("csv-parser");
const app = express();
const port = 3000;
const path = require("path");
const request = require("request");
require("dotenv").config();
const cors = require("cors");
const { Readable } = require("stream");
const axios = require("axios");
const fs = require("fs");
const querystring = require('querystring');

app.use(cors());
app.use(express.json({ limit: "50mb" }));
app.use(
  express.urlencoded({ limit: "50mb", extended: true, parameterLimit: 50000 })
);

const ADOBE_SIGN_BASE_URL = 'https://api.in1.adobesign.com:443/api/rest/v6/';

// SSL
const https = require("https");

console.log(path.resolve("../dev/certs/server.key"));
console.log(path.resolve("../dev/certs/server.crt"));

const options = {
  key: fs.readFileSync("../dev/certs/server.key"),
  cert: fs.readFileSync("../dev/certs/server.crt"),
};

// Define the storage for uploaded files
const multer = require("multer");
const storage = multer.memoryStorage(); // Use in-memory storage
const upload = multer({ storage });
const sharp = require("sharp");

// Middleware to parse request bodies
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

const STATIC_ASSETS_PATH = path.resolve(__dirname, "../static");

app.use(express.static(STATIC_ASSETS_PATH));


// Swagger UI
const swaggerDocs = require("./config/swaggerConfig");
const swaggerUI = require("swagger-ui-express");
app.use("/api-docs", swaggerUI.serve, swaggerUI.setup(swaggerDocs));

// Express Session
const session = require("express-session");
app.use(
  session({
    name: "sessionID",
    secret: "abc",
    resave: false,
    saveUninitialized: true,
    cookie: { secure: false }, // set to true if your using https
  })
);

// logging
const { log, clearLog } = require("./logging/logger");
const logRoute = require("./logging/logRoute");
app.use(logRoute);

// Middleware to add a logger to the request object
app.use((req, res, next) => {
  const sessionId = req.sessionID;
  req.logger = {
    log: (level, message) => {
      log(sessionId, level, message);
    },
    clear: () => {
      clearLog(sessionId);
    },
  };
  next();
});

// get sessionID
app.get("/api/session", (req, res) => {
  req.logger.log("info", "Initializing...");
  res.send({ sessionID: req.sessionID });
});

app.get("*", (req, res) => {
  res.sendFile(path.join(STATIC_ASSETS_PATH, "index.html"));
});
// Define the /callback route
app.get('/callback', (req, res) => {
  res.sendFile(path.join(STATIC_ASSETS_PATH, "index.html")); // Serve your main HTML file
});

app.post('/api/search', async (req, res) => {
  console.log('Inside api/search');
  const { startDate, endDate, email } = req.body.reqBody; 
  console.log("Received data: ", startDate, endDate, email);

  const searchEndpoint = ADOBE_SIGN_BASE_URL + 'search';
  try{
      const response = await axios.post(searchEndpoint,
        querystring.stringify({
            "scope": [
              "AGREEMENT_ASSETS"
            ],
            "agreementAssetsCriteria": {
              "modifiedDate": {
                "range": {
                  "gt": startDate,
                  "lt": endDate
                }
              },
              "pageSize": 50,
              "startIndex": 0,
              "status": [
                ""
              ],
              "type": [
                "AGREEMENT"
              ],
              "visibility": "SHOW_ALL",
            }
          }),
        {
          headers: {
            'Authorization': 'Bearer '
          },
        }
      );
      res.json(resData);
  } catch (error) {
    console.error('Token exchange failed', error);
    res.status(500).json({ error: 'Token exchange failed' });
  }
});
app.post('/api/exchange-token', async (req, res) => {
  console.log("inside -/api/exchange-token-----");
  const { authCode } = req.body;
  console.log("authCode------",authCode);
  if (!authCode) {
    return res.status(400).json({ error: 'Authorization code is required' });
  }

  try {
    const response = await axios.post(
      'https://abhishekdixitg.in1.adobesign.com/oauth/v2/token',
      querystring.stringify({
        "client_id": "CBJCHBCAABAApbsD-b_4RQuKSTGgI-sRNf8QWB673KWB",
        "client_secret": "NZX8wm1uKNKhsrkGejiuvrhjbMJbpyQt",
        "code": authCode,
        "grant_type": "authorization_code",
        "redirect_uri": "https://localhost:8443/callback",
      }),
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      }
    );
    const userUrl = ADOBE_SIGN_BASE_URL + 'users/me' ;
    console.log("userUrl--------",userUrl);
    console.log("response.data.access_token--------",response.data.access_token);
    const userData = await axios.get(userUrl, {
        headers: {
          'Authorization': 'Bearer '+response.data.access_token,
        },
      }
    );
    // Send the access token back to the client
    console.log("response.data---------",response.data);
    const resData = {
      "authData": response.data,
      "userData": userData.data 
    }
    res.json(resData);
  } catch (error) {
    console.error('Token exchange failed', error);
    res.status(500).json({ error: 'Token exchange failed' });
  }
});
async function postAPICall(endpoint, requestBody) {
  console.log("postAPICall:::", requestBody);
  let options = {
    url: endpoint,
    method: "POST",
    json: true,
    body: requestBody,
  };
  return new Promise((resolve, reject) => {
    request(options, (err, res, body) => {
      if (err || res.statusCode >= 400) {
        reject(err || body);
      } else {
        resolve(body);
      }
    });
  });
}
async function putAPICall(endpoint, requestBody) {
  //console.log("putAPICall::requestBody::",requestBody);
  //console.log("putAPICall::endpoint::",endpoint);

  let options = {
    url: endpoint,
    method: "PUT",
    ContentType: "application/octet-stream",
    body: requestBody,
  };
  return new Promise((resolve, reject) => {
    request(options, (err, res, body) => {
      if (err || res.statusCode >= 400) {
        reject(err || body);
      } else {
        resolve(body);
      }
    });
  });
}
app.use((req, res, next) => {
  console.log(`Received request for: ${req.url}`);
  next();
});
// Start the server
const server = https.createServer(options, app);
server.listen(port, () => {
  console.log(`Proxy server listening at https://localhost:${port}`);
});
