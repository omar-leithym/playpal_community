const express = require("express");
const cors = require("cors");
const { createPool } = require("mysql2/promise");
const asyncHandler = require("express-async-handler");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
require("dotenv").config();
const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cors());

const JWT_SECRET = "sdfdsfdsfsdf";

const dbConfig = {
  host: "localhost",
  user: "root",
  password: "pass",
  database: "ppcfinal",
};

const pool = createPool(dbConfig);

pool
  .getConnection()
  .then((connection) => {
    console.log("Connected to MySQL database");
    app.listen(3001, () => {
      console.log("Server is running on port 3001");
    });

    connection.release();
  })
  .catch((err) => {
    console.error("Database connection failed:", err);
  });

module.exports = pool;

app.get("/test", (req, res) => {
  res.status(200).send("Server is up and running!");
});

const protect = asyncHandler(async (req, res, next) => {
  let token;

  if (
    req.headers.authorization &&
    req.headers.authorization.startsWith("Bearer")
  ) {
    try {
      token = req.headers.authorization.split(" ")[1];
      const decoded = jwt.verify(token, JWT_SECRET);

      const connection = await pool.getConnection();
      const [rows] = await connection.query("SELECT * FROM user WHERE id = ?", [
        decoded.id,
      ]);
      connection.release();

      if (rows.length === 0) {
        res.status(401);
        throw new Error("Not authorized, user not found");
      }

      req.user = rows[0];
      delete req.user.password;
      next();
    } catch (error) {
      console.log(error);
      res.status(401);
      throw new Error("Not authorized");
    }
  }

  if (!token) {
    res.status(401);
    throw new Error("Not authorized, no token");
  }
});

