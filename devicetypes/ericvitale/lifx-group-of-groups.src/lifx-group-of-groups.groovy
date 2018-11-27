/**
 *  LIFX Group of Groups
 *
 *  Copyright 2016 ericvitale@gmail.com
 *
 *  Version 1.3.9 - (Vinay) Added Last Refresh tile, and reverted apiBreathe to previous version. (11/27/2018)
 *  Version 1.3.8 - (Vinay) UI Updates, sync changes and added preference for LIFX refresh. (11/19/2018)
 *  Version 1.3.7 - Fix for new ST app. (08/19/2018)
 *  Version 1.3.6 - Added more activity feed filtering. (10/9/2017) 
 *  Version 1.3.5 - Reduced activity feed chatter, also added a setting to disable on/off & setLevel messages. (10/8/2017)
 *  Version 1.3.4 - Fixed looping issue with retrying when lights are offline. (07/30/2017)
 *  Version 1.3.3 - Cleaned up a bit. (06/30/2017)
 *  Version 1.3.2 - Added the ability to use separate durations for on/off and setLevel commands. (06/26/2017)
 *  Version 1.3.1 - Added setLevelAndTemperature method to allow webCoRE set both with a single command. (06/25/2017)
 *  Version 1.3.0 - Updated to use the ST Beta Asynchronous API. (06/22/17)
 *  Version 1.2.5 - Added the apiFlash() methiod. apiFlash(cycles=5, period=0.5, brightness1=1.0, brightness2=0.0) (06/16/2017)
 *  Version 1.2.4 - Added saturation:0 to setColorTemperature per LIFX's recommendation. (05/22/2017)
 *  Version 1.2.3 - Fixed an issue with setColor() introduced by an api change. (05/19/2017)
 *  Version 1.2.2 - Fixed a bug introduced in version 1.2.1. (05/18/2017)
 *  Version 1.2.1 - Fixed an issue with poll not sending the correct group list to LIFX, they must have changed the api. (05/18/2017)
 *  Version 1.2.0 - Added the ability to execute the pulse and breathe command via CoRE or webCoRE using runEffect(effect="pulse", color="blue", from_color="red", cycles=5, period=0.5). (05/13/2017)
 *  Version 1.1.9 - Added custom command for runEffect and the ability to use "all" as a group. (05/07/2017)
 *  Version 1.1.8 - Added the power meter ability. (12/15/2016)
 *  Version 1.1.7 - Added the ability to sync with other groups using the LIFX Sync companion app. (11/8/2016)
 *  Version 1.1.6 - Added support for setLevel(level, duration), setHue, setSaturation. (10/05/2016)
 *  Version 1.1.5 - Changed lower end of color temperature from 2700K to 2500K per the LIFX spec.
 *  Version 1.1.4 - Further updated setLevel(...). No longer sends on command so that lights go to level immediatly and 
 *    not to the previous level. Same for color and color temperature. (07/29/2016)
 *  Version 1.1.3 - Updated setLevel(...) to be a bit more efficient and to prevent a possible but unlikely 
 *    NullPointerException. (07/16/2016)
 *  Version 1.1.2 - Added these version numbers (07/15/2016)
 *  Version 1.1.1 - Updated auto frequency to accept numbers only 1..* (07/09/2016)
 *  Version 1.1.0 - Added auto refresh (07/09/2016)
 *  Version 1.0.0 - Initial Release (07/06/2016)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *  
 *  You can find the latest version of this device handler @ https://github.com/ericvitale/ST-LIFX-Group-of-Groups
 *  You can find the companion LIFX Sync app @ https://github.com/ericvitale/ST-LIFX-Sync
 *  You can find my other device handlers & SmartApps @ https://github.com/ericvitale
 *
 **/
 
include 'asynchttp_v1'

import java.text.DecimalFormat;

