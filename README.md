Dropwizard metrics captured using the Observer pattern in Java 8
================================================================

Proof of concept for capturing application metrics using the Dropwizard metrics library.
The Observer pattern is used to not pollute business domain code with Dropwizard metrics dependencies.

This project also uses spring and spring-boot but that's only to wire some components together and create
a scheduled task to make the project more fun to play with.

### running the project

The easiest way to run the application is to use the maven spring boot plugin:

> mvn spring-boot:run

This application will start a scheduled task to randomly add or remove animals in the zoo. (each x seconds) And each y
seconds, the dropwizard console reporter will print the state of the metrics to the console.

### package structure
The package `net.eekie.metrics.zoo` contains the actual business code with metric capturing using observer pattern.
This could be your business logic of your application. In this package we only enable sending metrics which are going
to be handled by one or more listeners. (it's only about metrics and not dropwizard metric related yet)

The package `net.eekie.metrics.config` contain the config to instantiate an observable zoo with Listerner for
capturing Dropwizard metrics. It's here that the Dropwizard magic happens.

A Dropwizard console reporter is also configured to see the state of the metrics each y seconds. In a production
environment you want to setup a Graphite reporter instead or something similar. (also see docker)


### Note about multi threading
Important remark about using the observer pattern. Make sure whatever logic is executed by the listeners is not
blocking or doesn't take too long to complete. You don't want the Observed class to suffer from blocking calls in its
listeners. One way to solve this is to fire the listener logic in a separate thread. You have to decide for your self
if your uses case needs it. Each choice has its pros and cons.

As an example, in this project we execute all Listener logic in a separate thread. This extra thread is only created
once when the application is started. See class `net.eekie.metrics.zoo.ObservableSubject` to how it can be done.


## Docker

If you run the project with docker-compose, it will enable a Graphite reporter in the application. Docker compose will
 also pull an image from the registry with Graphite and Grafana installed. You will be able to play live metrics the app
 is sending in a Grafana dashboard.


### running with docker-componse

prerequisites:

* a working docker environment
* docker-compose command installed
* don't forget to build a jar first, for example `mvn clean package`

> docker-compose build

> docker-compose up


### JMX

JMX is configured in this project through spring. This is completely optional. It is to show
the metrics exposed by dropwizard. There is also a managed bean configured to allow adding animals
through JMX.

Something to note about the configuration, the JMXMP protocol is used instead of the standard RMI. JMXMP
is easier to forward since it communicates over a single tcp port. Unlike rmi which create a random port by default.
It is easier to configure jmxmp when running in a container, we only have to expose 1 port. Though
when you want to show a remote jmx through jmxmp protocol you'll have to add an extra jar to the class
path when using jvisualvm. otherwize it won't work.

#### use this when want to view the (remote) jmx when you started this app containerized (docker-compose up)

> jvisualvm --cp:a jmxremote_optional-repackaged-4.1.1.jar


jmx connection url: `service:jmx:jmxmp://192.168.99.100:9875`