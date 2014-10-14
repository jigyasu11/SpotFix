SpotFix
========

DEMO: https://github.com/jigyasu11/SpotFix_App_Demo

Contributors: Richeek Arya and Nishant Shobhit

Vision statement:
empower people to take charge and solve problems in their neighborhood
100 hours of community service

Personas:
‘The Ugly Indian’, the conscientious Alice, who wants to report dark spots, participate in spot fixes, monitor spot fixes
The Admin, Bob, who wants to be able to view all activity, prioritize spot fixes, incentivize users.
The Observer, the average Joe, who needs to be inspired to become an Ugly Indian.

App Interactions:

Signup
Present a screen where the user can provide either an email or a mobile, optionally. No names will be collected, because it’s all anonymous. Everyone will be assigned a name such as ‘Ugly Gorilla’, ‘Dirty Rabbit’ (adjective + cute animal name). This name will remain only on the client side. All mobiles and emails collected will only be used for notifications (like upcoming spotfixes etc.). Allow the user to choose from a list of cities, for now, only Bangalore.

Drawer / App Tray
The drawer (http://tiny.cc/ro2smx) which contains a list of menu items
Home
Report
Participate
Monitor

Home
The home screen is a feed which contains the last seven days worth of updates from the TUI server. Think of it like your facebook feed. The feed will hold notifications about upcoming spotfix (along with a one-click registration to participate). 

Report
Clicking Report opens up a map view (google maps) and zooms in to the user’s location. Let Alice pin the exact location of the dark spot and click ‘next’. The next screen is where she uploads pictures of the dark spot. For now, we can allow for a single picture of the dark spot. Clicking ‘next’ here would confirm the report. This flow can also contain a text input for a brief description of the location and the dark spot. Of course, there will always be a ‘Cancel’ button which will take the user back to the home screen (or whatever)

Participate
A list of all spotfixes sorted by nearness to Alice’s current location, and then sorted by the number of participants. One-click button to participate in the spotfix. A reminder will also be setup, which will SMS or send a push notification 1 hour before the spotfix is about to happen. Show spotfixes which Alice has already signup for in this screen itself. Clicking on an upcoming spotfix will take Alice to another map view pin pointing to the exact location (lat/long) of the spotfix scheduled.

Monitor
Same flow as report except that the last step would not take any text input.

Web interactions
(for now the web interface will only be used by Bob, the admin)

Dashboard

Tabbed view displaying all scheduled spotfixes. A scheduled spotfix can be moved to a confirmed, on-hold or rejected state. Any such event will send out a notification to users. Scheduled spotfix view will also contain a count of users who have expressed intent to participate.

View Reports
Map view of all reports that are coming in from users, along with pictures. Each report has it’s own view where the admin can either reject it, approve it or put it on hold. Approving a report will move it to the scheduled state. Other enhancements around viewing today’s reports etc.

Completed spotfixes
An archive of all spotfixes conducted successfully.

Announcer
An interface to send out feed updates, push notifications, SMS-es, emails to users.

Statistics
Graphs mostly. 
User registrations per day
Spotfixes reported per day
Number of users confirming to participate per day
Spotfix load per day (# of users / # of spotfixes)
Heat map of the city (more reports means red, no reports means blue)
