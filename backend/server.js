const express = require("express");
const path = require("path")
require('dotenv').config({ path: path.join(__dirname, '.env') })
const http = require("http")
const https = require("https")
const fs = require("fs")
const ws = require('ws')
const jwt = require("jsonwebtoken")
const db = require("./db")
const authRoutes = require("./routes/auth.routes")
const userRoutes = require("./routes/user.routes")
const recommendationRoutes = require("./routes/recommendation.routes")
const browseRoutes = require("./routes/browse.routes")
const reviewRoutes = require("./routes/review.routes")
const appointmentRoutes = require("./routes/appointment.routes")
const adminRoutes = require("./routes/admin.routes")
const conversationRoutes = require("./routes/conversation.routes")

const mongoUrl = process.env.MONGODB_URI
const env = process.env.ENV
const PORT = 80

db.mongoose
    .connect(mongoUrl, {
        useNewUrlParser: true,
        useUnifiedTopology: true
    })
    .then(() => {
        console.log("Successfully connected to MongoDB")
    })
    .catch(err => {
        console.error("Connection error", err)
        throw err
    })

const app = express();

// parse requests of content-type - application/json
app.use(express.json());

// parse requests of content-type - application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: true }));
app.use(express.static(__dirname, { dotfiles: 'allow' } ));

authRoutes(app)
userRoutes(app)
recommendationRoutes(app)
browseRoutes(app)
reviewRoutes(app)
appointmentRoutes(app)
adminRoutes(app)
conversationRoutes(app)

app.get("/health", (req, res) => {
    return res.status(200).send({message: 'OK'})
})

if (env === 'prod') {
    // PRODUCTION
    const privateKeyFile = "/etc/letsencrypt/live/edumatch.canadacentral.cloudapp.azure.com/privkey.pem"
    const certFile = "/etc/letsencrypt/live/edumatch.canadacentral.cloudapp.azure.com/fullchain.pem"
    const chainFile = "/etc/letsencrypt/live/edumatch.canadacentral.cloudapp.azure.com/chain.pem"

    const privateKey = fs.readFileSync(privateKeyFile, 'utf8');
    const certificate = fs.readFileSync(certFile, 'utf8');
    const ca = fs.readFileSync(chainFile, 'utf8');
    const secretKey = process.env.SECRET_KEY

    const credentials = {
        key: privateKey,
        cert: certificate,
        ca
    };
    // Starting both http & https servers
    const httpServer = http.createServer(app);
    const httpsServer = https.createServer(credentials, app);

    const Conversation = db.conversation
    const clients = new Map() // Currently connected client sockets
    
    const wss = new ws.Server({ server: httpsServer })

    // ChatGPT usage: No
    wss.on('connection', (ws, req) => {
        console.log("client connected to chat socket")
        const token = req.url.split('?token=')[1]
        if (token) {
            jwt.verify(token, secretKey, (err, userId) => {
                if (err) {
                    console.log(err)
                    ws.close()
                } else {
                    clients.set(userId, ws);
                    ws.on('message', (message) => {
                        try {
                            const data = JSON.parse(message)
                            const receiverUserId = data.receiverId
                            const messageText = data.message

                            if (!receiverUserId || !messageText) {
                                console.log("Error in received message format")
                            } else {
                                const currentDate = new Date()

                                const messageToSend = {
                                    senderId: userId,
                                    content: messageText,
                                    timestamp: currentDate.toUTCString()
                                };
                
                                // Send to receiver client socket if online
                                if (clients.has(receiverUserId)) 
                                    clients.get(receiverUserId).send(JSON.stringify(messageToSend))
    
                                // Update conversation in database
                                Conversation.findOne({
                                    $or: [
                                        {
                                            'participants.userId1': userId,
                                            'participants.userId2': receiverUserId
                                        },
                                        {
                                            'participants.userId1': receiverUserId,
                                            'participants.userId2': userId
                                        }
                                    ]
                                }).then(conversation => {
                                    if (!conversation)
                                        console.log(
                                            "Conversation between " + 
                                            userId + " and " + 
                                            receiverUserId + " not found"
                                        )
                                    else {
                                        conversation.messages.push(messageToSend)
                                        conversation.save()
                                    }
                                }).catch(error => {
                                    console.log("Error sending message to conversation in database "+ error)
                                })
                            }
                        } catch (error) {
                            console.log("Error processing message event: " + error)
                        }
                    })
                    ws.on('close', () => {
                        clients.delete(userId)
                    })
                }
            })
        } else {
            console.log("Missing token")
            ws.close()
        } 
    });

    httpServer.listen(80, () => {
        console.log('HTTP Server running on port 80');
    });

    httpsServer.listen(443, () => {
        console.log('HTTPS Server running on port 443');
    });

    process.on('uncaughtException', (error) => {
        console.error('Uncaught Exception:', error)
    });
} else {
    // LOCAL
    app.listen(PORT, () => {
        console.log(`Server is running on port ${PORT} locally.`);
    });
}