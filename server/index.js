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
    userid : String,
    name : String,
    phone : String
});
var User = mongoose.model('user', userSchema);

var gameSchema = new Schema({
    type : Boolean,
    playtime : Date,
    player1 : String,
    player2 : String,
    player3 : String,
    player4 : String,
    court : [{type: Schema.ObjectId, ref: 'Court'}],
    winner : Boolean,
    isMatched : Boolean,
    score : String
});
var Game = mongoose.model('game', gameSchema);

var courtSchema = new Schema({
    name : String,
    info : String
});
var Court = mongoose.model('court', courtSchema);



/////////*    Server Implementation     */////////



app.listen(3000, function() {console.log("Listening on port #3000")});
