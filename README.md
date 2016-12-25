# ST-LIFX-Group-of-Groups

## Summary
LIFX Group of Groups (LGoG) allows you to create a "virtual" device based on a single LIFX group or a set of LIFX groups.  

Example 1: If you have a group called "Kitchen" and a group called "Dining Room", you can create a group of groups called "First Floor" (or whatever) and add the two LIFX groups to it. You can treat the two groups as a single device. This handles up to 10 groups per device.

Example 2: If you have a single group within the LIFX called "Kitchen" you can add it and control your set of lights as a single device called, you guessed it "Kitchen" (or whatever else).

This device handler also supports scenes, see the preferences section below.

## This device handler supports
1. On / Off
2. Setting Color
3. Setting Color Temperature
4. Setting Brightness
5. Power Reporting
6. Syncing Between Groups [Must have companion app installed](https://github.com/ericvitale/ST-LIFX-Group-of-Groups/blob/master/smartapps/ericvitale/lifx-sync.src/lifx-sync.groovy)
7. 1 to 10 groups as a single device
8. 0 to 5 scenes for the group (unfortunatly if you don't use these you cannot remove them from UI via settings)

## Installation via GitHub Integration
1. Open SmartThings IDE in your web browser and log into your account.
2. Click on the "My Device Types" section in the navigation bar.
3. Click on "Settings".
4. Click "Add New Repository".
5. Enter "ericvitale" as the namespace.
6. Enter "ST-LIFX-Group-of-Groups" as the repository.
7. Hit "Save".
8. Select "Update from Repo" and select "ST-LIFX-Group-of-Groups".
9. Select "lifx-group-of-groups.groovy".
10. Check "Publish" and hit "Execute".
11. See the "Preferences" & "How to get your API Token" sections below on how to configure.

## Manual Installation (if that is your thing)
1. Open SmartThings IDE in your web browser and log into your account.
2. Click on the "My Device Types" section in the navigation bar.
3. On your Device Types page, click on the "+ New Device Type" button on the right.
4 . On the "New Device Type" page, Select the Tab "From Code" , Copy the "lifx-group-of-groups.groovy" source code from GitHub and paste it into the IDE editor window.
5. Click the blue "Create" button at the bottom of the page. An IDE editor window containing device handler template should now open.
6. Click the blue "Save" button above the editor window.
7. Click the "Publish" button next to it and select "For Me". You have now self-published your Device Handler.
8. See the "Preferences" & "How to get your API Token" sections below on how to configure.

## How to get your API Token
Navigate to https://cloud.lifx.com, sign in and then go to the settings section and select generate new token.

## Additional Feature Information
LIFX Group of Groups (LGoG) provides the following capabilities through a device handler and an optional companion application (LIFX Sync):

### Control a Group of LIFX Bulbs as a Single Device
This capability is the primary reason I created LGoG. By default, using the LIFX (Connect) app that comes with SmartThings all of your light bulbs will get added to SmartThings. If you have 1 bulb, this is likely just fine for you, however if you have many bulb, specifically bulbs you would like to control as a single device you are out of luck. Before LGoG you had to either download a SmartApp that would watch your bulbs and update the status of some of your bulbs based on the status of a master bulb or control them through CoRE (which you can still do with LGoG, however you are stuck selecting multiple lightbulbs.  Specifically how LGoG works is...

1. Create the groups of lights in your LIFX app. Yea, I know you can’t have a bulb in multiple groups, but LGoG fixes that! 
2. Generate and API key, instructions below.
3. Install the LGoG device handler.
4. Configure the device, add a single group or multiple groups.

Why is this better? It is more efficient. Let’s say you have a kitchen which as 10 LIFX bulbs in it. If you use the standard bulbs that ST gives you, if you want to turn them all on you have to turn 10 bulbs on. Weather you do it one by one manually, use a syncing app, or use CoRE, you are sending 10 commands to LIFX and then 10 commands back to your bulbs. Using LGoG you send a single command to LIFX and they in turn send a single command to your bulbs. You will find that your groups will become much more reliable.

### Control Multiple Groups of LIFX Bulbs as a Single Device
Yes, that is right. You can create multiple groups within the LIFX app and configure this device handler to control multiple groups as a single device. For example, I have bulbs in my kitchen, dining room, foyer, & family room. I have 1 device setup in SmartThings to control each of these (so 4 in total) and another device configured to control all of these groups as a single device called “First Floor Lights”.

### Create Multiple Devices for the Same Group or Set of Groups
This comes in handy when you want quick access to all of your light groups in a single “Room” within the SmartThings app, but also want to have the same device inside of the specific Room. For example I have my device “Kitchen Lights” in my Kitchen room and I have another device called “Kitchen Lights 2” which controls the same lights.

### Sync the Status of your Groups
As you can create multiple groups that control a device, you can now use the LIFX Sync companion application. Once configured, if you turn on “Kitchen Lights” it will automatically update the status of the “Kitchen Lights 2” device and vis versa. You can also setup the sync to be either one way or two way. [Must have companion app installed](https://github.com/ericvitale/ST-LIFX-Group-of-Groups/blob/master/smartapps/ericvitale/lifx-sync.src/lifx-sync.groovy)

1. 2-way Example: When "Group A" is changed (switch or level) "Group B" is updated. "Group B" is changed then "Group A" is updated to match "Group B".

2. 1-way Example: When "Group D" (Group D contains Group A, B, and C) is changed (switch or level), "Group C" is updated. When "Group C" is changed, "Group D" does not get updated.

### Scheduled Sync with LIFX
Schedule the DH to double check the light status via lifx and update the ST status accordingly.

### Power Usage Reporting
Report power usage based on the number of bulbs in a group. This was all based on real world testing as accurate as I could get.