metadata {
    definition (name: "LIFX Group of Groups", namespace: "ericvitale", author: "ericvitale@gmail.com", vid: "generic-rgbw-color-bulb") {
        capability "Polling"
        capability "Switch"
        capability "Switch Level"
        capability "Color Control"
        capability "Refresh"
		capability "Color Temperature"
		capability "Actuator"
        capability "Sensor"
        
        command "setAdjustedColor"
        command "setColor"
        command "refresh"        
        command "poll"
        command "syncOn"
        command "syncOff"
        command "syncPartial"        
        command "runEffect"
        command "transitionLevel"
        command "apiFlash"
        command "apiBreathe"
        command "setLevelAndTemperature"
        
        attribute "colorName", "string"
        attribute "lightStatus", "string"
        attribute "deviceStatus", "string"
        attribute "refreshStatus", "string"
    }
    
    preferences {
    	input "token", "text", title: "LIFX API Token", required: true
        input "defaultRefreshSchedule", "number", title: "LIFX Refresh Schedule (Minutes)", required: true, defaultValue: 1
        input "group01", "text", title: "Group 1", required: true
        input "group02", "text", title: "Group 2", required: false
        input "group03", "text", title: "Group 3", required: false
        input "group04", "text", title: "Group 4", required: false
        input "group05", "text", title: "Group 5", required: false
        input "group06", "text", title: "Group 6", required: false
        input "group07", "text", title: "Group 7", required: false
        input "group08", "text", title: "Group 8", required: false
        input "group09", "text", title: "Group 9", required: false
        input "group10", "text", title: "Group 10", required: false       
       	input "defaultTransition", "decimal", title: "Level Transition Time (s)", required: true, defaultValue: 0.0
        input "defaultStateTransition", "decimal", title: "On/Off Transition Time (s)", required: true, defaultValue: 0.0        
        input "useActLog", "bool", title: "On/Off/Level Act. Feed", required: true, defaultValue: true
        input "useActLogDebug", "bool", title: "Debug Act. Feed", required: true, defaultValue: false
        input "logging", "enum", title: "Log Level", required: false, defaultValue: "INFO", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
    }
    
    tiles(scale: 2) {
    	multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"                
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#8c92ac", nextState:"turningOn"
                attributeState "partial", label:'${name}', icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#e86d13", nextState:"turningOn"
			}
        }
        
        multiAttributeTile(name:"switchDetails", type: "lighting", canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#8c92ac", nextState:"turningOn"
                attributeState "partial", label:'TURN ON', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#e86d13", nextState:"turningOn"
			}            
            
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setColor"
			}
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        		attributeState "default", action:"switch level.setLevel"
            }            
            
            tileAttribute("device.lightStatus", key: "SECONDARY_CONTROL") {
				attributeState "default", label: 'ON: ${currentValue}', action: "refresh.refresh"
			}            
        }            
        
        valueTile("brightness", "device.level", decoration: "flat", width: 2, height: 1) {
        	state "level", label: 'Brightness (%)'
        }
        
        controlTile("levelSliderControl", "device.level", "slider", width: 2, height: 1) {
        	state "level", action:"switch level.setLevel"
        }
        
        valueTile("colorTemp", "device.colorTemperature", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "colorTemp", label: 'Temperature (K)'
		}
        
		controlTile("colorTempSliderControl", "device.colorTemperature", "slider", width: 2, height: 1, range:"(2500..9000)") {
			state "colorTemp", action:"color temperature.setColorTemperature"
		}        
        
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"", action:"refresh.refresh", icon: "st.secondary.refresh"
		}
        
        valueTile("deviceStatus", "device.deviceStatus", decoration: "flat", width: 4, height: 1) {
        	state "deviceStatus", label: 'Bulbs Responding: \n${currentValue}'
        }
        
        valueTile("refreshStatus", "device.refreshStatus", decoration: "flat", width: 4, height: 1) {
        	state "refreshStatus", label: 'Last Refresh: \n${currentValue}'
        }

        main(["switch"])
        details(["switchDetails", "brightness", "levelSliderControl", "refresh", "colorTemp", "colorTempSliderControl", "deviceStatus", "refreshStatus"])
    }
}

def installed() {
	initialize()
}

def updated() {
	initialize()
}

def refresh() {
    poll()
}

def initialize() {
	log("Begin Initializing...", "DEBUG")
    getGroups(true)
    setDefaultTransitionDuration(defaultTransition)
    setDefaultStateTransitionDuration(defaultStateTransition)    
    setUseActivityLog(useActLog)
    setUseActivityLogDebug(useActLogDebug)
    setRefreshSchedule(defaultRefreshSchedule)
    setupSchedule()
    log("End Initializing...", "DEBUG")
}

