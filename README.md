# Vagrant
The tests are configured to run against the local machine. For convience a Vagrantfile is included to automatically provision a PostgreSQL server with a test user and database. To create it run:

    $ vagrant up

After creating the VM the install script will:

1. Run apt-get update/upgrade to update the system packages
2. Install PostgreSQL
3. Update the PostgreSQL configuration files to allow inbound connections
4. Create a test user and database

# Server SSL Certificate
After installation run the following from the root directory of the project to extract the randomly generated server SSL certificate:

    $ vagrant ssh -c 'sudo cat /var/lib/postgresql/9.1/main/server.crt' > src/test/resources/server.crt

# Building
To build use maven:

    mvn clean compile

# Running Tests
To run the tests use maven:

    mvn test
