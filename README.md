# DemoAPI
Three GET calls, with only 2 respond concurrently

http://localhost:8080/actuator

{
"_links": {
"self": {
"href": "http://localhost:8080/actuator",
"templated": false
},
"health-path": {
"href": "http://localhost:8080/actuator/health/{*path}",
"templated": true
},
"health": {
"href": "http://localhost:8080/actuator/health",
"templated": false
}
}
}
