### HTTP IT Module

In order to execute the tests against a standard AEM Quickstart (Author and Publish) you can run the command:

```
mvn verify -Ptest-all ...
```

Additional parameters can be set with `-D`:

- `granite.it.author.url`: [http://localhost:4502]() (i.e base url as you would enter it in browser, so eventually add the context path)
- `granite.it.publish.url`: [http://localhost:4503]()
- `it.test` : select which test to execute (i.e MyClassIT#myTestMethod or MyClassIT for the all tests in that class) 

To test on a CM environment (Skyline) you can use the following command:

```
mvn clean verify -Ptest-all \
 -Dsling.it.instances=2 \
 -Dsling.it.instance.url.1="<URL of the skyline author setup>" \
 -Dsling.it.instance.url.2="<URL of the skyline publish setup>" \
 -Dsling.it.instance.runmode.1=author \
 -Dsling.it.instance.adminUser.1=admin \
 -Dsling.it.instance.adminPassword.1=####### \
 -Dsling.it.instance.runmode.2=publish \
 -Dsling.it.instance.adminUser.2=admin \
 -Dsling.it.instance.adminPassword.2=####### \
 -Dcom.sun.security.enableAIAcaIssuers=true \
 -Dsling.it.configure.default.replication.agents=false
```
