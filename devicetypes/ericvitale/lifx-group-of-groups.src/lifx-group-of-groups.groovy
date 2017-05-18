/**
 *  LIFX Group of Groups
 *
 *  Copyright 2016 ericvitale@gmail.com
 * 
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
        capability "Power Meter"
        
        command "setAdjustedColor"
        command "setColor"
        command "refresh"
        command "poll"
        command "sceneOne"
        command "sceneTwo"
        command "sceneThree"
        command "sceneFour"
        command "sceneFive"
        command "syncOn"
        command "syncOff"
        command "runEffect"
        
        attribute "colorName", "string"
        attribute "lightStatus", "string"
        attribute "powerUsageText", "string"
    }
    
    preferences {
    	input "token", "text", title: "API Token", required: true
        
        input "numberOfBulbs", "number", title: "Number of Bulbs", required: true, defaultValue: 0
        input "maxWatts", "decimal", title: "Number of Watts @ 100%", required: true, defaultValue: 11.0
        input "powerReportMinutes", "number", title: "Report every X minutes?", required: true, defaultValue: 1
        
        input "group01", "text", title: "Group 1", required: true//, submitOnChange: true
     	
        (2..10).each() { n->
        	input "group0${n}", "text", title: "Group ${n}", required: false
        }
        (1..5).each() { n->
        	input "scene0${n}Brightness", "number", title: "Scene ${n} - Brightness", required: false
            input "scene0${n}Color", "text", title: "Scene ${n} - Color/Kelvin", required: false, description: "Options: white, red, orange, yellow, cyan, green, blue, purple, pink, or kelvin:[2700-9000]"
        }
       
        input "logging", "enum", title: "Log Level", required: false, defaultValue: "INFO", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
        input "useSchedule", "bool", title: "Use Schedule", required: false, defaultValue: false
       	input "frequency", "number", title: "Frequency?", required: true, range: "1..*", defaultValue: 15
        input "startHour", "number", title: "Schedule Start Hour", required: true, range: "0..23", defaultValue: 7
        input "endHour", "number", title: "Schedule End Hour", required: true, range: "0..23", defaultValue: 23
    }

    simulator {
    }
    
    tiles(scale: 2) {
    	multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "onish", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ff0000", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#fffA62", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#fffA62", nextState:"turningOn"
			}
            
            /*tileAttribute ("device.level", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'${currentValue}%'
			}*/
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        		attributeState "default", action:"switch level.setLevel"
            }
            
            tileAttribute ("device.lightStatus", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'${currentValue}', action: "refresh.refresh"
			}
        }
        
        multiAttributeTile(name:"switchDetails", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "onish", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ff0000", nextState:"turningOn"
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
        
        standardTile("sceneOne", "device.sceneOne", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"Scene One", action:"sceneOne", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png"
		}
        
        standardTile("sceneTwo", "device.sceneTwo", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"Scene Two", action:"sceneTwo", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png"
		}
        
        standardTile("sceneThree", "device.sceneThree", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"Scene Three", action:"sceneThree", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png"
		}
        
        standardTile("sceneFour", "device.sceneFour", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"Scene Four", action:"sceneFour", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png"
		}
        
        standardTile("sceneFive", "device.sceneFive", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"Scene Five", action:"sceneFive", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png"
		}

        valueTile("PowerUsage", "device.powerUsageText", width: 4, height: 1) {
        	state "default", label: '${currentValue}'
        }

        main(["switch"])
        details(["switchDetails", "Brightness", "levelSliderControl", "colorTemp", "colorTempSliderControl", "rgbSelector", "sceneOne", "sceneTwo", "sceneThree", "sceneFour", "sceneFive", "refresh", "PowerUsage"])
    }
}

def groupList =  ""

def installed() {
	log("Begin installed().", "DEBUG")
	initialize()
    log("End installed().", "DEBUG")
}

def updated() {
	log("Begin updated().", "DEBUG")
	initialize()
    log("End updated().", "DEBUG")
}

def refresh() {
	log("Begin referesh().", "DEBUG")
    poll()
    log("End refresh().", "DEBUG")
}

