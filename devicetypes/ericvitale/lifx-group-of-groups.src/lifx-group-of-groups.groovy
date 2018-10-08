/**
 *  LIFX Group of Groups
 *
 *  Copyright 2016 ericvitale@gmail.com
 *
 *  Version 1.3.7 - Added support for the fast setting. https://api.developer.lifx.com/docs/set-state#fast-mode
 *    Resolved bug that was impacting the effects functionality. (10/7/2018)
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

private version() {
	return "1.3.7b"
}

metadata {
    definition (name: "LIFX Group of Groups", namespace: "ericvitale", author: "ericvitale@gmail.com") {
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
        command "runEffect"
        command "transitionLevel"
        command "apiFlash"
        command "apiBreathe"
        command "setLevelAndTemperature"
        
        attribute "colorName", "string"
        attribute "lightStatus", "string"
    }
    
    preferences {
    	section("LIFX Settings") {
        	input "token", "text", title: "API Token", required: true
        	input "fast", "bool", title: "Use Fast?", required: true, defaultValue: true
            input "defaultTransition", "decimal", title: "Level Transition Time (s)", required: true, defaultValue: 0.0
	        input "defaultStateTransition", "decimal", title: "On/Off Transition Time (s)", required: true, defaultValue: 0.0
        }
        
        section("Groups") {
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
        }
       
       	section("Logging") {
	        input "useActLog", "bool", title: "On/Off/Level Act. Feed", required: true, defaultValue: true
   	    	input "useActLogDebug", "bool", title: "Debug Act. Feed", required: true, defaultValue: false
    	    input "logging", "enum", title: "Log Level", required: false, defaultValue: "INFO", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
        }
    }
    
    tiles(scale: 2) {
    	multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#fffA62", nextState:"turningOn"
			}
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        		attributeState "default", action:"switch level.setLevel"
            }
            
            tileAttribute ("device.lightStatus", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'${currentValue}', action: "refresh.refresh"
			}
        }
        
        multiAttributeTile(name:"switchDetails", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#fffA62", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#fffA62", nextState:"turningOn"
			}
            
            tileAttribute ("device.lightStatus", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'${currentValue}', action: "refresh.refresh"
			}
        }
        
        valueTile("Brightness", "device.level", width: 2, height: 1) {
        	state "level", label: 'Brightness ${currentValue}%'
        }
        
        controlTile("levelSliderControl", "device.level", "slider", width: 4, height: 1) {
        	state "level", action:"switch level.setLevel"
        }
        
        valueTile("colorTemp", "device.colorTemperature", inactiveLabel: false, decoration: "flat", height: 1, width: 2) {
			state "colorTemp", label: '${currentValue}K'
		}
        
		controlTile("colorTempSliderControl", "device.colorTemperature", "slider", height: 1, width: 4, inactiveLabel: false, range:"(2500..9000)") {
			state "colorTemp", action:"color temperature.setColorTemperature"
		}
        
        controlTile("rgbSelector", "device.color", "color", height: 6, width: 6, inactiveLabel: false) {
            state "color", action:"setColor"
        }
        
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"", action:"refresh.refresh", icon: "st.secondary.refresh"
		}

        main(["switch"])
        details(["switchDetails", "Brightness", "levelSliderControl", "colorTemp", "colorTempSliderControl", "rgbSelector", "refresh"])
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
	log("Initializing...", "DEBUG")
    getGroups(true)
    setupSchedule()
    setDefaultTransitionDuration(defaultTransition)
    setDefaultStateTransitionDuration(defaultStateTransition)
    setUseActivityLog(useActLog)
    setUseActivityLogDebug(useActLogDebug)
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
    data = "LIFX-GoG.${version()}.${device.label} -- ${data ?: ''}"
        
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
	sendEvent(name: "switch", value: "on", displayed: getUseActivityLog(), data: [syncing: "true"])
}

def on(duration=getDefaultStateTransitionDuration()) {
	log("Turning on...", "INFO")
    sendLIFXCommand(["power" : "on", "duration" : duration, "fast" : getFast()])
    sendEvent(name: "switch", value: "on", displayed: getUseActivityLog(), data: [syncing: "false"])
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLog())
}

def syncOff() {
	 sendEvent(name: "switch", value: "off", displayed: getUseActivityLog(), data: [syncing: "true"])
}

def off(duration=getDefaultStateTransitionDuration(), sync=false) {
	log("Turning off...", "INFO")
    sendLIFXCommand(["power" : "off", "duration" : duration, fast: getFast()])
    sendEvent(name: "switch", value: "off", displayed: getUseActivityLog(), data: [syncing: "false"])
}

def transitionLevel(value, duration=getDefaultTransitionDuration()) {
	log("transitionLevel(${value}, ${duration})", "DEBUG")
	setLevel(value, duration)
}

def setLevel(level, duration=getDefaultTransitionDuration()) {
	log("Begin setting groups level to ${value} over ${duration} seconds.", "DEBUG")
    
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
   
    sendLIFXCommand(["brightness": brightness, "power": "on", "duration" : duration, "fast" : getFast()])
}

def setColor(value) {
	log("Begin setting groups color to ${value}.", "DEBUG")
    
    def data = [:]
    data.hue = value.hue
    data.saturation = value.saturation
    data.level = device.currentValue("level")
    
    sendLIFXCommand([color: "saturation:${data.saturation / 100} hue:${data.hue * 3.6}", "fast" : getFast()])
    
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
    
    sendLIFXCommand([color: "kelvin:${value} saturation:0", "fast" : getFast()])
            
	sendEvent(name: "colorTemperature", value: value, displayed: getUseActivityLogDebug())
	sendEvent(name: "color", value: "#ffffff", displayed: getUseActivityLogDebug())
	sendEvent(name: "saturation", value: 0, displayed: getUseActivityLogDebug())
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLogDebug())
}

def setHue(val) {
	log("Begin setting groups hue to ${val}.", "DEBUG")
    
    sendLIFXCommand([color: "hue:${val}", "fast" : getFast()])
    
    sendEvent(name: "hue", value: val, displayed: getUseActivityLogDebug())
    sendEvent(name: "switch", value: "on", displayed: getUseActivityLogDebug())
    sendEvent(name: "level", value: "${state.level}", displayed: getUseActivityLogDebug())
}

def setSaturation(val) {
	log("Begin setting groups saturation to ${val}.", "DEBUG")
    
    sendLIFXCommand([color: "saturation:${val}", "fast" : getFast()])
    
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
    
    sendLIFXCommand([color : "kelvin:${temperature} saturation:0 brightness:${brightness}", "power" : "on", "duration" : duration, "fast": getFast()])
}

def poll() {
	log("Polling...", "DEBUG")
    buildGroupList()
	sendLIFXInquiry()
}

def parse(description) {
}

def runEffect(effect="pulse", color="0.1", from_color="0.0", cycles=5.0, period=0.5, brightness=0.5) {
	log("runEffect(effect=${effect}, color=${color}, from_color=${from_color}, cycles=${cycles}, period=${period}, brightness=${brightness}.", "DEBUG")

	if(effect != "pulse" && effect != "breathe") {
    	log("${effect} is not a value effect, defaulting to pulse.", "ERROR")
        effect = "pulse"
    }
	
    runLIFXEffect(["color" : "${color.toLowerCase()} brightness:${brightness}".trim(), "from_color" : "${from_color.toLowerCase()} brightness:${brightness}".trim(), "cycles" : cycles ,"period" : period, "power_on" : true], effect)
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

	runLIFXEffect(["color" : "brightness:${brightness1}", "from_color" : "brightness:${brightness2}", "cycles" : cycles, "period" : period, "power_on" : true], "pulse")
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

	runLIFXEffect(["color" : "brightness:${brightness1}", "from_color" : "brightness:${brightness2}", "cycles" : cycles ,"period" : period, "power_on" : true], "breathe")
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
	log("Begin setupSchedule().", "DEBUG")
    
    try {
	    unschedule(refresh)
    } catch(e) {
        log("Failed to unschedule! Exception ${e}", "ERROR")
        return
    }
    
    runEvery1Minute(refresh)
}

def updateLightStatus(lightStatus) {
	def finalString = lightStatus
    if(finalString == null) {
    	finalString = "--"
    }
	sendEvent(name: "lightStatus", value: finalString, displayed: getUseActivityLogDebug())
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

def setFast(value) {
	state.fastSetting = value
}

def getFast() {
	if(state.fastSetting == null) {
    	state.fastSetting = true
 	}
	return state.fastSetting
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
    
    asynchttp_v1.put('putResponseHandler', params)
}

def runLIFXEffect(commands, effect) {

	def params = [
        uri: "https://api.lifx.com",
		path: "/v1/lights/" + getGroups() + "/effects/" + effect,
        headers: ["Content-Type": "application/json", "Accept": "application/json", "Authorization": "Bearer ${token}"],
        body: commands
    ]
    
    asynchttp_v1.post('postResponseHandler', params)
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

    if(response.getStatus() == 200 || response.getStatus() == 207) {
		log("Response received from LFIX in the postReponseHandler.", "DEBUG")
    } else {
    	log("LIFX failed to adjust group. LIFX returned ${response.getStatus()}.", "ERROR")
        log("Error = ${response.getErrorData()}", "ERROR")
    }
}

def putResponseHandler(response, data) {

    if(response.getStatus() == 200 || response.getStatus() == 207) {
		log("Response received from LFIX in the putReponseHandler.", "DEBUG")
        
        log("Response = ${response.getJson()}", "DEBUG")
        
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
            log("${bulbsOk} of ${totalBulbs} bulbs returned ok.", "INFO")
            resetRetryCount()
        } else {
        	log("${bulbsOk} of ${totalBulbs} bulbs returned ok.", "WARN")
            log("Retry Count = ${getRetryCount()}.", "INFO")
            retry()
        }

        updateLightStatus("${bulbsOk} of ${totalBulbs}")
        
    } else {
    	log("LIFX failed to adjust group. LIFX returned ${response.getStatus()}.", "ERROR")
        log("Error = ${response.getErrorData()}", "ERROR")
    }
}

def getResponseHandler(response, data) {

    if(response.getStatus() == 200 || response.getStatus() == 207) {
		log("Response received from LFIX in the getReponseHandler.", "DEBUG")
        
        log("Response ${response.getJson()}", "DEBUG")
        
       	response.getJson().each {
        	log("${it.label} is ${it.power}.", "TRACE")
        	log("Bulb Type: ${it.product.name}.", "TRACE")
        	log("Has variable color temperature = ${it.product.capabilities.has_variable_color_temp}.", "TRACE")
            log("Has color = ${it.product.capabilities.has_color}.", "TRACE")
            log("Has ir = ${it.product.capabilities.has_ir}.", "TRACE")
            log("Has Multizone = ${it.product.capabilities.has_multizone}.", "TRACE")
        	log("Brightness = ${it.brightness}.", "TRACE")
        	log("Color = [saturation:${it.color.saturation}], kelvin:${it.color.kelvin}, hue:${it.color.hue}.", "TRACE")
        
        	DecimalFormat df = new DecimalFormat("###,##0.0#")
        	DecimalFormat dfl = new DecimalFormat("###,##0.000")
        	DecimalFormat df0 = new DecimalFormat("###,##0")

            if(it.power == "on") {
                sendEvent(name: "switch", value: "on")
                if(it.color.saturation == 0.0) {
                    log("Saturation is 0.0, setting color temperature.", "TRACE")

                    def b = df0.format(it.brightness * 100)

                    sendEvent(name: "colorTemperature", value: it.color.kelvin, displayed: getUseActivityLogDebug())
                    sendEvent(name: "color", value: "#ffffff", displayed: getUseActivityLogDebug())
                    sendEvent(name: "level", value: b, displayed: getUseActivityLogDebug())
                    sendEvent(name: "switch", value: "on", displayed: getUseActivityLogDebug())
                } else {
                    log("Saturation is > 0.0, setting color.", "TRACE")
                    def h = df.format(it.color.hue)
                    def s = df.format(it.color.saturation)
                    def b = df0.format(it.brightness * 100)

                    log("h = ${h}, s = ${s}, b = ${b}.", "TRACE")

                    sendEvent(name: "hue", value: h, displayed: getUseActivityLogDebug())
                    sendEvent(name: "saturation", value: s, displayed: getUseActivityLogDebug())
                    sendEvent(name: "kelvin", value: it.color.kelvin, displayed: getUseActivityLogDebug())
                    sendEvent(name: "level", value: b, displayed: getUseActivityLogDebug())
                    sendEvent(name: "switch", value: "on", displayed: getUseActivityLogDebug())
                }
            } else if(it.power == "off") {
                sendEvent(name: "switch", value: "off", displayed: getUseActivityLogDebug())
            }
        }
    } else {
    	log("LIFX failed to update the group. LIFX returned ${response.getStatus()}.", "ERROR")
        log("Error = ${response.getErrorData()}", "ERROR")
    }
}