def buildGroupList() {

	def groups = ""
	
    try {
        
        if(group01.toUpperCase() == "ALL") {
        	groups = "all"
            return groups
        } else {
	        groups = "group:" + group01
        }
        
        if(group02 != null) {
        	groups = groups + ",group:" + group02
        }
        
        if(group03 != null) {
            groups = groups + ",group:" + group03
        }
        
        if(group04 != null) {
			groups = groups + ",group:" + group04
        }
        
        if(group05 != null) {
            groups = groups + ",group:" + group05
        }
                
        if(group06 != null) {
            groups = groups + ",group:" + group06
        }
        
        if(group07 != null) {
            groups = groups + ",group:" + group07
        }
        
        if(group08 != null) {
            groups = groups + ",group:" + group08
        }
        
        if(group09 != null) {
            groups = groups + ",group:" + group09
        }
        
        if(group10 != null) {
            groups = groups + ",group:" + group10
        }
        
        return groups
    } catch(e) {
    	log(e, "ERROR")
        return ""
    }
}

def getGroups(refresh=false) {
	if(refresh || state.groups == null || state.groups == "") {
    	state.groups = buildGroupList()
    }
	return state.groups
}

private determineLogLevel(data) {
    switch (data?.toUpperCase()) {
        case "TRACE":
            return 0
            break
        case "DEBUG":
            return 1
            break
        case "INFO":
            return 2
            break
        case "WARN":
            return 3
            break
        case "ERROR":
        	return 4
            break
        default:
            return 1
    }
}

def log(data, type) {
    data = "LIFX-GoG -- ${device.label} -- ${data ?: ''}"
        
    if (determineLogLevel(type) >= determineLogLevel(settings?.logging ?: "INFO")) {
        switch (type?.toUpperCase()) {
            case "TRACE":
                log.trace "${data}"
                break
            case "DEBUG":
                log.debug "${data}"
                break
            case "INFO":
                log.info "${data}"
                break
            case "WARN":
                log.warn "${data}"
                break
            case "ERROR":
                log.error "${data}"
                break
            default:
                log.error "LIFX-GoG -- ${device.label} -- Invalid Log Setting"
        }
    }
}

def syncOn() {
	log("Received Sync ON...", "DEBUG")
	sendEvent(name: "switch", value: "on", displayed: getUseActivityLog(), data: [syncing: "true"])
}

def syncPartial() {
	log("Received Sync PARTIAL...", "DEBUG")
	sendEvent(name: "switch", value: "partial", displayed: getUseActivityLog(), data: [syncing: "true"])
}

def syncOff() {
	log("Received Sync OFF...", "DEBUG")
	sendEvent(name: "switch", value: "off", displayed: getUseActivityLog(), data: [syncing: "true"])
}

def on(duration=getDefaultStateTransitionDuration()) {
	log("Turning on...", "INFO")
    sendLIFXCommand(["power" : "on", "duration" : duration])
    sendEvent(name: "switch", value: "on", displayed: getUseActivityLog(), data: [syncing: "false"])
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLog())
}

def off(duration=getDefaultStateTransitionDuration(), sync=false) {
	log("Turning off...", "INFO")
    sendLIFXCommand(["power" : "off", "duration" : duration])
    sendEvent(name: "switch", value: "off", displayed: getUseActivityLog(), data: [syncing: "false"])
}

def transitionLevel(value, duration=getDefaultTransitionDuration()) {
	log("transitionLevel(${value}, ${duration})", "DEBUG")
	setLevel(value, duration)
}

def setLevel(level, duration=getDefaultTransitionDuration()) {
	log("Begin setting groups level to ${level} over ${duration} seconds.", "DEBUG")
    
    if (level > 100) {
		level = 100
	} else if (level <= 0 || level == null) {
		sendEvent(name: "level", value: 0)
		return off()
	}
    
    state.level = level
    
	sendEvent(name: "level", value: level, displayed: getUseActivityLog())
    sendEvent(name: "switch", value: "on", displayed: false)
    
    def brightness = level / 100
   
    sendLIFXCommand(["brightness": brightness, "power": "on", "duration" : duration])
}