def initialize() {
	log("Begin initialize.", "DEBUG")
    unschedule()
    buildGroupList()
    setupSchedule()
    
    if(powerReportMinutes > 0 && numberOfBulbs > 0) {
	    schedule("0 0/${powerReportMinutes} * * * ?", reportPowerUsage)
        reportPowerUsage()
    }
    
    log("Number of Bulbs = ${numberOfBulbs}.", "INFO")
    log("Max Watts per bulb = ${maxWatts}.", "INFO")
    log("Report every ${powerReportMinutes} minutes.", "INFO")
    
	log("End initialize.", "DEBUG")
}

def buildGroupList() {
	log("Begin method buildGroupList().", "DEBUG")

    try {
        
        if(group01.toUpperCase() == "ALL") {
        	state.groupsList = "all"
            return
        } else {
	        state.groupsList = "group:" + group01
        }
        
        if(group02 != null) {
        	state.groupsList = "," + state.groupsList + "group:" + group02
        }
        
        if(group03 != null) {
            state.groupsList = "," + state.groupsList + "group:" + group03
        }
        
        if(group04 != null) {
			state.groupsList = "," + state.groupsList + "group:" + group04
        }
        
        if(group05 != null) {
            state.groupsList = "," + state.groupsList + "group:" + group05
        }
                
        if(group06 != null) {
            state.groupsList = "," + state.groupsList + "group:" + group06
        }
        
        if(group07 != null) {
            state.groupsList = "," + state.groupsList + "group:" + group07
        }
        
        if(group08 != null) {
            state.groupsList = "," + state.groupsList + "group:" + group08
        }
        
        if(group09 != null) {
            state.groupsList = "," + state.groupsList + "group:" + group09
        }
        
        if(group10 != null) {
            state.groupsList = "," + state.groupsList + "group:" + group10
        }
    } catch(e) {
    	log(e, "ERROR")
    }
    
    log("GroupsList = <<<${state.groupsList}>>>", "DEBUG")
    
    log("End method buildGroupList().", "DEBUG")
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
	sendEvent(name: "switch", value: "on", data: [syncing: "true"])
    reportPowerUsage()
}

def on() {
	log("Begin turning groups on", "DEBUG")
    buildGroupList()
    log("The group list is ${state.groupList}.", "DEBUG")
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", "power=on&duration=0.0")
    sendEvent(name: "switch", value: "on", data: [syncing: "false"])
    sendEvent(name: "level", value: "${state.level}")
    reportPowerUsage()
    log("state.level = ${state.level}", "DEBUG")
    log("End turning groups on", "DEBUG")
}

def syncOff() {
	 sendEvent(name: "switch", value: "off", data: [syncing: "true"])
     reportPowerUsage()
}

def off(sync=false) {
	log("Begin turning groups off", "DEBUG")
    buildGroupList()
    log("The group list is ${state.groupList}.", "DEBUG")
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", "power=off&duration=0.0")
    sendEvent(name: "switch", value: "off", data: [syncing: "false"])
    reportPowerUsage()
    log("state.level = ${state.level}", "DEBUG")
    log("End turning groups off", "DEBUG")
}

def setLevel(value) {
	log("Begin setting groups level to ${value}.", "DEBUG")
    
    def data = [:]
    data.hue = device.currentValue("hue")
    data.saturation = device.currentValue("saturation")
    data.level = value
    
    if (data.level < 1 && data.level > 0) {
		data.level = 1
	} else if (data.level == 0 || data.level == null) {
		sendEvent(name: "level", value: 0)
        reportPowerUsage()
		return off()
	}
    
    def brightness = data.level / 100
    
    buildGroupList()
	sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", ["brightness": brightness, "power": "on"])
    
    state.level = value

    sendEvent(name: "level", value: value)
    sendEvent(name: "switch", value: "on")
    reportPowerUsage()
    
    log("state.level = ${state.level}", "DEBUG")
    log("End setting groups level to ${value}.", "DEBUG")
}

def setLevel(value, duration) {
	log("Begin setting groups level to ${value} over ${duration} seconds.", "DEBUG")
    
    def data = [:]
    data.hue = device.currentValue("hue")
    data.saturation = device.currentValue("saturation")
    data.level = value
    
    if (data.level < 1 && data.level > 0) {
		data.level = 1
	} else if (data.level == 0 || data.level == null) {
		sendEvent(name: "level", value: 0)
        reportPowerUsage()
		return off()
	}
    
    def brightness = data.level / 100
    def durationSeconds = duration / 1000
    
    buildGroupList()
	sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", ["brightness": brightness, "power": "on", "duration": durationSeconds])
    
    state.level = value

    sendEvent(name: "level", value: value)
    sendEvent(name: "switch", value: "on")
    reportPowerUsage()
    
    log("state.level = ${state.level}", "DEBUG")
    log("End setting groups level.", "DEBUG")
}

