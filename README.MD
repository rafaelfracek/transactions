# Transactions

## How to run?

**Requirements:**

* docker

**On Linux:**

```
./build-and-run-on-docker.sh
```

**On Windows:**

```
.\build-and-run-on-docker
```

## Which files will be loaded to memory during startup?

**CSV file with transactions:**

```
./docker-data/transactions/transactions.csv
```

**CSV file with users:**

```
./docker-data/transactions/users.csv
```

**\* You can modify these files before startup.**

## Which users should I use for basic authentication?

There are two default users in users.csv file:
* user: user1, password: user1
* user: user2, password: user2

**\* You can add some users with an encoded bcrypt password before startup.**

## What is the endpoint url?

```
http://localhost:8081/accountSummaries
```

You can add **customerIds** request parameter, for example:

```
http://localhost:8081/accountSummaries?customerIds=2,3
http://localhost:8081/accountSummaries?customerIds=ALL
```