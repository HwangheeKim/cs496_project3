var express = require('express');
var multer = require('multer');
var bodyParser = require('body-parser');
var path = require('path');
var fs = require('fs');
var http = require('http');
var https = require('https');
var FCM = require('fcm-push');
var serverKey = 'AAAAHccdtZc:APA91bHQk-WzrFgv0YizDarCa9AuOV61d7Bv7CUkY7HMhxcShwenrVZqmF8day-vWoVq6HNlB7wlm57raeS2hGROCZqImSiWgLJoZ9IEoMNeDldNZpNsx9RBUvGms9ooBcDqAm5TgWVp';
var fcm = new FCM(serverKey);

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
    userToken : {type:String, required:true},
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
    latitute : {type:Number, default:""},
    longitute : {type:Number, default:""},
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


// Get request for all game information
app.get('/game/ongoing', function(req, res) {
    Game.find({score:""}, function(err, results) {
        if (err) throw err;
        
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify(results));
        res.end();
    });
});

// GET request for game with that user joined
app.get('/game/joined/:userID', function(req, res) {
    var uid = req.params.userID;
    var query = {$and: [{score:""}, {$or:[{player1:uid}, {player2:uid}, {player3:uid}, {player4:uid}]}]};
    Game.find(query, function(err, results) {
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

// GET request for user's record
app.get('/user/record/:userID', function(req, res) {
    console.log("[GET/user/record] Request on " + req.params.userID);
    var win = 0;
    var lose = 0;

    var amiplayer12 = {$or : [{player1:req.params.userID}, {player2:req.params.userID}]};
    var amiplayer34 = {$or : [{player3:req.params.userID}, {player4:req.params.userID}]};

    var iam12andwin = {$and : [amiplayer12, {winner:true}]};
    var iam34andwin = {$and : [amiplayer34, {winner:false}]};
    var iam12andlose = {$and : [amiplayer12, {winner:false}]};
    var iam34andlose = {$and : [amiplayer34, {winner:true}]};

    var iwon = {$or : [iam12andwin, iam34andwin]};
    var ilose = {$or : [iam12andlose, iam34andlose]};

    var winquery = {$and : [{score : {$ne : ""}}, iwon]};
    var losequery = {$and : [{score : {$ne : ""}}, ilose]};

    console.log("[GET/user/record] Entering the query");
    Game.find(winquery, function(err1, result1) {
        if (err1) throw err1;

        win = result1.length;
        Game.find(losequery, function(err2, result2) {
            if (err2) throw err2;

            lose = result2.length;

            res.writeHead(200, {'Content-Type' : 'application/json'});
            res.write(JSON.stringify({win:win, lose:lose}));
            res.end();
        });
    });
});

// POST request for user enrollment
app.post('/user/enroll', function(req, res) {
    console.log("[User/enroll] Got request");

    User.find({userID:req.body['userID']}, function(err, result) {
        console.log("        " + result)
        if(result.length>0) {
            var newUser = {};
            
            if(req.body['name']) newUser.name=req.body['name'];
            if(req.body['userToken']) newUser.userToken = req.body['userToken'];
            if(req.body['picture']) newUser.picture=req.body['picture'];
            if(req.body['email']) newUser.email=req.body['email'];
            if(req.body['group']) newUser.group=req.body['group'];
            if(req.body['phone']) newUser.phone=req.body['phone'];

            User.findOneAndUpdate( {userID:req.body['userID']}, newUser, {}, function (err, results) {
                if (err) throw err;
                console.log("DONE UPDATE USER " + results);
            });
        } else {
            var newUser = {
                userID : req.body['userID'],
                userToken : req.body['userToken'],
                name : req.body['name'],
                picture : notnull(req.body['picture']),
                email : notnull(req.body['email']),
                group : notnull(req.body['group']),
                phone : notnull(req.body['phone'])
            };

            User.findOneAndUpdate( {userID : req.body['userID']}, newUser, {upsert: true, new: true}, function (err, results) {
                if (err) throw err;
                console.log("DONE ENROLL NEW USER " + results);
            });
        }
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
        latitute : req.body['latitute'],
        longitute : req.body['longitute'],
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
    if(req.body['winner']!=null) updatedGame.winner = req.body['winner'];
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

// GET request for push notification
app.get('/game/notify/:action/:gameID/:userID', function(req, res) {
    console.log("[game/notify/"+ req.params.action + "] Get noti request :) on " + req.params.gameID + " # " + req.params.userID);
    Game.findOne({_id:req.params.gameID}, function(err, result1) {
        if (err) throw err;

        var isJoin = (req.params.action == "join");

        if(result1['player1']!="" && result1['player1']!=req.params.userID) notify(result1['player1'], isJoin);
        if(result1['player2']!="" && result1['player2']!=req.params.userID) notify(result1['player2'], isJoin);
        if(result1['player3']!="" && result1['player3']!=req.params.userID) notify(result1['player3'], isJoin);
        if(result1['player4']!="" && result1['player4']!=req.params.userID) notify(result1['player4'], isJoin);
    });
});


function notify(userID, isJoin) {
    User.findOne({userID:userID}, function(err, result) {
        if (err) throw err;
        var msgbody = "";
        if (isJoin) {
            msgbody = "New player joined your game!"
        } else {
            msgbody = "Player left the game!"
        }

        var message = {
            to: result['userToken'],
            priority: "high",
            notification: {
                title: "Tennis Together",
                body: msgbody
            }
        };

        fcm.send(message, function(err, res) {
            if (err) throw err;
            console.log("Successfully sent with response: ", res);
        })
    });
}


// GET request for game drop
app.get('/game/drop/:gameID', function(req, res) {
    console.log("[Game/drop] Got request on " + req.params.gameID);

    Game.remove({_id : req.params.gameID}, function(err) {
        if (err) throw err;

        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify({result:'OK'}));
        res.end();
    });
});

// GET request for unregister from the game
app.get('/game/cancel/:gameID/:userID', function(req, res) {
    console.log("[Game/cancel] Got request game " + req.params.gameID + " # userID " + req.params.userID);

    if (req.params.gameID.match(/^[0-9a-fA-F]{24}$/)) {
        Game.findOne({_id:req.params.gameID}, function(err, result) {
            if(result['player2']==req.params.userID) result['player2'] = "";
            if(result['player3']==req.params.userID) result['player3'] = "";
            if(result['player4']==req.params.userID) result['player4'] = "";
            Game.update({_id:req.params.gameID}, result, function(err, result1) {
                if (err) throw err;

                res.writeHead(200, {'Content-Type':'application/json'});
                res.write(JSON.stringify({result:'OK'}));
                res.end();
            });
        });
    }
});



app.listen(3000, function() {console.log("Listening on port #3000")});