def setColor(value) {
	log("Begin setting groups color to ${value}.", "DEBUG")
    
    def data = [:]
    data.hue = value.hue
    data.saturation = value.saturation
    data.level = device.currentValue("level")
    
    sendLIFXCommand([color: "saturation:${data.saturation / 100} hue:${data.hue * 3.6}"])
    
    sendEvent(name: "hue", value: value.hue, displayed: getUseActivityLogDebug())
    sendEvent(name: "saturation", value: value.saturation, displayed: getUseActivityLogDebug())
    sendEvent(name: "color", value: value.hex, displayed: getUseActivityLogDebug())
    sendEvent(name: "switch", value: "on", displayed: getUseActivityLogDebug())
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLogDebug())
}

def setColorTemperature(value) {
	log("Begin setting groups color temperature to ${value}.", "DEBUG")
    
    if(value < 2500) {
    	value = 2500
    } else if(value > 9000) {
    	value = 9000
    }
    
    sendLIFXCommand([color: "kelvin:${value} saturation:0"])
            
	sendEvent(name: "colorTemperature", value: value, displayed: getUseActivityLogDebug())
	sendEvent(name: "color", value: "#ffffff", displayed: getUseActivityLogDebug())
	sendEvent(name: "saturation", value: 0, displayed: getUseActivityLogDebug())
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLogDebug())
}

def setHue(val) {
	log("Begin setting groups hue to ${val}.", "DEBUG")
    
    sendLIFXCommand([color: "hue:${val}"])
    
    sendEvent(name: "hue", value: val, displayed: getUseActivityLogDebug())
    sendEvent(name: "switch", value: "on", displayed: getUseActivityLogDebug())
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLogDebug())
}

def setSaturation(val) {
	log("Begin setting groups saturation to ${val}.", "DEBUG")
    
    sendLIFXCommand([color: "saturation:${val}"])
    
    sendEvent(name: "saturation", value: val, displayed: getUseActivityLogDebug())
    sendEvent(name: "switch", value: "on", displayed: getUseActivityLogDebug())
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLogDebug())
}

def setLevelAndTemperature(level, temperature, duration=getDefaultTransitionDuration()) {
	log("Setting groups level to ${level} and color temperature to ${temperature} over ${duration} seconds.", "INFO")
    
    if (level > 100) {
		level = 100
	} else if (level <= 0 || level == null) {
		sendEvent(name: "level", value: 0)
		return off()
	}
    
    if(temperature < 2500) {
    	temperature = 2500
    } else if(temperature > 9000) {
    	temperature = 9000
    }
    
    state.level = level
	sendEvent(name: "level", value: level, displayed: getUseActivityLogDebug())
    sendEvent(name: "switch", value: "on", displayed: getUseActivityLog())
	sendEvent(name: "colorTemperature", value: temperature, displayed: getUseActivityLogDebug())
	sendEvent(name: "color", value: "#ffffff", displayed: getUseActivityLogDebug())
	sendEvent(name: "saturation", value: 0, displayed: getUseActivityLogDebug())
    
    def brightness = level / 100
    
    sendLIFXCommand([color : "kelvin:${temperature} saturation:0 brightness:${brightness}", "power" : "on", "duration" : duration])
}

def poll() {
	log("Polling...", "DEBUG")
    buildGroupList()
	sendLIFXInquiry()
}

def parse(description) {
}

def runEffect(effect="pulse", color="", from_color="", cycles=5, period=0.5, brightness=0.5) {
	log("runEffect(effect=${effect}, color=${color}: 1.0, from_color=${from_color}, cycles=${cycles}, period=${period}, brightness=${brightness}.", "DEBUG")

	if(effect != "pulse" && effect != "breathe") {
    	log("${effect} is not a value effect, defaulting to pulse.", "ERROR")
        effect = "pulse"
    }
	
    runLIFXEffect(["color" : "${color.toLowerCase()} brightness:${brightness}".trim(), "from_color" : "${from_color.toLowerCase()} brightness:${brightness}".trim(), "cycles" : "${cycles}" ,"period" : "${period}"], effect)
}

