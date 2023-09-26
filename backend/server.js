const express = require("express")
const jwt = require("jsonwebtoken")
const logger = require("./logger.js")
const mysql = require("mysql2")
const app = express()

const port = 3000;

app.use(express.json())

// Parse JSON request bodies
app.use(express.json());

// Parse URL-encoded form data
app.use(express.urlencoded({ extended: false }));

// check token expiration
const currenttimestamp = Math.floor(Date.now() / 1000); // current time in seconds

const dummyuser = {
  username: "daniel",
  password: "1234",
};

const secretKey = "SUPER_SECRET";
const tokenExpiration = "30s"; // Token expiration time, e.g., 1 hour

// Middleware to verify JWT token
function verifyToken(req, res, next) {
    const token = req.headers["authorization"];
  
    if (!token) {
      return res.status(401).json({ message: "Token not provided" });
    }
  
    jwt.verify(token, secretKey, (err, decoded) => {
      if (err) {
        return res.status(401).json({ message: "Invalid token" });
      }
  
      // Check token expiration
      const currentTimestamp = Math.floor(Date.now() / 1000); // Current time in seconds
      if (decoded.exp <= currentTimestamp) {
        return res.status(401).json({ message: "Token has expired" });
      }
  
      req.user = decoded;
      next();
    });
  }
  app.post("/login", (req, res) => {
    const { username, password } = req.body;
  
    // Check if the provided username and password match the dummy user
    if (username === dummyUser.username && password === dummyUser.password) {
      // If the login is successful, create a JWT token with expiration
      const token = jwt.sign({ username }, secretKey, { expiresIn: tokenExpiration });
  
      // Log the successful login and token generation
      logger.info(`User ${username} logged in successfully`);
      logger.info(`Generated JWT token: ${token}`);
  
      // Send the token as a response
      res.json({ message: "Login successful", token });
    } else {
      // If the login fails, send an error response
      logger.warn(`Login failed for user ${username}`);
      res.status(401).json({ message: "Login failed" });
    }
  });

// Protected route that requires a valid token
app.get("/protected", verifyToken, (req, res) => {
    // If the middleware (verifyToken) passed, the token is valid
    res.json({ message: "Current time is " + new Date().toISOString() });
  });


app.listen(port, () => {
  logger.info(`Server is running on port ${port}`);
});