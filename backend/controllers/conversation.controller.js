const db = require("../db")
const mongoose = require('mongoose')

const User = db.user
const Conversation = db.conversation

exports.getList = async (req, res) => {
    const user = await User.findById(req.userId)
    if (!user)
        return res.status(404).send({ message: "Could not find user in database with provided id" })

    const conversationList = await Conversation.find({
        $or: [
            { 'participants.displayedName1': user.displayedName },
            { 'participants.displayedName2': user.displayedName }
        ]
    })
    return res.status(200).json({
        conversations: conversationList.map(conversation => ({
            conversationId: conversation._id,
            conversationName: conversation.participants.displayedName1 == user.displayedName ? conversation.participants.displayedName2 : conversation.participants.displayedName1
        }))
    })
}

exports.getConversation = async (req, res) => {
    if (req.query.page < 1)
        return res.status(400).send({ message: "Page number cannot be less than 1" })

    const user = await User.findById(req.userId)
    if (!user)
        return res.status(404).send({ message: "Could not find user in database with provided id" })

    if (!mongoose.Types.ObjectId.isValid(req.query.conversationId)) {
        return res.status(400).send({ message: "Invalid provided conversationId" })
    }

    const conversation = await Conversation.findById(req.query.conversationId)
    if (!conversation)
        return res.status(404).send({ message: "Conversation not found" })

    const endIndex = conversation.messages.length - (req.query.page - 1) * 10
    if (endIndex < 0)
        return res.status(200).json({
            otherUserId: conversation.participants.userId1 == req.userId ? conversation.participants.userId2 : conversation.participants.userId1,
            messages: []
        })
    else {
        const startIndex = endIndex - 10

        return res.status(200).json({
            otherUserId: conversation.participants.userId1 == req.userId ? conversation.participants.userId2 : conversation.participants.userId1,
            messages: startIndex < 0 ? conversation.messages.slice(0, endIndex) : conversation.messages.slice(startIndex, endIndex)
        })
    }
}

exports.create = async (req, res) => {
    const user = await User.findById(req.userId)
    if (!user)
        return res.status(404).send({ message: "Could not find creating user in database with provided id" })

    if (!mongoose.Types.ObjectId.isValid(req.body.otherUserId)) {
        return res.status(400).send({ message: "Invalid provided userId" })
    }
    const otherUser = await User.findById(req.body.otherUserId)
    if (!otherUser)
        return res.status(404).send({ message: "Could not find other user in database with provided id" })

    const existingConversation = await Conversation.findOne({
        $or: [
            {
                'participants.userId1': req.userId,
                'participants.userId2': req.body.otherUserId
            },
            {
                'participants.userId1': req.body.otherUserId,
                'participants.userId2': req.userId
            }
        ]
    })

    if (existingConversation) {
        return res.status(400).send({ message: "There already exists a conversation between these two users in the database" })
    } else {
        const newConversation = new Conversation({
            participants:
                {
                    userId1: req.userId,
                    userId2: req.body.otherUserId,
                    displayedName1: user.displayedName,
                    displayedName2: otherUser.displayedName
                },
            messages: []
        })
        
        await newConversation.save()

        res.status(200).json({
            conversationId: newConversation._id,
            conversationName: otherUser.displayedName
        })
    }
}