def apiFlash(cycles=5, period=0.5, brightness1=1.0, brightness2=0.0) {
    
    if(brightness1 < 0.0) {
    	brightness1 = 0.0
    } else if(brightness1 > 1.0) {
    	brightness1 = 1.0
    }
    
    if(brightness2 < 0.0) {
    	brightness2 = 0.0
    } else if(brightness2 > 1.0) {
    	brightness2 = 1.0
    }

	runLIFXEffect(["color" : "brightness:${brightness1}", "from_color" : "brightness:${brightness2}", "cycles" : "${cycles}" ,"period" : "${period}"], "pulse")
}

def apiBreathe(cycles=3, period=2.0, brightness1=1.0, brightness2=0.0) {
    
    if(brightness1 < 0.0) {
    	brightness1 = 0.0
    } else if(brightness1 > 1.0) {
    	brightness1 = 1.0
    }
    
    if(brightness2 < 0.0) {
    	brightness2 = 0.0
    } else if(brightness2 > 1.0) {
    	brightness2 = 1.0
    }

	runLIFXEffect(["color" : "brightness:${brightness1}", "from_color" : "brightness:${brightness2}", "cycles" : "${cycles}" ,"period" : "${period}"], "breathe")
}

def getHex(val) {
	if(val.toLowerCase() == "red") {
    	return "#ff0000"
   	} else if(val.toLowerCase() == "blue") {
    	return "#0000ff"
    } else if(val.toLowerCase() == "green") {
    	return "#00ff00"
    } else if(val.toLowerCase() == "orange") {
    	return "#ff8000"
    } else if(val.toLowerCase() == "yellow") {
    	return "#ffff00"
    } else if(val.toLowerCase() == "cyan") {
    	return "#00ffff"
    } else if(val.toLowerCase() == "purple") {
    	return "#800080"
    } else if(val.toLowerCase() == "pink") {
    	return "#ffb6c1"
    } else {
    	return "#ffffff"
    }
}

def setupSchedule() {
	log("Begin setupSchedule() with refresh schedule: ${getRefreshSchedule()} minutes.", "DEBUG")
    
    try {
	    unschedule(refresh)
    } catch(e) {
        log("Failed to unschedule! Exception ${e}", "ERROR")
        return
    }
    
    schedule("0 0/${getRefreshSchedule()} * * * ?", refresh)
}

def updateLightStatus(lightStatus) {
	def finalString = lightStatus
    if(finalString == null) {
    	finalString = "--"
    }
	sendEvent(name: "lightStatus", value: finalString, displayed: getUseActivityLogDebug())
}

def updateDeviceStatus(deviceStatus) {
	def finalString = deviceStatus
    if(finalString == null) {
    	finalString = "--"
    }
	sendEvent(name: "deviceStatus", value: finalString, displayed: getUseActivityLogDebug())
}

def updateRefreshStatus(refreshStatus) {
	def finalString = refreshStatus
    if(finalString == null) {
    	finalString = "Last Refresh: \n--"
    }
	sendEvent(name: "refreshStatus", value: finalString, displayed: getUseActivityLogDebug())
}

def getUseActivityLog() {
	if(state.useActivityLog == null) {
    	state.useActivityLog = true
    }
	return state.useActivityLog
}

def setUseActivityLog(value) {
	state.useActivityLog = value
}

def getUseActivityLogDebug() {
	if(state.useActivityLogDebug == null) {
    	state.useActivityLogDebug = false
    }
    return state.useActivityLogDebug
}

def setUseActivityLogDebug(value) {
	state.useActivityLogDebug = value
}

def getLastCommand() {
	return state.lastCommand
}

def setLastCommand(command) {
	state.lastCommand = command
}

def incRetryCount() {
	state.retryCount = state.retryCount + 1
}

def resetRetryCount() {
	state.retryCount = 0
}

def getRetryCount() {
	return state.retryCount
}

def getMaxRetry() {
	return 3
}

def getRetryWait(base, count) {
	
    if(count == 0) {
    	return base
    } else {
    	return base * (6 * count)
    }
}

def setDefaultTransitionDuration(value) {
	state.transitionDuration = value
}

def getDefaultTransitionDuration() {
	return state.transitionDuration
}

def setRefreshSchedule(value) {
	state.refreshSchedule = value
}

def getRefreshSchedule() {	
	return state.refreshSchedule
}

def setDefaultStateTransitionDuration(value) {
	state.onOffTransitionDuration = value
}

