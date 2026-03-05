# *Welcome on the official SEDAP-Express repository!* :+1:

## <span style="color:blue">Remarks</span> 
<span style="color:blue">
This repository has not yet been officially published. The ICD (Interface Description Document) is actually ready, but many software parts and source, which makes it very easy to develop a SEDAP-Express interface, are unfortunately still missing. This repository will be completed until the end of the year.
</span>
 
## Scope

SEDAP-Express is an exceptionally fast path to integrate new applications, sensors, effectors or other similar things especially into the ecosystem of MESE/UNIITY but not only! There are already a bunch of companies and individuals who use that internal for their own projects. That's why it is intentionally kept simple and offers several technical ways of communication. Of course, this results in limitations, but in most cases where quick and easy integration is required, these are negligible. If increased demands arise later on, the “bigger” SEDAP API respective MESE/UNIITY interface can be used if necessary.

SEDAP-Express is licensed under the “Simplified BSD License” (BSD-2-Clause). Therefore, there should be no problems using SEDAP-Express in commercial or non-commercial projects or integrating parts of the SEDAP-Express framework.


## Sub-Projects

Below you will find a brief overview and description of the sub-projects. If you have any questions or comments, please write to us here. And even better: If you find any errors, feel free to open a bug report!
The samples are minimal examples and have always comments at the places where do you have to write or fill in your own code.


### Documentation 
In this project you will find all the necessary documents such as the SEDAP Express ICD and all other referenced standards and documents. Hopefully, tutorial videos and such stuff will be added in the coming weeks  here to make it as easy as possible for everyone to get started.

### SEDAPExpress

Here you can find everything you need to implement SEDAP-Express into your Application and to understand the protocol. In future, we will also provide SDKs in programming languages other than Java. However, these will then be available via separate repositories.

### SEDAPExpressTool
This mockup simulates the real SEDAP-Express connector of the UNIITY framework. That's why the main function of that tool is to be a mock up for the original UNIITY system. You can manually generate messages for test and debugging, analyse existing messages, receive messages form your system and validate them, etc.  Also, it's intended to be a simple kind of Command&Control (C2) system simulator by providing a simple map with MIL-STD-2525C icons. The engine uses the standard NASA WorldWind framework without our own multithread and other modifications. You can also use operational if you want to transfer messages via other channels ("sneakernet :D ). 

### SampleTCPClient
For most cases, this sample client is the right way to get started with SEDAP Express development. TCP has some significant advantages over UDP, so if you are able to use it in your future use case, it is better to start with this one.

### SampleTCPServer
This is nearly the same project as the SampleTCPClient, except that in this case you are in the server role. Technically it shares the main sources with SampleTCPClient.

### SampleUDPClient
Almost the same client as the TCP variant, but for use cases in which only UDP can be used or is required for other reasons.

### SampleMQTTClient
If you have for instance a central MQTT server and want to use that, this is an example of how to implement that. 

### SampleRESTClient
If you want to implement a REST API client for SEDAP Express, you can use this sample client as a basis. It already contains everything you need for a smooth start.

### SampleRESTServer
When using the REST API it's also possible to play the server role. This is an example for how to easy setup a simple SEDAP-Express REST server.

### SampleProtobufClient
The protocol buffer standard is another method for exchanging SEDAP Express messages. You can see how it works in this example client. As always, the required libraries can already be found in the libs folder of the project.

### SampleSerialClient
This is another example client that can be used as a basic structure for your new client if you need to communicate via a serial line.




***An NOW greetings from the North Sea and Happy Coding***


***Volker Voß***
