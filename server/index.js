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
    userID : {type:String, required:true},
    name : {type:String, required:true},
    picture : {type:String, default:"http://www.ogubin.com/images/empty_profile2.png"},
    email : {type:String, default:""},
    group : {type:String, default:""},
    phone : {type:String, default:""}
});
var User = mongoose.model('user', userSchema, 'user');

var gameSchema = new Schema({
    type : {type:Boolean, default:true},
    playtime : {type:Date, default:Date.now},
    player1 : {type:String, default:""},
    player2 : {type:String, default:""},
    player3 : {type:String, default:""},
    player4 : {type:String, default:""},
    //court : [{type: Schema.ObjectId, ref: 'Court'}],
    court : {type:String, default:""},
    winner : {type:Boolean, default:false},
    isMatched : {type:Boolean, default:false},
    score : {type:String, default:""}
});
var Game = mongoose.model('game', gameSchema, 'game');

var courtSchema = new Schema({
    name : String,
    info : String
});
var Court = mongoose.model('court', courtSchema, 'court');



/////////*    Server Implementation     */////////

// Get request for all game information
app.get('/game/all', function(req, res) {
    Game.find({}, function(err, results) {
        if (err) throw err;
        
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(results));
        res.end();
    });
});

// Get request for game information
app.get('/game/:gameID', function(req, res) {
    Game.findById(req.params.gameID, function(err, result){
        if (err) throw err;

        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(result));
        res.end();
    });
});

// GET request for all user
app.get('/user/all', function(req, res) {
    User.find({}, function(err, results) {
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

function notnull(str) {
    if(str) return str;
    return "";
}

// POST request for user enrollment
app.post('/user/enroll', function(req, res) {
    console.log("[User/enroll] Got request");
    var newUser = {
        userID : req.body['userID'],
        name : req.body['name'],
        picture : notnull(req.body['picture']),
        email : notnull(req.body['email']),
        group : notnull(req.body['group']),
        phone : notnull(req.body['phone'])
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

// POST request for game update
app.post('/game/update/:gameID', function(req, res) {
    console.log("[Game/update] Got request on " + req.params.gameID);

    var updatedGame = {};

    if(req.body['type']) updatedGame.type = req.body['type'];
    if(req.body['playtime']) updatedGame.playtime = req.body['playtime'];
    if(req.body['player1']) updatedGame.player1 = req.body['player1'];
    if(req.body['player2']) updatedGame.player2 = req.body['player2'];
    if(req.body['player3']) updatedGame.player3 = req.body['player3'];
    if(req.body['player4']) updatedGame.player4 = req.body['player4'];
    if(req.body['court']) updatedGame.court = req.body['court'];
    if(req.body['winner']) updatedGame.winner = req.body['winner'];
    if(req.body['isMatched']) updatedGame.isMatched = req.body['isMatched'];
    if(req.body['score']) updatedGame.score = req.body['score'];

    console.log("[Game/update] " + JSON.stringify(updatedGame));
    Game.update({_id:req.params.gameID}, updatedGame, {upsert:false}, function(err, result) {
        if (err) throw err;

        Game.findById(req.params.gameID, function(err, result1){
            if (err) throw err;

            res.writeHead(200, {'Content-Type':'application/json'});
            res.write(JSON.stringify(result1));
            res.end();
        });
    });
});



app.listen(3000, function() {console.log("Listening on port #3000")});