def setColor(value) {
	log("Begin setting groups color to ${value}.", "DEBUG")
    
    def data = [:]
    data.hue = value.hue
    data.saturation = value.saturation
    data.level = device.currentValue("level")
    
    buildGroupList()
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", [color: "saturation:${data.saturation / 100}+hue:${data.hue * 3.6}"])
    
    sendEvent(name: "hue", value: value.hue)
    sendEvent(name: "saturation", value: value.saturation)
    sendEvent(name: "color", value: value.hex)
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: "${state.level}")
    reportPowerUsage()
    
    log("End setting groups color to ${value}.", "DEBUG")
}

def setColorTemperature(value) {
	log("Begin setting groups color temperature to ${value}.", "DEBUG")
    
    buildGroupList()
	sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", [color: "kelvin:${value}"])
            
	sendEvent(name: "colorTemperature", value: value)
	sendEvent(name: "color", value: "#ffffff")
	sendEvent(name: "saturation", value: 0)
    sendEvent(name: "level", value: "${state.level}")
    reportPowerUsage()
    
    log("End setting groups color temperature to ${value}.", "DEBUG")
}

def setHue(val) {
	log("Begin setting groups hue to ${val}.", "DEBUG")
    
    buildGroupList()
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", [color: "hue:${val}"])
    
    sendEvent(name: "hue", value: val)
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: "${state.level}")
    reportPowerUsage()
    
    log("End setting groups hue to ${val}.", "DEBUG")
}

def setSaturation(val) {
	log("Begin setting groups saturation to ${val}.", "DEBUG")
    
    buildGroupList()
    
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", [color: "saturation:${val}"])
    
    sendEvent(name: "saturation", value: val)
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "level", value: "${state.level}")
    reportPowerUsage()
    
    log("End setting groups saturation to ${val}.", "DEBUG")
}

private sendMessageToLIFX(path, method="GET", body=null) {
    def pollParams = [
        uri: "https://api.lifx.com",
		path: "/v1/"+path+".json",
		headers: ["Content-Type": "application/x-www-form-urlencoded", "Authorization": "Bearer ${token}"],
        body: body
    ]
    
    log("URL = ${path}", "DEBUG")
    
    try {
        if(method=="GET") {
            httpGet(pollParams) { resp ->            
                parseResponse(resp)
            }
        } else if(method=="PUT") {
            httpPut(pollParams) { resp ->            
                parseResponse(resp)
            }
        } else if(method=="POST") {
            httpPost(pollParams) { resp ->            
                parseResponse(resp)
            }
        }
    } catch(Exception e) {
        log(e, "ERROR")
        if(e?.getMessage()?.toUpperCase() == "NOT FOUND") {
        	log("LIFX did not understand one of your group names. They need to match what is in your LIFX app and they are case sensitive.", "ERROR")
        } else if(e?.getMessage()?.toUpperCase() == "UNAUTHORIZED") {
        	log("The API token you entered is not correct and LFIX will not authorize your remote call.", "ERROR")
        }
    }
}

private sendMessageToLIFXWithResponse(path, method="GET", body=null) {
    def pollParams = [
        uri: "https://api.lifx.com",
		path: "/v1/"+path+".json",
        headers: ["Content-Type": "application/x-www-form-urlencoded", "Authorization": "Bearer ${token}"],
        body: body
    ]
    
    log("path = ${path}", "DEBUG")
    
    try {
        if(method=="GET") {
            httpGet(pollParams) { resp ->            
                parseResponsePoll(resp)
            }
        } else if(method=="PUT") {
            httpPut(pollParams) { resp ->            
                parseResponsePoll(resp)
            }
        }
        
        return resp
    } catch(Exception e) {
        log(e, "ERROR")
        if(e?.getMessage().toUpperCase() == "NOT FOUND") {
        	log("LIFX did not understand one of your group names. They need to match what is in your LIFX app and they are case sensitive.", "ERROR")
        } else if(e?.getMessage().toUpperCase() == "UNAUTHORIZED") {
        	log("The API token you entered is not correct and LFIX will not authorize your remote call.", "ERROR")
        }
    }
}

