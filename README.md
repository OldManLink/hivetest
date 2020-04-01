[![MIT License][license-badge]][LICENSE]

# Hive Streaming Client Tester

> Home assignment created by [Peter Hugosson-Miller][oldmanlink-profile] as part of the recruitment process for Hive Streaming. Make a service to monitor, display and report the CPU usages of remote clients 
> 
> Implemented in Play/Scala on the backend and ReactJS/CanvasJS on the frontend. Based on the excellent [Scala Play React Seed](http://bit.ly/2A1AzEq) .
 
![TwentyClients](https://github.com/OldManLink/hivetest/blob/master/docs/11_TwentyClients.png)

## How to build it?

### Prerequisites

* [Java 8](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
* [Node.js](https://nodejs.org/)
* [Yarn](https://classic.yarnpkg.com/en/docs/install/)
* [sbt](https://www.scala-sbt.org/download.html)
* [scala](https://www.scala-lang.org/download/)
* [MySQL 5.6](https://dev.mysql.com/downloads/mysql/5.6.html)

### Let's get started,

* Fork or clone this repository.

* Use any of the following [SBT](http://www.scala-sbt.org/) commands which will intern trigger frontend associated npm scripts.

```
    sbt clean           # Clean existing build artifacts

    sbt stage           # Build your application from your project’s source directory

    sbt run             # Run both backend and frontend builds in watch mode

    sbt dist            # Build both backend and frontend sources into a single distribution artifact

    sbt test            # Run both backend and frontend unit tests
```

* This seed is not using [scala play views](https://www.playframework.com/documentation/2.6.x/ScalaTemplates). All the views and frontend associated routes are served via [React](https://reactjs.org/) code base under `ui` directory.

## Complete Directory Layout

```
├── /app/                                 # The backend (scala play) sources (controllers, models, services)
│     └── /controllers/                   # Backend controllers
│           └── FrontendController.scala  # Asset controller wrapper serving frontend assets and artifacts
├── /conf/                                # Configurations files and other non-compiled resources (on classpath)
│     ├── application.conf                # Play application configuratiion file.
│     ├── logback.xml                     # Logging configuration
│     └── routes                          # Routes definition file
├── /docs/                                # Documents and screenshots
├── /logs/                                # Log directory
│     └── application.log                 # Application log file
├── /project/                             # Contains project build configuration and plugins
│     ├── FrontendCommands.scala          # Frontend build command mapping configuration
│     ├── FrontendRunHook.scala           # Forntend build PlayRunHook (trigger frontend serve on sbt run)
│     ├── build.properties                # Marker for sbt project
│     └── plugins.sbt                     # SBT plugins declaration
├── /public/                              # Frontend build artifacts will be copied to this directory
├── /target/                              # Play project build artifact directory
│     ├── /universal/                     # Application packaging
│     └── /web/                           # Compiled web assets
├── /test/                                # Contains unit tests of backend sources
├── /ui/                                  # React frontend source (based on Create React App)
│     ├── /public/                        # Contains the index.html file
│     ├── /node_modules/                  # 3rd-party frontend libraries and utilities
│     ├── /src/                           # The frontend source codebase of the application
│     ├── .editorconfig                   # Define and maintain consistent coding styles between different editors and IDEs
│     ├── .gitignore                      # Contains ui files to be ignored when pushing to git
│     ├── package.json                    # NPM configuration of frontend source
│     ├── README.md                       # Contains all user guide details for the ui
│     └── yarn.lock                       # Yarn lock file
├── .gitignore                            # Contains files to be ignored when pushing to git
├── build.sbt                             # Play application SBT configuration
├── LICENSE                               # License Agreement file
├── README.md                             # Application user guide
└── ui-build.sbt                          # SBT command hooks associated with frontend npm scripts 
```

**Note: _On production build all the front end React build artifacts will be copied to the `public` folder._**

## How to use it?

### Prepare the database

Open a MySQL terminal window as root, and run the `conf/sql/db_setup.sql` script, copied here for ease of use:
```
CREATE DATABASE IF NOT EXISTS `hivetest` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `hivetest`;

GRANT ALTER, CREATE, DELETE, DROP, INDEX, INSERT, SELECT, UPDATE ON hivetest.* TO `hivedbuser`@`localhost` IDENTIFIED BY 'streeemz';

GRANT ALTER, CREATE, DELETE, DROP, INDEX, INSERT, SELECT, UPDATE ON hivetest.* TO `hivedbuser`@`%`IDENTIFIED BY 'streeemz';

```

### Start the server and frontend
Open a console window and enter this command:
```
sbt run
```
This will open your browser on the address `http://localhost:3000/` with the following view:

![Opened Tester](https://github.com/OldManLink/hivetest/blob/master/docs/00_OpenedTester.png)

Click on the <img src="https://github.com/OldManLink/hivetest/blob/master/docs/01_ReactButton.png" width="117" height="91" /> to add a running client to the frontend.

You can add as many as you like, but please see the following information first. As you can see, the client has a sensible starting value of 20%:
![OneClient](https://github.com/OldManLink/hivetest/blob/master/docs/02_OneClient.png)

You can change this by moving your mouse over the client and changing the percentage thus:
![PercentChanger](https://github.com/OldManLink/hivetest/blob/master/docs/03_PercentChanger.png)
![MakeSlower](https://github.com/OldManLink/hivetest/blob/master/docs/04_MakeSlower.png)
![Slowed](https://github.com/OldManLink/hivetest/blob/master/docs/05_Slowed.png)

The client changes colour as it gets slower, but as long as it is able to send its report to the backend you can see its timestamp changing. Each new timestamp comes from the server after a successful report.

Sometimes a client can't reach the backend, even though it is still collecting cpu information. To simulate this, click on the `block` checkbox as seen below:
![Block](https://github.com/OldManLink/hivetest/blob/master/docs/06_Block.png)
![Blocked](https://github.com/OldManLink/hivetest/blob/master/docs/07_Blocked.png)

You can see the client gains a double border to show it is blocked, and queued reports start to appear in the client as a line of dots, one per report. When you unblock the client, all the queued reports are sent at once. You will see how the graph might suddenly hop up or down as it gets a lot of new information at the same time.
![Unblock](https://github.com/OldManLink/hivetest/blob/master/docs/08_Unblock.png)

Sometimes a client might get so busy that it is unable to send reports to the backend, even though the backend is available. Try setting the cpu percentage to 100, and you will see that the timestamp remains unchanged, indicating that no reports are being sent. Compare the timestamps on client #217 with the others:
![StoppedClient100](https://github.com/OldManLink/hivetest/blob/master/docs/09_StoppedClient100.png)

Then when the client is able to send again, all the missing reports are automatically filled in by the server as 100%, so if you drop the cpu percentage again you will see how the graph jumps up once more:
![RestartClient97](https://github.com/OldManLink/hivetest/blob/master/docs/10_RestartClient97.png)

The system is quite robust, since it is written in Scala. With twenty clients runing there is no noticeable slowdown. I will determine the maximum number of clients for my setup some other time, but for now, here's what 20 clients look like on a 2019 MacBook Pro, running OS X Catalina, and displayed on the Safari browser:
![TwentyClients](https://github.com/OldManLink/hivetest/blob/master/docs/11_TwentyClients.png)

### Stop the server and frontend
To stop all the clients smoothly, simply reload the page on your browser. React will smoothly unmount and terminate all the running clients. Once this is done, press `<Ctrl> C` in the console to terminate the backend and frontend services.

## Further discussion points
I'm tempted to put all my thoughts here in the documentation, but then I would never get this sent off, so I'll have to stop here. If you want to know more, please call me back for a second interview!


## License

This software is licensed under the MIT license

[license-badge]: http://img.shields.io/badge/license-MIT-blue.svg?style=flat
[license]: https://github.com/yohangz/java-play-react-seed/blob/master/README.md

[oldmanlink-profile]: https://github.com/oldmanlink