app.post("/register", async (req, res) => {
  const { username, email, password, firstName, lastName } = req.body;
  console.log("entered register");
  if (!username || !email || !password || !firstName || !lastName) {
    return res.status(400).json("Please fill in all fields");
  }

  try {
    const connection = await pool.getConnection();

    const [userExists] = await connection.query(
      "SELECT * FROM user WHERE username = ?",
      [username]
    );
    const [emailExists] = await connection.query(
      "SELECT * FROM user WHERE email = ?",
      [email]
    );

    if (userExists.length > 0) {
      connection.release();
      return res.status(400).json("Username exists");
    }

    if (emailExists.length > 0) {
      connection.release();
      return res.status(400).json("Email exists");
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    const [result] = await connection.query(
      "INSERT INTO user (username, email, password, firstName, lastName) VALUES (?, ?, ?, ?, ?)",
      [username, email, hashedPassword, firstName, lastName]
    );

    connection.release();

    const token = jwt.sign({ id: result.insertId }, JWT_SECRET, {
      expiresIn: "30d",
    });
    console.log(result.id);
    res.status(201).json({
      id: result.insertId,
      username,
      email,
      firstName,
      lastName,
      token,
    });
  } catch (error) {
    console.error("Error during user registration:", error);
    res.status(500).json("Server error");
  }
});

app.get("/profile", async (req, res) => {
  const { email } = req.query;
  console.log(email + " arrived");
  try {
    const connection = await pool.getConnection();

    // Query to fetch user details
    const [userRows] = await connection.query(
      "SELECT username, bio,firstName FROM user WHERE email = ?",
      [email]
    );

    if (userRows.length === 0) {
      connection.release();
      return res.status(404).json({ message: "User not found" });
    }

    const { username, bio, firstName } = userRows[0];

    // Query to fetch user pictures
    const [otherRows] = await connection.query(
      "SELECT urlPic FROM userPics WHERE email = ?",
      [email]
    );

    const urlPics = otherRows.map((row) => row.urlPic); // Extracting urlPics from rows
    // console.log(urlPics);
    //console.log(otherRows);
    const [otherRows2] = await connection.query(
      "SELECT P.sport_name,P.skillLevel FROM Plays P INNER JOIN user u ON  u.email=P.email WHERE u.email= ?",
      [email]
    );
    const favSports = otherRows2.map((row) => row.sport_name);
    const favSportsSkill = otherRows2.map((row) => row.skillLevel);
    console.log(favSports);
    console.log(favSportsSkill);
    connection.release();
    //console.log(otherRows2);
    // Prepare response based  on the results of both queries
    res
      .status(200)
      .json({ firstName, username, bio, urlPics, favSports, favSportsSkill }); //now add favSports    favSports and favSportsSkill are dependent on each other each index represents one object
    //idk if it is best practice to do this or just send an array of JSON and deal with it in frontend
  } catch (error) {
    console.error("Error during profile retrieval:", error);
    res.status(500).json({ message: "Server error" });
  }
});
app.get("/pic", async (req, res) => {
  const { email } = req.query;
  console.log(email);
  try {
    const connection = await pool.getConnection();
    const [rows] = await connection.query(
      "SELECT profilePicURL FROM user WHERE email= ?",
      [email]
    );
    connection.release();
    //console.log(rows[0]);
    if (rows.length === 0) {
      return res.status(404).json({ message: "User not found" });
    }

    const profilePicURL = rows[0].profilePicURL;
    console.log(profilePicURL);
    res.status(200).json({ profilePicURL });
  } catch (error) {
    console.error("Error during profile picture retrieval:", error);
    res.status(500).json({ message: "Server error" });
  }
});
app.get("/bio", async (req, res) => {
  const { email } = req.query;
  //  console.log(email);
  try {
    const connection = await pool.getConnection();
    const [rows] = await connection.query(
      "SELECT bio from user WHERE email= ?",
      [email]
    );
    //console.log("not found");
    connection.release();

    if (rows.length === 0) {
      return res.status(404).json({ message: "User not found" });
    }

    const bio = rows[0].bio;
    res.status(200).json({ bio });
  } catch (error) {
    console.error("Error during bio retrieval:", error);
    res.status(500).json({ message: "Server error" });
  }
});

// Example with POST and req.body
app.post("/bio", async (req, res) => {
  const { email } = req.body;

  try {
    const connection = await pool.getConnection();
    const [rows] = await connection.query(
      "SELECT bio from user WHERE email= ?",
      [email]
    );
    connection.release();

    if (rows.length === 0) {
      return res.status(404).json({ message: "User not found" });
    }

    const bio = rows[0].bio;
    res.status(200).json({ bio });
  } catch (error) {
    console.error("Error during bio retrieval:", error);
    res.status(500).json({ message: "Server error" });
  }
});

app.post("/login", async (req, res) => {
  const { username, password } = req.body;

  try {
    const connection = await pool.getConnection();
    const [rows] = await connection.query(
      "SELECT * FROM user WHERE username = ?",
      [username]
    );
    connection.release();

    if (rows.length === 0) {
      return res.status(400).json("Invalid credentials");
    }

    const user = rows[0];
    const isMatch = await bcrypt.compare(password, user.password);

    if (isMatch) {
      const token = jwt.sign({ id: user.id }, JWT_SECRET, {
        expiresIn: "30d",
      });

      res.status(200).json({
        id: user.id,
        username: user.username,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        token,
      });
    } else {
      res.status(400).json("Invalid credentials");
    }
  } catch (error) {
    console.error("Error during user login:", error);
    res.status(500).json("Server error");
  }
});

app.post("/addSport", async (req, res) => {
  const { sport_name, email, skillLevel } = req.body;

  // Validate input
  if (!sport_name || !email || !skillLevel) {
    return res.status(400).json("All fields are required");
  }

  try {
    // Get a connection from the pool
    const connection = await pool.getConnection();

    // Insert data into the sport table
    await connection.query(
      "INSERT INTO Plays (sport_name, email, skillLevel) VALUES (?, ?, ?)",
      [sport_name, email, skillLevel]
    );

    connection.release(); // Release the connection

    res.status(200).json({ message: "Sport added successfully" });
  } catch (error) {
    console.error("Error adding sport:", error);
    res.status(500).json("Server error");
  }
});

app.get("/matchmake", protect, async (req, res) => {
  const { location, gender, sport, level } = req.query;

  try {
    const connection = await pool.getConnection();

    let query = `SELECT user.id, user.firstName, user.lastName, user.gender, user.profilePicURL, user.location, user.email, plays.skillLevel AS level FROM user
                     JOIN plays ON user.email = plays.email
                     WHERE plays.sport_name = ?`;
    let conditions = [];
    let values = [sport];

    if (location && location !== "null") {
      conditions.push("user.location = ?");
      values.push(location);
    }
    if (gender && gender !== "null") {
      conditions.push("user.gender = ?");
      values.push(gender);
    }
    if (level && level !== "null") {
      conditions.push("plays.skillLevel = ?");
      values.push(level);
    }

    if (conditions.length > 0) {
      query += " AND " + conditions.join(" AND ");
    }

    console.log("Executing query:", query);
    console.log("With values:", values);

    const [rows] = await connection.query(query, values);
    connection.release();
    console.log(rows);
    res.status(200).json(rows);
  } catch (error) {
    console.error("Error during matchmaking:", error);
    res.status(500).json({ message: "Server error" });
  }
});

app.get(
  "/pending-requests/:userId",
  asyncHandler(async (req, res) => {
    const userId = req.params.userId;

    try {
      const connection = await pool.getConnection();
      try {
        const [results] = await connection.query(
          `SELECT mr.id, mr.requester_id, mr.requested_id, mr.status, mr.timestamp, 
                        u.firstName, u.lastName, u.profilePicURL, u.email as otherEmail
                 FROM match_requests mr
                 JOIN user u ON mr.requester_id = u.id
                 WHERE mr.requested_id = ? AND mr.status = 'pending'`,
          [userId]
        );
        res.json(results);
      } finally {
        connection.release();
      }
    } catch (err) {
      console.error("Error executing query:", err);
      res.status(500).send("Internal server error");
    }
  })
);

app.post("/send-match-request", async (req, res) => {
  const { id, targetID } = req.body;
  console.log(req.body);
  if (!id || !targetID) {
    return res.status(400).json("Please provide both userId and targetUserId");
  }

  try {
    const connection = await pool.getConnection();

    const [existingRequests] = await connection.query(
      `SELECT * FROM match_requests WHERE requester_id = ? AND requested_id = ?`,
      [id, targetID]
    );

    if (existingRequests.length > 0) {
      connection.release();
      return res.status(400).json("Match request already sent");
    }

    const [result] = await connection.query(
      `INSERT INTO match_requests (requester_id, requested_id, status) VALUES (?, ?, 'pending')`,
      [id, targetID]
    );

    connection.release();
    res.status(201).json({
      id: result.insertId,
      requester_id: id,
      requested_id: targetID,
      status: "pending",
    });
  } catch (error) {
    console.error("Error during match request creation:", error);
    res.status(500).json("Server error");
  }
});

app.post("/addfriend", async (req, res) => {
  const { email, email2 } = req.body;
  console.log(email, email2);
  console.log("entered addfriend");
  try {
    const connection = await pool.getConnection();
    const [result] = await connection.query(
      "INSERT INTO friend(user_1, user_2) VALUES (?, ?);",
      [email, email2]
    );
    connection.release();

    // Check if the insert was successful
    if (result.affectedRows > 0) {
      res.status(200).json({
        message: "Friend added successfully",
        data: {
          user_1: email,
          user_2: email2,
        },
      });
    } else {
      res.status(400).json({
        message: "Failed to add friend",
      });
    }
  } catch (error) {
    //if it is an error then they're already friends technically but this could lead to a bug later for example if the database is down
    //it could display theyre firends when theyre not but that's fine ig cause in everything we will check if they are friends anyway such as in chat

    console.error("Already your friend!", error);
    res.status(500).json({
      message: "Already friends!",
    });
  }
});

app.delete("/delete-match-request", async (req, res) => {
  const { requester_id, requested_id } = req.body;

  if (!requester_id || !requested_id) {
    return res
      .status(400)
      .json("Please provide both requester_id and requested_id");
  }

  try {
    const connection = await pool.getConnection();

    // First attempt: Check if the request matches the given IDs
    let [result] = await connection.query(
      `DELETE FROM match_requests WHERE requester_id = ? AND requested_id = ?`,
      [requester_id, requested_id]
    );

    // If no rows were deleted, attempt the reverse
    if (result.affectedRows === 0) {
      [result] = await connection.query(
        `DELETE FROM match_requests WHERE requester_id = ? AND requested_id = ?`,
        [requested_id, requester_id]
      );
    }

    connection.release();

    if (result.affectedRows > 0) {
      res.status(200).json("Match request deleted successfully");
    } else {
      res.status(404).json("Match request not found");
    }
  } catch (error) {
    console.error("Error during match request deletion:", error);
    res.status(500).json("Server error");
  }
});

app.post("/tournament/signup", async (req, res) => {
  const { tournament_id, team_name, user_id } = req.body;

  if (!tournament_id || !user_id || !team_name) {
    return res
      .status(400)
      .json("Please provide tournament_id, user_id, and team_name");
  }

  try {
    const connection = await pool.getConnection();

    // Check if the user is already registered for the tournament
    const [existingRegistration] = await connection.query(
      "SELECT * FROM team WHERE tournament_id = ? AND captain_id = ?",
      [tournament_id, user_id]
    );

    if (existingRegistration.length > 0) {
      connection.release();
      return res
        .status(400)
        .json("User already registered for this tournament");
    }

    // Fetch the current number of remaining teams for the tournament
    const [tournamentInfo] = await connection.query(
      "SELECT remainingTeams FROM tournament WHERE tournament_id = ?",
      [tournament_id]
    );

    if (tournamentInfo.length === 0) {
      connection.release();
      return res.status(404).json("Tournament not found");
    }

    const remainingTeams = tournamentInfo[0].remaining_teams;

    if (remainingTeams <= 0) {
      connection.release();
      return res.status(400).json("No remaining spots in this tournament");
    }

    // Register the user for the tournament
    const [result] = await connection.query(
      "INSERT INTO team (tournament_id, captain_id, team_name) VALUES (?, ?, ?)",
      [tournament_id, user_id, team_name]
    );

    // Decrease the remaining teams count by 1
    await connection.query(
      "UPDATE tournament SET remainingTeams = remainingTeams - 1 WHERE tournament_id = ?",
      [tournament_id]
    );

    connection.release();

    console.log();
    res.status(201).json({
      message: "Successfully Registered!",
    });
  } catch (error) {
    console.error("Error during tournament sign-up:", error);
    res.status(500).json("Server error");
  }
});

app.post("/tournament/withdraw", async (req, res) => {
  const { tournament_id, user_id } = req.body;

  if (!tournament_id || !user_id) {
    return res
      .status(400)
      .json("Please provide both tournament_id and user_id");
  }

  try {
    const connection = await pool.getConnection();

    // Check if the user is registered for the tournament and get the team information
    const [teamInfo] = await connection.query(
      "SELECT team_id FROM team WHERE tournament_id = ? AND captain_id = ?",
      [tournament_id, user_id]
    );

    if (teamInfo.length === 0) {
      connection.release();
      return res.status(404).json("No team found for this tournament and user");
    }

    const team_id = teamInfo[0].team_id;

    // Remove the team from the tournament
    await connection.query(
      "DELETE FROM team WHERE tournament_id = ? AND team_id = ?",
      [tournament_id, team_id]
    );

    // Increase the remaining teams count by 1
    await connection.query(
      "UPDATE tournament SET remaining_teams = remaining_teams + 1 WHERE tournament_id = ?",
      [tournament_id]
    );

    connection.release();
    res.status(200).json("Successfully withdrew from tournament");
  } catch (error) {
    console.error("Error during tournament withdrawal:", error);
    res.status(500).json("Server error");
  }
});

app.get("/matchmake-experts", protect, async (req, res) => {
  const { location, gender, sport } = req.query;

  try {
    const connection = await pool.getConnection();

    let query = `SELECT user.id, user.firstName, user.lastName, user.gender, user.profilePicURL, user.location, user.email, plays.skillLevel AS level 
                     FROM user
                     JOIN plays ON user.email = plays.email
                     WHERE plays.sport_name = ? AND plays.skillLevel = 'pro'`;
    let conditions = [];
    let values = [sport];

    if (location && location !== "null") {
      conditions.push("user.location = ?");
      values.push(location);
    }
    if (gender && gender !== "null") {
      conditions.push("user.gender = ?");
      values.push(gender);
    }

    if (conditions.length > 0) {
      query += " AND " + conditions.join(" AND ");
    }

    console.log("Executing query:", query);
    console.log("With values:", values);

    const [rows] = await connection.query(query, values);
    connection.release();
    console.log(rows);
    res.status(200).json(rows);
  } catch (error) {
    console.error("Error during matchmaking:", error);
    res.status(500).json({ message: "Server error" });
  }
});

app.get("/upcoming-tournaments", async (req, res) => {
  try {
    const connection = await pool.getConnection();
    const currentDate = new Date().toISOString().slice(0, 19).replace("T", " "); // Current date in MySQL datetime format

    const [rows] = await connection.query(
      "SELECT * FROM tournament WHERE start_date > ?",
      [currentDate]
    );

    connection.release();
    console.log(rows);
    res.status(200).json(rows);
  } catch (error) {
    console.error("Error fetching upcoming tournaments:", error);
    res.status(500).json({ message: "Server error" });
  }
});

app.post("/changePassword", async (req, res) => {
  const { email, oldPassword, newPassword } = req.body;
  console.log("ENTERED CHANGING PASSWORD");
  try {
    // Fetch user's current hashed password from the database
    const connection = await pool.getConnection();
    console.log("ab to run query");
    const [rows] = await connection.query(
      "SELECT * FROM user WHERE email = ?",
      [email]
    );
    connection.release();

    if (rows.length === 0) {
      return res.status(400).json("User not found");
    }
    console.log("user recognised");

    const user = rows[0];
    const currentHashedPassword = user.password;

    // Compare oldPassword with the hashed password in the database
    const isMatch = await bcrypt.compare(oldPassword, currentHashedPassword);

    if (isMatch) {
      // Hash the new password before updating in the database
      const hashedNewPassword = await bcrypt.hash(newPassword, 10); // Adjust salt rounds as needed

      // Update the user's password in the database
      const updateConnection = await pool.getConnection();
      await updateConnection.query(
        "UPDATE user SET password = ? WHERE email = ?",
        [hashedNewPassword, email]
      );
      updateConnection.release();

      // Respond with success message
      console.log("changed the pass");
      res.status(200).json({ message: "Password updated successfully" });
    } else {
      console.log("unmatching pass");
      // Passwords do not match, respond with error
      res.status(400).json("Incorrect old password");
    }
  } catch (error) {
    console.log("some weird error");
    console.error("Error during password change:", error);
    res.status(500).json("Server error");
  }
});
app.post("/updatelocation", async (req, res) => {
  const { email, location } = req.body;

  try {
    const connection = await pool.getConnection();
    const [result] = await connection.query(
      "UPDATE user SET location = ? WHERE email = ?",
      [location, email]
    );
    connection.release();
    console.log(result.affectedRows);
    if (result.affectedRows > 0) {
      res.status(200).json({ message: "Location updated successfully" });
    } else {
      res
        .status(404)
        .json({ message: "User not found or Location not updated" });
    }
  } catch (error) {
    console.error("Error updating profile picture URL:", error);
    res.status(500).json({ message: "Server error" });
  }
});

app.post("/updatebio", async (req, res) => {
  const { email, bio } = req.body;
  console.log("ENTERED: " + email + " " + bio);
  try {
    const connection = await pool.getConnection();
    const [result] = await connection.query(
      "UPDATE user SET bio = ? WHERE email = ?",
      [bio, email]
    );
    connection.release();

    if (result.affectedRows > 0) {
      res.status(200).json({ message: "Bio updated successfully" });
    } else {
      res.status(404).json({ message: "User not found or bio not updated" });
    }
  } catch (error) {
    console.error("Error updating bio:", error);
    res.status(500).json({ message: "Server error" });
  }
});

// API endpoint to fetch friends list
app.get("/api/friends", async (req, res) => {
    const { userEmail } = req.query;
    let connection;
    try {
      connection = await pool.getConnection();
      const [results] = await connection.query(
        `
          SELECT u.email, u.username AS name, u.profilePicURL AS profilePicUrl,
                 (SELECT c.message 
                  FROM chat AS c 
                  WHERE (c.sender_email = u.email AND c.receiver_email = ?)
                     OR (c.receiver_email = u.email AND c.sender_email = ?)
                  ORDER BY c.timestamp DESC LIMIT 1) AS last_message
          FROM friend f
          INNER JOIN user u ON f.user_2 = u.email
          WHERE f.user_1 = ? 
          UNION 
          SELECT u.email, u.username AS name, u.profilePicURL AS profilePicUrl,
                 (SELECT c.message 
                  FROM chat AS c 
                  WHERE (c.sender_email = u.email AND c.receiver_email = ?)
                     OR (c.receiver_email = u.email AND c.sender_email = ?)
                  ORDER BY c.timestamp DESC LIMIT 1) AS last_message
          FROM friend f
          INNER JOIN user u ON f.user_1 = u.email
          WHERE f.user_2 = ?;
        `,
        [userEmail, userEmail, userEmail, userEmail, userEmail, userEmail]
      );
      res.json(results);
    } catch (err) {
      console.error("Error fetching friends:", err);
      res.status(500).json({ message: "Server error" });
    } finally {
      if (connection) connection.release();
    }
  });
  

// API endpoint to fetch user details by email
app.get("/api/user", async (req, res) => {
  const { userEmail } = req.query;
  try {
    const connection = await pool.getConnection();
    const [results] = await connection.query(
      "SELECT username AS name, profilePicURL AS profilePicUrl FROM user WHERE email = ?",
      [userEmail]
    );
    connection.release();

    if (results.length > 0) {
      const user = {
        name: results[0].name,
        profilePicUrl: results[0].profilePicUrl,
      };
      res.json(user);
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (err) {
    console.error("Error fetching user details:", err);
    res.status(500).json({ message: "Server error" });
  }
});

// API endpoint to fetch chats between two users
app.get("/api/chats", async (req, res) => {
  const { user1_email, user2_email } = req.query;
  console.log("I am called!");
  try {
    const connection = await pool.getConnection();
    const [results] = await connection.query(
      `
            SELECT * FROM chat
            WHERE (sender_email = ? AND receiver_email = ?)
               OR (sender_email = ? AND receiver_email = ?)
            ORDER BY timestamp
        `,
      [user1_email, user2_email, user2_email, user1_email]
    );
    connection.release();
    // console.log("Chats: ");
    // console.log(results);
    res.status(200).json(results);
  } catch (err) {
    console.error("Error fetching chats:", err);
    res.status(500).json({ message: "Server error" });
  }
});

// API endpoint to send a new chat message
app.post("/api/chats", async (req, res) => {
  const { sender_email, receiver_email, message } = req.body;
  try {
    const connection = await pool.getConnection();
    const [result] = await connection.query(
      "INSERT INTO chat (sender_email, receiver_email, message) VALUES (?, ?, ?)",
      [sender_email, receiver_email, message]
    );
    connection.release();
    res.json({ status: "success" });
  } catch (err) {
    console.error("Error sending message:", err);
    res.status(500).json({ message: "Server error" });
  }
});

app.get("/tournaments/my", async (req, res) => {
  const { userID } = req.query;
  console.log(userID);
  try {
    const connection = await pool.getConnection();

    // Query to find teams where captain_id matches the userID
    const [teamResults] = await connection.query(
      "SELECT tournament_id FROM team WHERE captain_id = ?",
      [userID]
    );

    const tournamentIDs = teamResults.map((row) => row.tournament_id);

    if (tournamentIDs.length === 0) {
      connection.release();
      return res.json([]);
    }

    // Query to get tournament details
    const [tournamentResults] = await connection.query(
      "SELECT * FROM tournament WHERE tournament_id IN (?)",
      [tournamentIDs]
    );

    connection.release();
    console.log(tournamentResults);
    res.json(tournamentResults);
  } catch (err) {
    console.error("Error fetching tournaments:", err);
    res.status(500).json({ message: "Server error" });
  }
});

app.post("/uploadPicture", async (req, res) => {
  const { email, urlPic } = req.body;
  console.log(urlPic + " is url");
  console.log("the entrace");
  console.log(email);
  if (!email || !urlPic) {
    return res
      .status(400)
      .json({ success: false, message: "Email and Picture URL are required." });
  }

  try {
    const connection = await pool.getConnection();
    console.log("right before query");
    const query = "INSERT INTO userpics (email, urlPic) VALUES (?, ?)";
    const [results] = await connection.query(query, [email, urlPic]);
    connection.release();
    console.log("arrival into succes");

    res
      .status(200)
      .json({ success: true, message: "Picture URL uploaded successfully." });
  } catch (err) {
    console.error("Error inserting data:", err);
    res
      .status(500)
      .json({ success: false, message: "Error inserting data into database." });
  }
});

app.post("/updateprofilepic", async (req, res) => {
  const { email, profilePicURL } = req.body;

  try {
    const connection = await pool.getConnection();
    const [result] = await connection.query(
      "UPDATE user SET profilePicURL = ? WHERE email = ?",
      [profilePicURL, email]
    );
    connection.release();

    if (result.affectedRows > 0) {
      res
        .status(200)
        .json({ message: "Profile picture URL updated successfully" });
    } else {
      res
        .status(404)
        .json({ message: "User not found or profile picture URL not updated" });
    }
  } catch (error) {
    console.error("Error updating profile picture URL:", error);
    res.status(500).json({ message: "Server error" });
  }
});