private parseResponse(resp) {
    if (resp.status == 404) {
		sendEvent(name: "switch", value: "unreachable")
        log("LIFX Service Unreachable!", "INFO")
		return []
	}
    
    def okResponses = 0
    
    log("resp = ${resp.data}", "DEBUG")
    
    resp.data.results.each { it->
    	if(it.status == "timed_out") {
        	log("Bulb ${it.label} has timmed out.", "ERROR")
        } else if(it.status == "ok") {
        	log("Bulb ${it.label} has updated successfully.", "DEBUG")
            okResponses++
        } else if(it.status == "offline") {
        	log("Bulb ${it.label} is offline.", "ERROR")
        }
    }
    
	log("${okResponses} of ${resp.data.results.size()} returned ok.", "INFO")
    updateLightStatus("${okResponses} of ${resp.data.results.size()}")  
}

private parseResponsePoll(resp) {
    log("Response: " + resp.data, "DEBUG")
    log("Response Size: " + resp.data.size, "DEBUG")
    
    if (resp.status == 404) {
		sendEvent(name: "switch", value: "unreachable")
        log("LIFX Service Unreachable!", "INFO")
		return []
	}
    
    def anyOn
    def anyOff
    
    resp.data.each {
    
    	log("IT = ${it.label} -- ${it.power}.", "DEBUG")
    	if(it.power == "on") {
        	anyOn = true
        } else if(it.power == "off") {
        	anyOff = true
        }
    }
    
    if(anyOn && anyOff) {
    	log("Some lights on, some off.", "DEBUG")
        sendEvent(name: "switch", value: "onish")
        reportPowerUsage()
    } else if(anyOn && !anyOff) {
    	log("All lights on.", "DEBUG")
		sendEvent(name: "switch", value: "on")
        reportPowerUsage()
    } else {
    	log("All lights off.", "DEBUG")
        sendEvent(name: "switch", value: "off")
        reportPowerUsage()
    }
}

def poll() {
	log("Begin poll.", "DEBUG")
    
    	buildGroupList()
    	sendMessageToLIFXWithResponse("lights/" + state.groupsList, "GET")
    
    log("End poll.", "DEBUG")
}

def parse(description) {
	log("Begin parse()", "DEBUG")
	log("description = ${description}.", "DEBUG")
    //sendEvent(name: "level", value: state.level)
    log("End parse().", "DEBUG")
}

def sceneOne() {
	log("Begin sceneOne().", "DEBUG")
    log("sceneOne(${scene01Brightness}, ${scene01Color}", "DEBUG")
    setScene(scene01Brightness, scene01Color)
	log("End sceneOne().", "DEBUG")
}

def sceneTwo() {
	log("Begin sceneTwo().", "DEBUG")
    log("sceneTwo(${scene02Brightness}, ${scene02Color}", "DEBUG")
    setScene(scene02Brightness, scene02Color)
	log("End sceneTwo().", "DEBUG")
}

def sceneThree() {
	log("Begin sceneThree().", "DEBUG")
    log("sceneThree(${scene03Brightness}, ${scene03Color}", "DEBUG")
    setScene(scene03Brightness, scene03Color)
	log("End sceneThree().", "DEBUG")
}

def sceneFour() {
	log("Begin sceneFour().", "DEBUG")
    log("sceneFour(${scene04Brightness}, ${scene04Color}", "DEBUG")
    setScene(scene04Brightness, scene04Color)
	log("End sceneFour().", "DEBUG")
}

def sceneFive() {
	log("Begin sceneFive().", "DEBUG")
    log("sceneFive(${scene05Brightness}, ${scene05Color}", "DEBUG")
    setScene(scene05Brightness, scene05Color)
	log("End sceneFive().", "DEBUG")
}

