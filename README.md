#A Ratpack framework test

##Description

A simplest backend application that provides some REST endpoints - just to take a look at Ratpack framework and test its abilities.

##REST API description

4 REST endpoints are implemented. They communicate over http using JSON with the following functionality:

/login - a POST request that accepts no input and returns a token (which need to be used in subsequent calls to the API, in the Authorization header). Every call to /login returns a new token and every invocation to this endpoint creates a new user, gives it a preset balance in a preset currency.

/balance - a GET request that accepts an Authorization header (with the token value output from /login) and returns the current balance along with the currency code.

/transactions - a GET request that accepts an Authorization header (with the token value output from /login) and returns a list of transactions performed by the user. Each transaction contains date, description, amount and currency.

/spend - a POST request that accepts an Authorization header (with the token value output from /login), JSON content representing one spend transaction with the transaction date, description, amount, currency.

##Technology stack

Ratpack framework is used for web server application. Redis is used as the datastore.

Spring DI or spring components are not used, Guice used for DI instead.

Basic Docker capability is added just for conveniency.

TODO: add Kubernetes abilities

#Results

##Build

###Local

```
gradle installShadowDist
```

###Docker

```
docker build -t ratpack_app-server -f ./Dockerfile .

```

##Run

###Local

```
./build/install/ratpack_app-shadow/bin/ratpack_app
```


###Docker

```
docker run --name ratpack_app-server -e $JAVA_OPTIONS="Xmx250m" -e PORT=8080 -p 8080:5050 -m 500M ratpack_app-server
```


##Test

###Prerequisites

```
REDIS_VOLUME=/home/$USER/javadev/data/ratpack_app docker run --name ratpack_app-redis -v $REDIS_VOLUME/data:/data -v -v $REDIS_VOLUME/conf/redis.conf:/usr/local/etc/redis/redis.conf -d redis redis-server --appendonly yes
```

###Run autotests

####Trouble

doesn't work in Java9+, see https://github.com/ratpack/ratpack/issues/1410

```
gradle test

./gradlew test
```

###Manual

####Prerequisites

```
 gradle installShadowDist

./build/install/ratpack_app-shadow/bin/ratpack_app
```

or

```
docker build -t ratpack_app-server -f ./Dockerfile .

docker run --rm --name ratpack_app-server --hostname ratpack_app-server -e JAVA_OPTIONS="Xmx250m" -e PORT=8080 -p 8080:5050 -m 500M ratpack_app-server
```

####Testing flow

```
export HOSTNAME=localhost
export PORT=5050

export HOSTNAME=172.17.0.4
export PORT=8080

curl -iv http://$HOSTNAME:$PORT/login

```

{"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW5lcmFsIHB1cnBvc2UiLCJpc3MiOiJNdXNoQmV0dGVyIEludGVydmlld2VyIiwiaWF0IjoxNTk4ODA4NzM1fQ.kLtIrpnh9-AB7I2a_gvfp8I5RxtAyW3AKUMUhERMYTk"}

```
export TOKEN="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW5lHB1cnBvc2UiLCJpc3MiOiJNdXNoQmV0dGVyIEludGVydmlld2VyIiwiaWF0IjoxNTk4ODA4NzM1fQ.kLtIrpnh9-AB7I2a_gvfp8I5RxtAyW3AKUMUhERMYTk"


curl -iv http://$HOSTNAME:$PORT/balance -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json"

curl -iv http://$HOSTNAME:$PORT/transactions -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json"

curl -iv -X POST http://$HOSTNAME:$PORT/spend -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json" -d '{"date":"2020-08-30T19:29:00.0000","description":"New Transaction 2020-08-30 19:29:00","amount":1.25,"currency":"USD"}'

curl -iv -X POST http://$HOSTNAME:$PORT/spend -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json" -d '{"date":"2020-08-30T19:37:00.0000","description":"New Transaction 2020-08-30 19:37:00","amount":6.15,"currency":"USD"}'

curl -iv -X POST http://$HOSTNAME:$PORT/spend -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json" -d '{"date":"2020-08-30T19:38:00.0000","description":"New Transaction 2020-08-30 19:38:00","amount":1.25,"currency":"USD"}'

curl -iv http://$HOSTNAME:$PORT/balance -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json"

curl -iv http://$HOSTNAME:$PORT/transactions -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json"

curl -iv -X POST http://$HOSTNAME:$PORT/spend -H "Authorization: Bearer $TOKEN" -H "Content-type: application/json" -d '{"date":"2020-08-30T19:41:00.0000","description":"New Transaction 2020-08-30 19:41:00","amount":6.15,"currency":"USD"}'

```
