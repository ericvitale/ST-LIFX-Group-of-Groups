# ST-LIFX-Group-of-Groups

## Summary
This SmartThings device handler allows you to create a "virtual" device based on a single LIFIX group or a set of LIFX groups. 

Example 1: If you have a group called "Kitchen" and a group called "Dining Room", you can create a group of groups called "First Floor" (or whatever) and add the two LIFX groups to it. You can treat the two groups as a single device. This handles up to 10 groups per device.

Example 2: If you have a single group within the LIFX called "Kitchen" you can add it and control your set of lights as a single device called, you guessed it "Kitchen" (or whatever else).

This device handler also supports scenes, see the preferences section below.

## This device handler supports
On / Off
Setting Color
Setting Color Temperature
Setting Brightness
1 to 10 groups as a single device
0 to 3 scenes for the group (unfortunatly if you don't use these you cannot remove them from UI via settings)

## Preferences
1. API Token - [Required] You have to get this from LIFX. It is a long character string so text it to yourself and copy and paste it in.
2. Groups 1 to 10 - [Required, 1st] You can choose from up to 10 groups, the first group is required. Enter the group name, case sensitive. No need to enter a group id.
3. Scenes 1 to 3 - Select a brightness level and a color (kelvin temperatures should be entered like this: "kelvin:2750" with no quotes.
3. Log Level - Enter: TRACE, DEBUG, INFO, WARN, ERROR

## Acknowledgements
Insperation to create this device handler came from AdamV (https://github.com/adampv/smartthings/blob/master/LIFXGroupversion.groovy). Adam also credits Nicolas Cerveaux.

## How to get your API Token
Navigate to https://cloud.lifx.com, sign in and then go to the settings section and select generate new token.
