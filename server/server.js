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
const mysql = require("mysql2/promise");

const JSZip = require('jszip');
const application_domain = process.env.application_host;

const REGEX_PATTERN = /^[^<>:"/\\|?*]*$/;



app.use(cors());
app.use(express.json({ limit: "50mb" }));
app.use(
  express.urlencoded({ limit: "50mb", extended: true, parameterLimit: 50000 })
);

const ADOBE_SIGN_BASE_URL = process.env.ADOBE_SIGN_BASE_URL; // 'https://api.in1.adobesign.com:443/api/rest/v6/';
const initializeDb = process.env.INITIALIZE_DB === 'true'; 

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

let db; // Variable to hold the database connection

async function initializeDbConnection() {
  try {
    db = await mysql.createConnection({
      host: process.env.DB_HOST || "localhost",
      user: process.env.DB_USER || "root",
      password: process.env.DB_PASSWORD || "aabbhhii",
      database: process.env.DB_NAME || "bulk_operation_tool",
    });
    console.log('Database connected successfully');
  } catch (error) {
    console.error('Database connection failed:', error);
    process.exit(1);
  }
}
console.log("initializeDb:", initializeDb);
console.log("INITIALIZE_DB:", process.env.INITIALIZE_DB);

if (initializeDb) {
  console.log("Initializing database connection...");
  initializeDbConnection().catch(error => {
    console.error("Database initialization failed:", error);
  });
}


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
app.use((req, res, next) => {
  if (req.session.bearerToken) {
    req.headers['Authorization'] = `Bearer ${req.session.bearerToken}`;
  }
  console.log(`Authorization Header Set: ${req.headers['Authorization']}`);
  console.log(`Received request for: ${req.url}`);
  next();
});
/*
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
*/

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
function convertToISO8601(dateObj) {
  const { year, month, day } = dateObj;

  // JavaScript's Date uses a 0-based index for months (0 = January, 11 = December)
  const date = new Date(year, month - 1, day);

  // Convert to ISO-8601 string (format: YYYY-MM-DDTHH:mm:ss.sssZ)
  //const isoDateString = date.toISOString();
  console.log("date::::::::",dateObj);
  return dateObj;
}
app.post('/api/download-formfields', async (req, res) => {

  const { ids } = req.body;
  const zip = new JSZip();
  try {
    const files = await Promise.all(
      ids.map(async (id) => {
        const endpoint = `${ADOBE_SIGN_BASE_URL}agreements/${id}/formData`;
        console.log("download formfields endpoint::",endpoint);
        const response = await axios.get(endpoint, {
          headers: {
            'Authorization': req.headers['authorization'], 
            'Content-Type': 'application/json',
          },
          responseType: 'arraybuffer' 
        });
        const filename = `agreement_${id}.csv`; 
        zip.file(filename, response.data, { binary: true });

        return { filename, fileData: response.data.toString('base64') };

      })
    );

    // Send as zip file
    const content = await zip.generateAsync({ type: 'nodebuffer' });
    res.set({
      'Content-Type': 'application/zip',
      'Content-Disposition': 'attachment; filename="formfields.zip"'
    });
    res.send(content);
  } catch (error) {
    console.error("Error fetching files:", error.message);
    res.status(500).json({ error: "Failed to fetch files." });
  }

});

app.post('/api/download-agreements', async (req, res) => {

    const { ids } = req.body;
    const zip = new JSZip();
    try {
      const files = await Promise.all(
        ids.map(async (id) => {
          const endpoint = `${ADOBE_SIGN_BASE_URL}agreements/${id}/combinedDocument`;
          console.log("download endpoint::",endpoint);
          console.log("download req.headers['authorization']::",req.headers['authorization']);
          const response = await axios.get(endpoint, {
            headers: {
              'Authorization': req.headers['authorization'], // Pass Bearer token
              'Content-Type': 'application/json',
            },
            responseType: 'arraybuffer' // Set to handle binary data
          });
          const filename = `agreement_${id}.pdf`; // Name the file as needed
          zip.file(filename, response.data, { binary: true });

          return { filename, fileData: response.data.toString('base64') };

        })
      );

      // Send as zip file
      const content = await zip.generateAsync({ type: 'nodebuffer' });
      res.set({
        'Content-Type': 'application/zip',
        'Content-Disposition': 'attachment; filename="agreements.zip"'
      });
      res.send(content);
    } catch (error) {
      console.error("Error fetching files:", error.message);
      res.status(500).json({ error: "Failed to fetch files." });
    }

});

app.post('/api/search', async (req, res) => { 
  console.log('Inside api/search');
  const { startDate, endDate, email, selectedStatuses } = req.body; 
  console.log("Received data: ", startDate, endDate, email);
  
  const isoStartDate = convertToISO8601(startDate);
  console.log(isoStartDate);  // Output: 2020-02-03T00:00:00.000Z
  const isoEndDate = convertToISO8601(endDate);
  console.log(isoEndDate);  // Output: 2020-02-03T00:00:00.000Z

  const searchEndpoint = ADOBE_SIGN_BASE_URL + 'search';
  console.log('req.headers----------', req.headers['authorization']);
  console.log('selectedStatuses---------', selectedStatuses);
  
  try {
    let allResults = []; // Array to store all results
    let startIndex = 0;  // Start index for pagination
    let hasNext = true;   // Flag to control the loop

    while (hasNext) {
      const response = await axios.post(
        searchEndpoint,
        {
          scope: ["AGREEMENT_ASSETS"],
          agreementAssetsCriteria: {
            modifiedDate: {
              range: {
                gt: isoStartDate,  
                lt: isoEndDate,
              },
            },
            pageSize: 50,
            startIndex: startIndex, // Use the current startIndex
            status: selectedStatuses,  
            type: ["AGREEMENT"],
            visibility: "SHOW_ALL",
          },
        },
        {
          headers: {
            'Authorization': req.headers['authorization'],  
            'Content-Type': 'application/json',  
            'x-api-user': `email:${email}`
          },
        }
      );

      console.log("response.data-------------", response.data);
      allResults = allResults.concat(response.data.agreementAssetsResults.agreementAssetsResultList); // Collect results

      // Check for next index
      const nextIndex = response.data.agreementAssetsResults.searchPageInfo.nextIndex;
      hasNext = nextIndex !== null; // Continue if nextIndex is not null

      if (hasNext) {
        startIndex = nextIndex; // Update startIndex for the next iteration
      }
    }

    // Return all collected results
    res.json({ totalResults: allResults.length, agreementAssetsResults: allResults });
    
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
      process.env.OAUTH_TOKEN_URL,
      querystring.stringify({
        "client_id": process.env.client_id,
        "client_secret": process.env.client_secret,
        "code": authCode,
        "grant_type": "authorization_code",
        "redirect_uri": process.env.redirect_URL,
      }),
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      }
    );
    req.session.bearerToken = response.data.access_token;  // Save the token in the session
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
    console.log("userData.data---------",userData.data);
    if(initializeDb){
        const userId = userData.data.id;
        const email = userData.data.email;
        const loginTime = new Date();
        const ipAddress = req.ip;
        const device = req.headers['user-agent'];

        console.log("loginTime---------",loginTime);
        console.log("ipAddress---------",ipAddress);
        console.log("device---------",device);

        await db.execute(
          'INSERT INTO user_logins (user_id, login_time, ip_address, device, email) VALUES (?, ?, ?, ?,?)',
          [userId, loginTime, ipAddress, device, email]
        );
    }

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

// Start the server
const server = https.createServer(options, app);
server.listen(port, () => {
  console.log(`Proxy server listening at https://${application_domain}:${port}`);
});

