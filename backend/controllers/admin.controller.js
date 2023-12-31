const db = require("../db")
const { UserType } = require("../constants/user.types");

const User = db.user
const Conversation = db.conversation

// ChatGPT usage: No
exports.ban = async (req, res) => {
    // ban
    try {
        const admin = await User.findById(req.userId)
        if (admin.type != UserType.ADMIN)
            return res.status(401).send({ message: "User is not admin and is not authorized to ban" })
    
        const user = await User.findById(req.body.userId)
        if (!user) {
            return res.status(404).send({message: "User not found"})
        }
    
        if (user.type == UserType.ADMIN)
            return res.status(401).send({ message: "User is admin and can't be banned" })
    
        user.isBanned = true
        await user.save()
        
        return res.status(200).send({ message: "User with id " + user._id + " was banned successfully" })
    } catch (err) {
        console.log(err)
        return res.status(500).send({ message: err.message })
    }
}

// ChatGPT usage: No
exports.unban = async (req, res) => {
    // unban
    try {
        const admin = await User.findById(req.userId)
        if (admin.type != UserType.ADMIN)
            return res.status(401).send({ message: "User is not admin and is not authorized to unban" })
    
        const user = await User.findById(req.body.userId)
        if (!user) {
            return res.status(404).send({message: "User not found"})
        }
        user.isBanned = false
        await user.save()
        
        return res.status(200).send({ message: "User with id " + user._id + " was unbanned successfully" })
    } catch (err) {
        console.log(err)
        return res.status(500).send({ message: err.message })
    }
    
}

// ChatGPT usage: No
exports.getUsers = async (req, res) => {
    // getUsers
    try {
        const admin = await User.findById(req.userId)
        if (admin.type != UserType.ADMIN)
            return res.status(401).send({ message: "User is not admin and is not authorized to view user list" })
    
        const users = await User.find({})
    
        return res.status(200).json({
            users: users.map(user => ({
                userId: user._id,
                username: user.username,
                displayedName: user.displayedName,
                type: user.type,
                isBanned: user.isBanned
            })).sort((a, b) => a.displayedName.localeCompare(b.displayedName))
        })
    } catch (err) {
        console.log(err)
        return res.status(500).send({ message: err.message })
    }
    
}

// ChatGPT usage: No
exports.getProfile = async (req, res) => {
    // getProfile
    try {
        const admin = await User.findById(req.userId)
        if (admin.type != UserType.ADMIN)
            return res.status(401).send({ message: "User is not admin and is not authorized to view user profile and messages" })
    
        const user = await User.findById(req.query.userId)
    
        if (!user) {
            return res.status(404).send({ message: "Could not find user in database with provided id" })
        }
        const userReviews = await User.aggregate()
            .unwind('$userReviews')
            .match({'userReviews.reviewerId': req.query.userId})
            .project({ 
                '_id': 0,
                'comment': '$userReviews.comment'
            })
    
        const userConversations = await Conversation.find({
            $or: [
                { 'participants.userId1': req.query.userId },
                { 'participants.userId2': req.query.userId }
            ]
        })
        console.log(userReviews)
    
        const userMessages = []
        userConversations.forEach(conversation => {
            const filteredMessages = conversation.messages.filter(message => {
                return message.senderId == req.query.userId
            })
    
            userMessages.push(...filteredMessages)
        })
    
        return res.status(200).json({
            bio: user.bio,
            reviews: userReviews,
            messages: userMessages
        })
    } catch (err) {
        console.log(err)
        return res.status(500).send({ message: err.message })
    }
}
