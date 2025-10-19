# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfKR3y-P8gKoQ2oGCkB-rgeWKlaSC6k7AWtGYHp8K+s6XYwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3r5d5LsKMBihKboynKZbvEqmAqsGGpuka2QwGgEDMCiOLoiiDHDg6SY2Z23a9v2XkgdU-pugActlP64gVUYxnGhQ6SV8DIKmMDpgAjAROaqHm8zQUWJb1D40yXtASAAF4oLsdFNkVi7iVQSIbu6W7JeqMBNPofw7DAGXiiA0AouAMAAGbQPFnLbt5G3+odWw7AAkmgF3Fig4CtSgsYKeh8LJj12F9U4g2jGMw2jeZE3QFNM16nNi3LY2DFVT5vIjvUh5yCgz7xOel7XmtArhfUj4BmTW7Yx+jxwvULnihkqgAZZTMYV1oEvIRhnzCRxnkZeVH1sLFmdWDJRgDheFKURQuqbBYuIRLqkrQxnjeH4-heCg6AxHEiQG0bLm+FgomCnzjTSBG-ERu0EbdD0cmqApwwUeLHU1dZDywv63vq4UVk27Z9QOfYluk-BIeebZgoU-5YDE7HV7aHOoXFZUy6ReKT50-IsrysHSFJaqGpfVQJpIOuZfoCFuPFRtW09n2T3VRJpbV7XHDEz7ANA-G0uVKJaZQ0N-Lw+NxZIwqKPxGjS0NvRTcLpTrcurTL704nOMb-UHAoNwx6Xun5O3jnQr1BkMwQDQO8Z-InddVZLNCZb7Oc2HvN+y8VxR7dVlvLfCowtaMV1gEAqMRsDig1PxNEMAADiSoNDWxeqWBoyCnau3sEqL2aty6dUqO-Y0RDG6-1KptbeyAcioJzOnH2CdOxJyvvSGAjJU7nwopfbOi5c4RSioXXexc4oN0KHtKuaAa7IHrhQwoFMu40Lsu3Sq+9MH1F7nIgeIch7tRBphcGctIbQ2zNPfMs9JoLwosvDGa8lF-zKk-V8+8qTsNHPZNEDC1AYizs3ARN8UFMkrAgThaDX6M0DqWRB9C0HfwQIBbmoNqH6WWPgnMBYGjjAySgD60gCz9XCMEQIIJNjxF1CgNKkFIQrGGGMZIoA1TVJUjBdJSo6pKkhBcTogC-ZGJATAXCYCRjtMyfUbJYxcn5MKcU0pyxymVJaWNGC9TGkgGaUrL4IJcmdLmN03pmNIHMX8BwAA7G4JwKAnAxAjMEOAXEABs8AJyGB8TAIoxjw7dwma0DoeCCGLx9gRXZNSpb9KiV+eoEiQUdJqVpaEyTvkqPxq8nxTCQ5rFBXMFhKi2H8M8VwtOvDM7r33PeIRBcXHaBLuQuO5cpEwG0XXOllEQ5krClvVRFVImpJ7jIvuuikL6OBkA8epip65ksYWOepZpq2KgAtFeEDHFcupkXYAr93EEtRUeFA6Lcl8ICZTQR9Q4CvPCXMW63gZgoKVK41hb9klmrRUqBJSTonKP0lMpUMz6hFJKX07uAzerDNhXMP1MAA2BAgTrE5lgT4OU2MbJACQwAJr7BAZNAApCA4o7VWv8OstUnzZbIskk0ZkMkei5MIfS9ABFsAIGAAmqAcAIAOSgOZMiPqI0FP9XMoNiZSHOtZcCmGTaW2UHbZ27t3xpn9qjYOrmnq1UwAAFZ5rQOiiRKxJ2tpndAFYC7cWDm1calOxKNX+I3hS+owjqViNLgoiuKUmUCrkWO9lqrqFtx5QzPlWiP11yFZGaMgMDFiuMRPMxsMLFjRldY+Vs1FXo1XqtDxyitrEwdXig+flOFMgNb66QN7yVU2CcwXJBpxSOAXa+-azL1z0Z-R2FFmV-0aMA++2RdcfH5JFSPCFMteoAFYFYwzhtKsYiNSxZUtYYHUeomXSBgFaG06GsZuJkB4qa2AtDomI32vdzaD0dqPW8I1t6KPMn04TGAeo3n2ppXIBz8RDD0YAwHKFMBc1s3-IkldX4vVgSHTzETEMw3gKOXGvWXgW0prTfF+UiBgywGANgJthA8gFA+Rg7jDR7aO2dq7YwhjIXMxgCMRFq7f3bxANwPAfitU6Z1TABraXmuONNd1NLbnDAGd0PoMQXmyGpbwO6oLwEnE1AAeViLJiovVaOUAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
