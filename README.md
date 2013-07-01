### JDBC SSL Connection to PostgreSQL
This example demonstrates how to properly create an SSL connection to a PostgreSQL server. 

The majority of examples on the internet of using SSL with PostgreSQL instruct the JDBC driver to not validate the server's certificate. This is vulnerable to a [man in the middle][MITM] attack. This is particularly important with the rise of cloud based database-as-a-service platforms where client applications are connecting to the remote servers through the public internet.

The example code includes a [TrustManager] and [SSLSocketFactory] that can validate against a pre-shared certificate. It provides protection against MITM. Additionaly, through certificate pinning a self-signed certificate can be even more secure than one signed by a trusted [certificate authority][CA] as it will also be immune to CA compromises.

### Vagrant
The tests are configured to run against the local machine. For convience a Vagrantfile is included to automatically provision a PostgreSQL server with a test user and database. To create it run:

    $ vagrant up

After creating the VM the install script will:

1. Run apt-get update/upgrade to update the system packages
2. Install PostgreSQL
3. Update the PostgreSQL configuration files to allow inbound connections
4. Create a test user and database

### Server SSL Certificate
After installation run the following from the root directory of the project to extract the randomly generated server SSL certificate:

    $ vagrant ssh -c 'sudo cat /var/lib/postgresql/9.1/main/server.crt' > src/test/resources/server.crt

### Building
To build use maven:

    mvn clean compile

### Running Tests
To run the tests use maven:

    mvn test

[MITM]: http://en.wikipedia.org/wiki/Man-in-the-middle_attack
[TrustManager]: http://docs.oracle.com/javase/6/docs/api/javax/net/ssl/TrustManager.html
[SSLSocketFactory]: http://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html]
[CA]: http://en.wikipedia.org/wiki/Certificate_authority
