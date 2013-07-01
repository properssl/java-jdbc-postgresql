# Install PostgreSQL:
apt-get -y install postgresql

# Install Patch (used to patch postgresql.conf):
apt-get -y install patch

# PostgreSQL schema setup:
echo "Changing listen address to all addresses"
cd /
patch -p0 < /mnt/bootstrap/postgresql.conf.patch

echo "Adding all/all to pg_hba.conf"
echo "host    all             all             all                     md5" >> /etc/postgresql/9.1/main/pg_hba.conf

service postgresql restart

echo "Setting up PostgreSQL schema"
su - postgres -c 'psql -f /mnt/bootstrap/database-setup.sql'