def getDefaultStateTransitionDuration() {
	if(state.onOffTransitionDuration == null) {
    	state.onOffTransitionDuration = 0.0
    }
	return state.onOffTransitionDuration
}

def retry() {
	if(getRetryCount() < getMaxRetry()) {
    	log("Retrying command...", "INFO")
        incRetryCount()
		runIn(getRetryWait(5, getRetryCount()), sendLastCommand )
    } else {
    	log("Too many retries...", "WARN")
        resetRetryCount()
    }
}

def sendLastCommand() {
	sendLIFXCommand(getLastCommand())
}

def sendLIFXCommand(commands) {

	setLastCommand(commands)
    
    def params = [
        uri: "https://api.lifx.com",
		path: "/v1/lights/" + getGroups() + "/state",
        headers: ["Content-Type": "application/json", "Accept": "application/json", "Authorization": "Bearer ${token}"],
        body: commands
    ]
    
    asynchttp_v1.put('putResponseHandler', params, commands)
}

def runLIFXEffect(commands, effect) {

	def params = [
        uri: "https://api.lifx.com",
		path: "/v1/lights/" + getGroups() + "/effects/" + effect,
        headers: ["Content-Type": "application/json", "Accept": "application/json", "Authorization": "Bearer ${token}"],
        body: commands
    ]
    
    asynchttp_v1.post('postResponseHandler', params, commands)
}

def sendLIFXInquiry() {

	def params = [
        uri: "https://api.lifx.com",
		path: "/v1/lights/" + getGroups(),
        headers: ["Content-Type": "application/x-www-form-urlencoded", "Authorization": "Bearer ${token}"]
    ]
    
    asynchttp_v1.get('getResponseHandler', params)
}

def postResponseHandler(response, data) {

	log("postResponseHandler: Commands sent: ${data}", "DEBUG")
    if(response.getStatus() == 200 || response.getStatus() == 207) {
		log("postResponseHandler: Response received from LIFX.", "DEBUG")        
        log("postResponseHandler: Response = ${response.getJson()}", "TRACE")
    } else {
    	log("postResponseHandler: LIFX failed to adjust group. LIFX returned ${response.getStatus()}.", "ERROR")
        log("postResponseHandler: Error = ${response.getErrorData()}", "ERROR")
    }
}

def putResponseHandler(response, data) {
	
    def commands = data // commands used for post
    log("putResponseHandler: Commands sent: ${commands}", "DEBUG")
    
    if(response.getStatus() == 200 || response.getStatus() == 207) {
		log("putResponseHandler: Response received from LIFX.", "DEBUG")
        log("putResponseHandler: Response = ${response.getJson()}", "TRACE")
        
        def totalBulbs = response.getJson().results.length()
        def results = response.getJson().results
        def bulbsOk = 0
        
        for(int i=0;i<totalBulbs;i++) {
        	if(results[i].status != "ok") {
        		log("${results[i].label} is ${results[i].status}.", "WARN")
            } else {
            	bulbsOk++
            	log("${results[i].label} is ${results[i].status}.", "TRACE")
            }
        }
        
        if(bulbsOk == totalBulbs) { 
            log("putResponseHandler: ${bulbsOk} of ${totalBulbs} bulbs OK.", "INFO")
            resetRetryCount()
        } else {
        	log("putResponseHandler: ${bulbsOk} of ${totalBulbs} bulbs OK.", "WARN")
            log("putResponseHandler: Retry Count = ${getRetryCount()}.", "INFO")
            retry()
        }
        updateDeviceStatus("${bulbsOk} of ${totalBulbs}")
		
        def onStatus = "--"
        if (commands.power == "off" && bulbsOk == totalBulbs) {
        	onStatus = "0 of ${totalBulbs}" //all bulbs did turn off successfully since response was "ok"
        }
        else if (commands.power == "off" && bulbsOk != totalBulbs) {
        	onStatus = "${totalBulbs - bulbsOk} of ${totalBulbs}" // all bulbs didn't turn off successfully
        }
        else {        	
        	onStatus = "${bulbsOk} of ${totalBulbs}"
        }
        
        log("putResponseHandler: ${onStatus} bulbs ON.", "INFO")
        updateLightStatus(onStatus)
        
    } else {
    	log("putResponseHandler: LIFX failed to adjust group. LIFX returned ${response.getStatus()}.", "ERROR")
        log("putResponseHandler: Error = ${response.getErrorData()}", "ERROR")
    }
}

