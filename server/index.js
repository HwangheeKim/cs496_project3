var express = require('express');
var multer = require('multer');
var bodyParser = require('body-parser');
var path = require('path');
var fs = require('fs');
var storage = multer.diskStorage({
    destination: function(req, file, cb) {
        cb(null, 'upload/'); 
    },
    filename: function(req, file, cb) {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});
var upload = multer({ storage: storage });
var app = new express();
app.use(bodyParser.json());

////////*    MongoDB    *////////
var mongoose = require('mongoose');
mongoose.connect('localhost', 'tennis');

// Defining Schemas and Models
var Schema = mongoose.Schema;

var userSchema = new Schema({
    userID : String,
    name : String,
    picture : String,
    email : String,
    group : String,
    phone : String
});
var User = mongoose.model('user', userSchema, 'user');

var gameSchema = new Schema({
    type : Boolean,
    playtime : Date,
    player1 : String,
    player2 : String,
    player3 : String,
    player4 : String,
    //court : [{type: Schema.ObjectId, ref: 'Court'}],
    court : String,
    winner : Boolean,
    isMatched : Boolean,
    score : String
});
var Game = mongoose.model('game', gameSchema, 'game');

var courtSchema = new Schema({
    name : String,
    info : String
});
var Court = mongoose.model('court', courtSchema, 'court');



/////////*    Server Implementation     */////////

// Get rerquest for game information
app.get('/game/all', function(req, res) {
    Game.find({}, function(err, results) {
        if (err) throw err;
        
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(results));
        res.end();
    });
});

// GET request for specific user
app.get('/user/:userID', function(req, res) {
    console.log("[GET/user/:userID] Request on " + req.params.userID);
    User.findOne({userID : req.params.userID}, function(err, result) {
        if (err) throw err
        res.writeHead(200, {'Content-Type' : 'application/json'});
        res.write(JSON.stringify(result));
        res.end();
    });
});

// POST request for user enrollment
app.post('/user/enroll', function(req, res) {
    console.log("[User/enroll] Got request");
    var newUser = {
        userID : req.body['userID'],
        name : req.body['name'],
        picture : req.body['picture'],
        email : req.body['email'],
        group : req.body['group'],
        phone : req.body['phone']
    };

    User.findOneAndUpdate( {userID : req.body['userID']}, newUser, {upsert: true, new: true}, function (err, result) {
        if (err) throw err;
        console.log("DONE PROCESS " + result)
    });


    res.writeHead(200, {'Content-Type':'application/json'});
    res.write(JSON.stringify({result: 'OK'}));
    res.end();
});

// POST request for game register
app.post('/game/register', function(req, res) {
    console.log("[Game/register] Got request");
    var newGame = new Game({
        type : req.body['type'],
        playtime : req.body['playtime'],
        player1 : req.body['player1'],
        player2 : "",
        player3 : "",
        player4 : "",
        court : req.body['court'],
        winner : false,
        isMatched : false,
        score : ""
    });

    console.log("[Game/register] playtime " + req.body['playtime']);
    
    newGame.save(function(err, result) {
        if (err) throw err;
        console.log("[Game/register] Game registered " + result);

        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify({result: 'OK'}));
        res.end();
    });
});



app.listen(3000, function() {console.log("Listening on port #3000")});
