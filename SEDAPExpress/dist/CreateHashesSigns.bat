set "version=1.4.2"

certutil -hashfile "sedapexpress-%version%.pom" MD5 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.pom.md5
certutil -hashfile "sedapexpress-%version%.pom" SHA1 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.pom.sha1
certutil -hashfile "sedapexpress-%version%.pom" SHA256 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.pom.sha256
certutil -hashfile "sedapexpress-%version%.pom" SHA512 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.pom.sha512
gpg --batch --yes -u "uniity@bundeswehr.org" -ab -o sedapexpress-%version%.pom.asc sedapexpress-%version%.pom

certutil -hashfile "sedapexpress-%version%.jar" MD5 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.jar.md5
certutil -hashfile "sedapexpress-%version%.jar" SHA1 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.jar.sha1
certutil -hashfile "sedapexpress-%version%.jar" SHA256 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.jar.sha256
certutil -hashfile "sedapexpress-%version%.jar" SHA512 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%.jar.sha512
gpg --batch --yes -u "uniity@bundeswehr.org" -ab -o sedapexpress-%version%.jar.asc sedapexpress-%version%.jar

certutil -hashfile "sedapexpress-%version%-sources.jar" MD5 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-sources.jar.md5
certutil -hashfile "sedapexpress-%version%-sources.jar" SHA1 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-sources.jar.sha1
certutil -hashfile "sedapexpress-%version%-sources.jar" SHA256 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-sources.jar.sha256
certutil -hashfile "sedapexpress-%version%-sources.jar" SHA512 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-sources.jar.sha512
gpg --batch --yes -u "uniity@bundeswehr.org" -ab -o sedapexpress-%version%-sources.jar.asc sedapexpress-%version%-sources.jar


certutil -hashfile "sedapexpress-%version%-javadoc.jar" MD5 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-javadoc.jar.md5
certutil -hashfile "sedapexpress-%version%-javadoc.jar" SHA1 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-javadoc.jar.sha1
certutil -hashfile "sedapexpress-%version%-javadoc.jar" SHA256 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-javadoc.jar.sha256
certutil -hashfile "sedapexpress-%version%-javadoc.jar" SHA512 | findstr /v "CertUtil:" | findstr /v "Hash" > sedapexpress-%version%-javadoc.jar.sha512
gpg --batch --yes -u "uniity@bundeswehr.org" -ab -o sedapexpress-%version%-javadoc.jar.asc sedapexpress-%version%-javadoc.jar