def getResponseHandler(response, data) {

	log("getResponseHandler: Command sent [Inquiry].", "DEBUG")
    if(response.getStatus() == 200 || response.getStatus() == 207) {    
		log("getResponseHandler: Response received from LIFX.", "DEBUG")
        log("getResponseHandler: Response ${response.getJson()}", "TRACE")
        
        DecimalFormat df = new DecimalFormat("###,##0.0#")
        DecimalFormat dfl = new DecimalFormat("###,##0.000")
        DecimalFormat df0 = new DecimalFormat("###,##0")
        
        def totalBulbs = 0
        def bulbsOn = 0
        def bulbsOk = 0
        
       	response.getJson().each {        
        	log("getResponseHandler: ${it.label} is ${it.power}.", "TRACE")
            log("getResponseHandler: Connected = ${it.connected}.", "TRACE")
        	log("getResponseHandler: Bulb Type: ${it.product.name}.", "TRACE")
        	log("getResponseHandler: Has variable color temperature = ${it.product.capabilities.has_variable_color_temp}.", "TRACE")
            log("getResponseHandler: Has color = ${it.product.capabilities.has_color}.", "TRACE")
            log("getResponseHandler: Has ir = ${it.product.capabilities.has_ir}.", "TRACE")
            log("getResponseHandler: Has Multizone = ${it.product.capabilities.has_multizone}.", "TRACE")
        	log("getResponseHandler: Brightness = ${it.brightness}.", "TRACE")            
        	log("getResponseHandler: Color = [saturation:${it.color.saturation}], kelvin:${it.color.kelvin}, hue:${it.color.hue}.", "TRACE")        	
			
            totalBulbs++
            
            if(it.connected) {
            	bulbsOk++
                
                if(it.power == "on") {
                    //sendEvent(name: "switch", value: "on")
                    bulbsOn++
                
                    if(it.color.saturation == 0.0) {
                        log("getResponseHandler: Saturation is 0.0, setting color temperature.", "TRACE")

                        def b = df0.format(it.brightness * 100)

                        sendEvent(name: "colorTemperature", value: it.color.kelvin, displayed: getUseActivityLogDebug())
                        sendEvent(name: "color", value: "#ffffff", displayed: getUseActivityLogDebug())
                        sendEvent(name: "level", value: b, displayed: getUseActivityLogDebug())
                        
                    } else {
                        log("getResponseHandler: Saturation is > 0.0, setting color.", "TRACE")

                        def h = df.format(it.color.hue)
                        def s = df.format(it.color.saturation)
                        def b = df0.format(it.brightness * 100)

                        log("h = ${h}, s = ${s}, b = ${b}.", "TRACE")
                        sendEvent(name: "hue", value: h, displayed: getUseActivityLogDebug())
                        sendEvent(name: "saturation", value: s, displayed: getUseActivityLogDebug())
                        sendEvent(name: "kelvin", value: it.color.kelvin, displayed: getUseActivityLogDebug())
                        sendEvent(name: "level", value: b, displayed: getUseActivityLogDebug())
                    }
                }
            }            
        }
        
        log("getResponseHandler: ${bulbsOk} of ${totalBulbs} bulbs OK.", "DEBUG")
        updateDeviceStatus("${bulbsOk} of ${totalBulbs}")
        
        log("getResponseHandler: ${bulbsOn} of ${totalBulbs} bulbs ON.", "DEBUG")        
        updateLightStatus("${bulbsOn} of ${totalBulbs}")
        
        def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        updateRefreshStatus(now)
        
        if (bulbsOn == totalBulbs) {
        	sendEvent(name: "switch", value: "on", displayed: getUseActivityLogDebug())
        }
        else if(bulbsOn == 0) {
        	sendEvent(name: "switch", value: "off", displayed: getUseActivityLogDebug())
        }
        else {
        	sendEvent(name: "switch", value: "partial", displayed: getUseActivityLogDebug())
        }        
    } else {
    	log("getResponseHandler: LIFX failed to update the group. LIFX returned ${response.getStatus()}.", "ERROR")
        log("getResponseHandler: Error = ${response.getErrorData()}", "ERROR")
    }
}
