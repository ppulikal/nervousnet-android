### Welcome to Nervousnet GitHub Page.
The planetary nervous system is a large-scale distributed research platform that provides real-time social sensing services as a public good. Existing Big Data systems threaten social cohesion as they are designed to be closed, proprietary, privacy-intrusive and discriminatory. In contrast, the Planetary Nervous System is an open, privacy-preserving and participatory platform designed to be collectively built by citizens and for citizens.

The planetary nervous system is enabled by Internet of Things technologies and aims at seamlessly interconnecting a large number of different pervasive devices, e.g. mobile phones, smart sensors, etc. For this purpose, several universal state-of-the-art protocols and communication means are introduced. A novel social sensing paradigm shift is engineered: Users are provided with freedom and incentives to share, collect and, at the same time, protect data of their digital environment in real-time. In this way, social sensing turns into a knowledge extraction service of public good.

The social sensing services of the planetary nervous system can be publicly used for building novel innovative applications. Whether you would like to detect an earthquake, perform a secure evacuation or discover the hot spots of a visited city, the Planetary Nervous system makes this possible by collectively sensing social activity of participatory citizens.


### Architecture<br>
The Android nervousnet mobile application is based on the concept of Bound Services and Android Interface Definition
Language (AIDL).<br>
<i>“A Service is an application component that can perform long-running operations in the background and does not provide a user
interface. Another application component can start a service and it will continue to run in the background even if the user switches to
another application. Additionally, a component can bind to a service to interact with it and even perform interprocess communication
(IPC).” </i><br>
https://developer.android.com/guide/components/services.html<br><br>
<i>“A bound service offers a client-server interface that allows components to interact with the service, send requests, get results, and even do so across processes with inter-process communication (IPC).”</i><br>
https://developer.android.com/guide/components/bound-services.html<br><br>
<i>“ AIDL allows you to define the programming interface that both the client and service agree upon in order to communicate with each
other using interprocess communication (IPC). On Android, one process cannot normally access the memory of another process. So to
talk, they need to decompose their objects into primitives that the operating system can understand, and marshall the objects across that
boundary for you. The code to do that marshalling is tedious to write, so Android handles it for you with AIDL.”</i><br>
https://developer.android.com/guide/components/aidl.html<br><br>

![alt tag](https://github.com/nervousnet/nervousnet-android/blob/master/Resources/Images/Others/ppt_screens/Slide3.jpg)

####Terminology
#####Mobile App - Native Mobile Application built for Android and iOS platforms. <br>
<ul>
<li>Allows users to view and share various Sensor related Data</li><br>
<li>Required to be installed for running external apps (Axons) built using nervousnet PlatformAPI’s.</li><br>
<li>Acts like a connectivity hub for external products like smartwatches, beacons and external sensors that want to share sensor data with the nervousnet platform.</li><br>
<li>Android version uses background Services to enable third party apps and extensions to connect and share data with the Nervousnet platform.</li><br>
<li>iOS version uses WebViews and allows for external Axons to run inside a WebView container.</li><br>
</ul>
#####Axons (Native)- Native Android apps, Smart devices, beacons that can connect to the nervousnet HUB mobile app.<br>
<ul>
<li>Uses the nervousnet Platform API's to receive and share sensor data.</li><br>
<li>Works only in Android devices. </li><br>
<li>Uses the Android background services feature.</li><br>
<li>Possibility of using Bluetooth, Wi-Fi Direct to do connect to the nervousnet mobile app.</li><br>
</ul>
#####Axons - HTML, JavaScript and CSS applications that run inside WebView containers inside the nervousnet apps. <br>
<ul>
<li>Currently supported on the iOS platform.</li><br>
<li>Android Platform support in the next phase.</li><br>
</ul>
#####nervousnet CORE – Distributed and Decentralized set of Servers <br>
<ul>
<li>Used to store and collect Data shared by Clients (Mobile & Web), IOT Hardware sensors and devices, partner platforms and more.</li><br>
<li>Individual Servers are called nervousnet Nodes.</li><br>
<li>Mobile Clients will have the option of selecting a server from a list.</li><br>
</ul>

![alt tag](https://github.com/nervousnet/nervousnet-android/blob/master/Resources/Images/Others/ppt_screens/Slide5.jpg)
### Support or Contact
For more information or support check out our website http://www.nervousnet.info
