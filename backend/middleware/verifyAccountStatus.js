const db = require("../db")
const User = db.user


function verifyAccountStatus(req, res, next) {
    try {
        var userId = req.userId
        User.findById(userId)
            .then(user => {
                if (!user || user.isBanned) {
                    return res.status(403).send({ message: "User is not found or banned"}); // Forbidden 
                }
                next()
                return
            })
    } catch (err) {
        console.log(err)
        return res.status(500).send({ message: err.message })
    }
    
}

const account = {
    verifyAccountStatus
}

module.exports = account