Ushare
======

### Umbrella sharing App based on Wi-Fi positioning
- - - 

User Story
--------------

* One day, Bob was having a class in Science Building A169, but he suddenly found that it was raining outside and he forgot to bring an umbrella. Bob looked around and had no idea who to ask for sharing an umbrella to go to the dormitory. That’s why we plan to design an app to accommodate this situation.

* With our app, people can instantly find companies on their way who are willing to share the umbrella.

- - - 

Why choose us?
--------------

1. Accuracy & Power-saving Ushare uses campus’s wireless network for location service. Our campus has wide wireless network coverage and staff’s devices usually access the nearest AP with strongest signal among numerous hotspots. Having the same access point means they are close enough to each other. Compared with GPS, our design simply keeps track of the BSSID of the AP, users are connected to, without any extra energy loss and it ensures even higher location accuracy.

2. Efficient Ushare can significantly reduce the time for finding the people willing to share umbrella. It provides users (peers) real-time communication. The whole appointment may takes less than 5 minutes (depends on both sides’ response time).

3. Convenient Ushare can accommodate some awkward social situation. Some people may be born shy and afraid of being rejected. They can use this app with ease, because the people they found are all willing to share umbrella.
To sum up, the app we are designing will provide people a chance to help each other. We believe we are capable to make this come true.

- - -

Ushare Installation Guide
--------------

### Requirement
 * Android Phone (3.1.2 and above, 1280x720 or higher resolution is recommended)	'''Required'''
 * Access to ushare.iyuyue.net (with in the XJTLU campus)	 '''Required'''
 * Android Development Tools (ADT) or Eclipse	'''Optional'''

### Build & Installation

#### For Client:

 * Method 1(Recommended): Download and install the latest version of Ushare from http://ushare.iyuyue.net (in your Phone)
 
 * Method 2: Copy the apk file (code/client/ushare.apk in the package) to your phone and install 
 
 * Method 3: Import the project archive to ADT Eclipse (code/client/ Ushare_*.zip in the package) and build the project to your phone

#### For Server:
 * The server API has been set up for you at ushare.iyuyue.net
 * You can access the Dashboard through https://ushare.iyuyue.net/web/dashboard/
 * Also, phpMyAdmin is available at http://10.7.126.32/phpmyadmin/ (ushare:ushare2014)

- - -

### Screenshot

![Alt text](http://doc/Dashboard_screenshot1.png "Dashboard Screenshot 1")
- - -
### * Known Bugs of Ushare


* [MIUI JLB24.0 or older] The router's BSSID may show up as 00:00:00:00:00:00 after resuming from hibernation.
This is an System API bug introduced by MIUI JLB24.0 (the official custom OS of Xiaomi Phone) or older version. It has been fixed in the later version.

- - -

#### _Web Frontend and API Service: Yue Yu_
#### _Android Client: Yue Yu, Weiping Huang_
#### _UI Design: Weiping Huang, Yue Yu, Yangzi Zhao, Ruiqiong Tian_

### _We worked together!_