def setScene(brightness, temp) {
	log("Begin setScene(...)", "DEBUG")
    
    if(brightness != null && temp != null) {
    	def brightnessValue = brightness / 100
    	buildGroupList()
   	 	sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", ["color" : "${temp.toLowerCase()}+", "brightness" : "${brightnessValue}" ,"power" : "on"])
       
        sendEvent(name: "level", value: brightness)
	    sendEvent(name: "switch", value: "on")
        reportPowerUsage()
        
       if(temp.toLowerCase().startsWith("kelvin:")) {
       		temp = temp.toLowerCase().minus("kelvin:")
	        sendEvent(name: "colorTemp", value: temp)
			sendEvent(name: "color", value: "#ffffff")
			sendEvent(name: "saturation", value: 0)
            sendEvent(name: "colorTemperature", value: temp)
		    sendEvent(name: "level", value: "${state.level}")
        } else {
        	sendEvent(name: "color", value: getHex(temp))
        }
    }
    
    log("End setScene(...)", "DEBUG")
}

def runEffect(effect="pulse", color="blue", from_color="red", cycles=5, period=0.5, brightness=0.5) {
	log("runEffect(effect=${effect}, color=${color}: 1.0, from_color=${from_color}, cycles=${cycles}, period=${period}, brightness=${brightness}.", "INFO")

	if(effect != "pulse" && effect != "breathe") {
    	log("${effect} is not a value effect, defaulting to pulse.", "ERROR")
        effect = "pulse"
    }
	
    buildGroupList()
    log("The Group is: ${state.groupsList}", "DEBUG")
    sendMessageToLIFX("lights/${state.groupsList}/effects/${effect}", "POST", ["color" : "${color.toLowerCase()}+brightness:${brightness}", "from_color" : "${from_color.toLowerCase()}+brightness:${brightness}", "cycles" : "${cycles}" ,"period" : "${period}"])
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
    	log("Failed to unschedule!", "ERROR")
        log("Exception ${e}", "ERROR")
        return
    }

	log("useSchedule = ${useSchedule}.", "DEBUG")
    
    if(useSchedule) {
        
        try {
        	schedule("17 0/${frequency.toString()} ${startHour.toString()}-${endHour.toString()} * * ?", refresh)
            log("Refresh scheduled to run every ${frequency.toString()} minutes between hours ${startHour.toString()}-${endHour.toString()}.", "INFO")
        } catch(e) {
        	log("Failed to set schedule!", "ERROR")
            log("Exception ${e}", "ERROR")
        } 
    }
    
    log("End setupSchedule().", "DEBUG")
}

def updateLightStatus(lightStatus) {
	def finalString = lightStatus
    if(finalString == null) {
    	finalString = "--"
    }
	sendEvent(name: "lightStatus", value: finalString, display: false , displayed: false)
}

def determinePowerUsage() {
	
    log("switch = ${device.currentValue('switch')}.", "DEBUG")
    log("level = ${state.level}.", "DEBUG")
    
    def val = 0.0

    if(device.currentValue("switch") == "off") {
    	val =  0.7
    } else {
        if(state.level <= 100 && state.level >= 90) {
            val = (maxWatts * (1 - ((100 - state.level) * 0.02)))
        } else if(state.level < 90 && state.level >= 80) {
            val = maxWatts * (0.8 - ((90 - state.level) * 0.015))
        } else if(state.level < 80 && state.level >= 70) {
            val = maxWatts * (0.65 - ((80 - state.level) * 0.015))
        } else if(state.level < 70 && state.level >= 60) {
            val = maxWatts * (0.5 - ((70 - state.level) * 0.0135))
        } else if(state.level < 60 && state.level >= 50) {
            val = (maxWatts * (0.365 - ((60 - state.level) * 0.0145)))
        } else if(state.level < 50 && state.level >= 40) {
            val = (maxWatts * (0.22 - ((50 - state.level) * 0.0075)))
        } else {
            val = maxWatts / 11.0
        }
    }
    
    log("Power Reported: ${val * numberOfBulbs} watts or ${val} watts per bulb.", "INFO")
    log("val = ${val * numberOfBulbs}.", "DEBUG")
    return val * numberOfBulbs
}

def reportPowerUsage() {
	if(numberOfBulbs > 0) {
	def usage = determinePowerUsage()
	log("Power usage = ${usage}", "INFO")
	sendEvent(name: "power", value: "${usage}")
    sendEvent(name: "powerUsageText", value: "${usage}W, ${usage / numberOfBulbs}W per bulb (${numberOfBulbs}).")
    } else {
 		log("Power usage reporting turned off.", "DEBUG")   
    }
}