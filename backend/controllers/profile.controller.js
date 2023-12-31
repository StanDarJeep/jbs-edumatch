const db = require("../db")
const ratingController = require("./review.controller")

const User = db.user
const EXCLUDED_FIELDS = [
    "-_id",
    "-googleId",
    "-password",
    "-googleOauth",
    "-recommendationWeights",
    "-hasSignedUp"
]

const ALLOWED_TO_CHANGE = [
    "displayedName",
    "phoneNumber",
    "education",
    "subjectHourlyRate",
    "manualAvailability",
    "locationMode",
    "location",
    "bio",
]

// ChatGPT usage: No
exports.getPublicProfile = (req, res) => {
    // getPublicProfile
    
    var userId = req.query.userId
    if (!userId) {
        return res.status(400).send({ message: "Must specify userId."})
    }

    User.findById(userId).then(user => {
        if (!user || user.isBanned) {
            return res.status(404).send({ message: "User not found."})
        }
        
        var ratings = user.userReviews
        ratings.sort((fb1, fb2) => fb2.rating - fb1.rating)
        var top2Ratings
        if (ratings.length <= 2) {
            top2Ratings = ratings
        } else {
            top2Ratings = ratings.slice(0, 3)
        }

        var data = {
            displayedName: user.displayedName,
            overallRating: ratingController.getOverallRating(ratings),
            bio: user.bio,
            school: user.education.school,
            program: user.education.program,
            courses: user.education.courses,
            tags: user.education.tags,
            subjectHourlyRate: user.subjectHourlyRate,
            top2Ratings
        }
        return res.status(200).send(data)
    }).catch(err => {
        console.log(err)
        return res.status(500).send({ message: err.message })
    })
    
}

// ChatGPT usage: No
exports.getPrivateProfile = (req, res) => {
    // getPrivateProfile
    var userId = req.userId
    User.findById(userId).select(EXCLUDED_FIELDS).then(user => {
        return res.status(200).send(user)
    }).catch(err => {
        console.log(err)
        return res.status(500).send({ message: err.message })
    })
}

// ChatGPT usage: No
exports.editProfile = (req, res) => {
    // editProfile
    var userId = req.userId
    var data = {}
    var keys = Object.keys(req.body)
    for (var key of keys) {
        if (ALLOWED_TO_CHANGE.includes(key)) {
            data[key] = req.body[key]
        }
    }

    User.findByIdAndUpdate(userId, {...data}, {new: true})
        .select(EXCLUDED_FIELDS)
        .then(updatedUser => {
            return res.status(200).send(updatedUser)
        }).catch(err => {
            console.log(err)
            return res.status(500).send({ message: err.message })
        })

}

