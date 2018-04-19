var path    = require("path");
var bodyParser = require('body-parser');
var express = require('express');
var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(__dirname + '/'));

app.get('/', function (req, res) {
	res.sendFile(path.join(__dirname + '/index.html'));
});

var server = app.listen(8080, function () {
   var host = server.address().address
   var port = server.address().port
   console.log("Bluetrans app listening at http://%s:%s", host, port)
});