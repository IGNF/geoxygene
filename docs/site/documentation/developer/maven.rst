
Maven Developer
#######################

Project settings in the pom file
**************************************

Open the pom.xml file and configure :


* Repositories to download jars :

  .. literalinclude:: /download/resources/repository.xml
          :language: xml

* the correct dependencies like this :

  .. literalinclude:: /download/resources/dependencies.xml
          :language: xml
        

Certificate
******************

If you don't use Eclipse editor for building your project, you have to configure Maven to use the certificates for downloading jar files on sites behind an HTTPS server.
The reference of this approach is explained here : http://maven.apache.org/guides/mini/guide-repository-ssl.html.

1. Get the certificate 

**First method** : If you have openssl installed

   .. container:: chemin
   
      echo -n | openssl s_client -connect dionysos2.ign.fr:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > dionysos2.cert 


**Second method** : Download the certificate from your browser like this : 

.. container:: twocol

   .. container:: leftside


      1.1 Open your browser and go to :

          .. container:: svnurl
    
             https://cogit-services.ign.fr/nexus-webapp-1.4.1/index.html

      1.2 In the navigation bar, click on the padlock

      1.3 Click on "More informations"

      1.4 Click on "Display certificate"

      1.5 Click on "détails"

      1.6 Click on "Export"

      1.7 Save the certicate in your disk. 
          For example : **E:\\certificat\\dionysos2.crt**

   .. container:: rightside
   
      .. container:: centerside
     
             .. figure:: /documentation/resources/img/maven/CertificatJava.png
                :width: 400px
       
                Figure 1 : Download certificate


2. The following command line imports the certififcate authority's certificate into a JKS formatted key store named trust.jks, the trust store.

   .. container:: chemin
 
         keytool -v -alias mavensrv -import -file E:\\certificat\\dionysos2.crt -keystore trust.jks


3. Define a new environment variable

   .. container:: chemin

        MAVEN_OPTS -Xmx512m 
                   -Djavax.net.ssl.trustStore=E:\certificat\trust.jks 
                   -Djavax.net.ssl.trustStorePassword= 
                   -Djavax.net.ssl.keyStore=E:\certificat 
                   -Djavax.net.ssl.keyStoreType=pkcs12 
                   -Djavax.net.ssl.keyStorePassword=

4. Finish


