# ST-LIFX-Group-of-Groups

## Summary
This SmartThings device handler allows you to create a "virtual" device based on a set of LIFX groups. If you have a group called "Kitchen" and a group called "Dining Room", you can create a group of groups called "First Floor" (or whatever) and add the two LIFX groups to it. You can treat the two groups as a single device. This handles up to 10 groups per device.

## This device handler supports
On / Off
Setting Color
Setting Color Temperature
Setting Brightness
2 to 10 groups as a single device (no point in doing just one) - If you want to use just one use this device handler (https://github.com/ericvitale/other/blob/master/devicetypes/lifx/lifx-group.src/lifx-group.groovy).

## Inputs
1. API Token - [Required] You have to get this from LIFX. It is a long character string so text it to yourself and copy and paste it in.
2. Groups 1 to 10 - [Required, 1st & 2nd] You can choose from up to 10 groups, the first two are required. Enter the group name, case sensitive. No need to enter a group id.
3. Log Level - Enter: TRACE, DEBUG, INFO, WARN, ERROR

## Acknowledgements
Insperation to create this device handler came from AdamV (https://github.com/adampv/smartthings/blob/master/LIFXGroupversion.groovy). Adam also credits Nicolas Cerveaux.

## How to get your API Token
Navigate to https://cloud.lifx.com, sign in and then go to the settings section and select generate new token. save the token somewhere safe.
