const db = require("../db")
const { LocationMode } = require("../constants/location.modes");
const haversine = require('haversine')

const User = db.user

exports.checkedProfile = async (req, res) => {
    const tutor = await User.findById(req.body.tutorId)
    const tutee = await User.findById(req.userId)
    const distance = haversine(tutor.location, tutee.location)

    tutee.recommendationWeights.minRating -= (tutee.recommendationWeights.minRating - tutor.rating) * (tutor.rating < tutee.recommendationWeights.minRating ? 0.15 : 0.05)
    tutee.recommendationWeights.locationModeWeight -= (tutor.locationMode != tutee.locationMode ? 0.01 : -0.005)

    if (tutee.locationMode == LocationMode.IN_PERSON)
        tutee.recommendationWeights.maxDistance += (distance - tutee.recommendationWeights.maxDistance) * (distance > tutee.recommendationWeights.maxDistance ? 0.15 : 0.02)

    tutee.save()
    res.status(200).send({ message: "Adjusted weights based on checked profile"})
}

exports.contactedTutor = async (req, res) => {
    const tutor = await User.findById(req.body.tutorId)
    const tutee = await User.findById(req.userId)
    const distance = haversine(tutor.location, tutee.location)

    if (tutor.subjectHourlyRate.length != 0) {
        const averageHourlyRate = tutor.subjectHourlyRate.reduce((acc, subject) => acc + subject.hourlyRate) / tutor.subjectHourlyRate.length
        tutee.recommendationWeights.budget += (averageHourlyRate - tutee.recommendationWeights.budget) * (averageHourlyRate > tutee.recommendationWeights.budget ? 0.15 : 0.05)
    }

    tutee.recommendationWeights.minRating -= (tutee.recommendationWeights.minRating - tutor.rating) * (tutor.rating < tutee.recommendationWeights.minRating ? 0.3 : 0.05)
    tutee.recommendationWeights.locationModeWeight -= (tutor.locationMode != tutee.locationMode ? 0.05 : -0.01)

    if (tutee.locationMode == LocationMode.IN_PERSON)
        tutee.recommendationWeights.maxDistance += (distance - tutee.recommendationWeights.maxDistance) * (distance > tutee.recommendationWeights.maxDistance ? 0.2 : 0.02)

    tutee.save()
    res.status(200).send({ message: "Adjusted weights based on contacted tutor"})
}

exports.scheduledAppointment = async (req, res) => {
    const tutor = await User.findById(req.body.tutorId)
    const tutee = await User.findById(req.userId)
    const distance = haversine(tutor.location, tutee.location)
    const scheduledSubjectHourlyRate = tutor.subjectHourlyRate.find(subject => subject.course == req.body.scheduledSubject)
    if (!scheduledSubjectHourlyRate)
        res.status(500).send({ message: "Unable to find hourly rate associated with subject" })

    tutee.recommendationWeights.budget += (scheduledSubjectHourlyRate - tutee.recommendationWeights.budget) * (scheduledSubjectHourlyRate > tutee.recommendationWeights.budget ? 0.5 : 0.1)

    tutee.recommendationWeights.minRating -= (tutee.recommendationWeights.minRating - tutor.rating) * (tutor.rating < tutee.recommendationWeights.minRating ? 0.5 : 0.1)
    tutee.recommendationWeights.locationModeWeight -= (tutor.locationMode != tutee.locationMode ? 0.1 : -0.02)

    if (tutee.locationMode == LocationMode.IN_PERSON)
        tutee.recommendationWeights.maxDistance += (distance - tutee.recommendationWeights.maxDistance) * (distance > tutee.recommendationWeights.maxDistance ? 0.4 : 0.1)

    tutee.previousSubjects.push(req.body.scheduledSubject)

    tutee.save()
    res.status(200).send({ message: "Adjusted weights based on scheduled appointment"})
}

exports.reviewedTutor = async (req, res) => {
    const tutor = await User.findById(req.body.tutorId)
    const tutee = await User.findById(req.userId)
    const distance = haversine(tutor.location, tutee.location)
    const reviewFactor = req.body.review * 0.1

    if (tutor.subjectHourlyRate.length != 0) {
        const averageHourlyRate = tutor.subjectHourlyRate.reduce((acc, subject) => acc + subject.hourlyRate) / tutor.subjectHourlyRate.length
        tutee.recommendationWeights.budget += (averageHourlyRate - tutee.recommendationWeights.budget) * (averageHourlyRate > tutee.recommendationWeights.budget ? 0.15 : 0.05) * reviewFactor
    }

    tutee.recommendationWeights.minRating -= (tutee.recommendationWeights.minRating - tutor.rating) * (tutor.rating < tutee.recommendationWeights.minRating ? 0.3 : 0.05) * reviewFactor
    tutee.recommendationWeights.locationModeWeight -= (tutor.locationMode != tutee.locationMode ? 0.05 : -0.01) * reviewFactor

    if (tutee.locationMode == LocationMode.IN_PERSON)
        tutee.recommendationWeights.maxDistance += (distance - tutee.recommendationWeights.maxDistance) * (distance > tutee.recommendationWeights.maxDistance ? 0.2 : 0.02) * reviewFactor

    tutee.save()
    res.status(200).send({ message: "Adjusted weights based on reviewed tutor"})
}