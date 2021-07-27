# Getting Started

## Environment

### 1. Start Jenkins

Outside of the VSCode dev container, start Jenkins:

```sh
make start-jenkins
```

Follow the instructions to setup Jenkins using the admin password printed to the console. You can also display the password by running:

```sh
make show-password
```

Note that all jenkins related commands need to happen outside of the dev container.

### 2. Open Project in Dev Container

Using VSCode, open the workspace in the dev container. It will build the docker image the first time you open in the container.

In the container, run:

```sh
make build
```

### Clean up / Tear down Jenkins

```sh
make stop-jenkins
```
