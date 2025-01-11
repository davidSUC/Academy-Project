const fs = require("fs");
const http = require("http");
const https = require("https");
const url = require("url");
const crypto = require("crypto");
const querystring = require("querystring");

const weatherAPI_KEY = require("./auth/weatherAPI_KEY.json");
const {spreadsheetId, key} = require("./auth/googleSheet_KEY.json");
const {client_id, client_secret, grant_type, redirect_uri, auth_uri, token_uri, scope} = require("./auth/credentials.json")

const task_states = [];
const port = 3000;
const server = http.createServer();

server.on("listening", listening_handler);
server.on("request", connection_handler);
server.listen(port);

function listening_handler(){
    console.log(`Now Listening on Port ${port}`);
}

function connection_handler(req, res){
    if(req.url === "/"){
        const main = fs.createReadStream("html/index.html");
            res.writeHead(200, {"Content-Type": "text/html"});
            main.pipe(res);
    }
    else if(req.url.startsWith("/search")){
        const user_input = new URL(req.url, `https://${req.headers.host}`).searchParams;
        const city = user_input.get('city');
        if(city==null || city==""){
            res.writeHead(404, {"Content-Type": "text/html"});
            res.end("<h1>Input is Needed!</h1>");  
        }
        else{
            get_city_weather(city, res);
        }
    }
    else if(req.url.startsWith("/?state")){
        const {state, code} = url.parse(req.url, true).query;
        let task_state = task_states.find(task_state => task_state.state === state);
        if(code === undefined || state === undefined || task_state === undefined){
            not_found(res);
            return;
        }
        const result = task_state;
        //res.end("123");
        send_access_token_request(code, result, res);   
    }
    else{
        not_found(res);
    }
}

function get_city_weather(city, res){
    let appid =  querystring.stringify(weatherAPI_KEY);
    const weather_get = https.get(`https://api.openweathermap.org/data/2.5/weather?q=${city}&${appid}&units=imperial`);
    weather_get.once("response", process_stream);
	function process_stream (weather_stream){
		let weather_data = "";
		weather_stream.on("data", chunk => weather_data += chunk);
		weather_stream.on("end", () => weather_info(weather_data, res));
	}
}

function weather_info(weather_data, res){
    let weather_obj = JSON.parse(weather_data);
    if(weather_obj.cod==404){
        not_found(res);
    }
    else{
        console.log(`${weather_obj.cod}, request to append data.`);
        let cityName = weather_obj.name;
        let cityTemp = weather_obj.main.temp;
        let cityWeather = weather_obj.weather[0].main;
        const requestData = [cityName,cityTemp,cityWeather];
        const state = crypto.randomBytes(20).toString("hex");
        task_states.push({requestData, state});
        get_googleSheet_auth_cod(state, res);
    }
}

function get_googleSheet_auth_cod(state, res){
    const authorization_endpoint = "https://accounts.google.com/o/oauth2/auth?response_type=code&access_type=offline&approval_prompt=auto";
    let uri = querystring.stringify({client_id, scope, redirect_uri, state});
    res.writeHead(302, {Location: `${authorization_endpoint}&${uri}`}).end();
}

function send_access_token_request(code, result, res){
    const token_endpoint = token_uri;
    const post_data = querystring.stringify({client_id, client_secret, grant_type, redirect_uri, code})
    let options = {
        method: "POST",
        headers:{
            "Content-Type":"application/x-www-form-urlencoded",
        }
    }
    https.request(
        token_endpoint,
        options,
        (token_stream) => process_stream(token_stream, receive_access_token, result, res)
    ).end(post_data);
}

function receive_access_token(body, result, res){
    const {access_token} = JSON.parse(body);
    send_result_request(result, access_token, res);
}

function send_result_request(result, access_token, res){
    const googleSheet_endpoint = "https://sheets.googleapis.com/v4/spreadsheets";
    const post_data = JSON.stringify({"majorDimension":"ROWS", "values":[result.requestData]});
    const options = {
        method: "POST",
        headers:{
            "Content-Type": "application/json",
			Authorization: `Bearer ${access_token}`
        }
    }
    https.request(`${googleSheet_endpoint}/${spreadsheetId}/values/A1:append?valueInputOption=USER_ENTERED&key=${key}`,
    options,
    (task_stream) => process_stream(task_stream, receive_task_response, res)
    ).end(post_data);
}

function receive_task_response(body, res){
	const results = JSON.parse(body);
	console.log(results);
	res.writeHead(302, {Location: `https://docs.google.com/spreadsheets/d/${results.spreadsheetId}/edit?usp=sharing`})
	   .end();
}

function process_stream (stream, callback , ...args){
	let body = "";
	stream.on("data", chunk => body += chunk);
	stream.on("end", () => callback(body, ...args));
}

function not_found(res){
    res.writeHead(404, {"Content-Type": "text/html"});
	res.end(`<h1>404 Not Found</h1>`);
}