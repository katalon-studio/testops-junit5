### Configuration

Configurations will be read from environment variables, Java system properties, and properties file in this order.

* `testops.server-url` (environment variable: `TESTOPS_SERVER_URL)

Katalon TestOps endpoint (default: `https://testops.katalon.io`).

* `testops.api-key` (environment variable: `TESTOPS_API_KEY`)

Your Katalon TestOps API Key.

* `testops.project-id` (environment variable: `TESTOPS_PROJECT_ID`)

The Katalon TestOps project that will receive the test results.

#### Via environment variables

* Linux

```
export TESTOPS_SERVER_URL=https://testops.katalon.io
export TESTOPS_API_KEY=<your API key>
export TESTOPS_PROJECT_ID=<your project ID>
mvn test
```

* Windows

```
set TESTOPS_SERVER_URL=https://testops.katalon.io
set TESTOPS_API_KEY=<your API key>
set TESTOPS_PROJECT_ID=<your project ID>
mvn test
```

#### Via Java system properties

```
mvn test -Dtestops.server-url=https://testops.katalon.io -Dtestops.api-key=<your API key> -Dtestops.project-id=<your project ID>
```

#### Via properties file

Create a `testops.properties` file in the `resources` directory

```
testops.server-url=htts://testops.katalon.io
testops.api-key=
testops.project-id=
```

Run the command:

```
mvn test
```

## Samples

https://github.com/katalon-studio-samples/testops-junit5-sample