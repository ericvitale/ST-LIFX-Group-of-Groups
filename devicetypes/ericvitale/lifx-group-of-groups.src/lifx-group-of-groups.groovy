/**
 *  LIFX Group of Groups
 *
 *  Copyright 2016 ericvitale@gmail.com
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
 *  You can find my other device handlers & SmartApps @ https://github.com/ericvitale
 *
 *  Some code borrowed from AdamV & Nicolas Cerveaux
 *
 */

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
        command "sceneOne"
        command "sceneTwo"
        command "sceneThree"
        
        attribute "colorName", "string"
    }
    
    preferences {
    	input "token", "text", title: "API Token", required: true
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
        input "scene01Brightness", "number", title: "Scene 1 Brightness", required: false
        input "scene01Color", "text", title: "Scene 1 Color/Kelvin", required: false, description: "Options: white, red, orange, yellow, cyan, green, blue, purple, pink, or kelvin:[2700-9000]"
        input "scene02Brightness", "number", title: "Scene 2 Brightness", required: false
        input "scene02Color", "text", title: "Scene 2 Color/Kelvin", required: false, description: "Options: white, red, orange, yellow, cyan, green, blue, purple, pink, or kelvin:[2700-9000]"
        input "scene03Brightness", "number", title: "Scene 3 Brightness", required: false
        input "scene03Color", "text", title: "Scene 3 Color/Kelvin", required: false, description: "Options: white, red, orange, yellow, cyan, green, blue, purple, pink, or kelvin:[2700-9000]"
        input "logging", "text", title: "Log Level", required: false, defaultValue: "INFO"
    }

    simulator {
    }
    
    tiles {
    	multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 3, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "onish", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ff0000", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#fffA62", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#fffA62", nextState:"turningOn"
			}
            
            tileAttribute ("device.level", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'${currentValue}%'
			}
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        		attributeState "default", action:"switch level.setLevel"
            }
        }
        
        valueTile("Brightness", "device.level", width: 2, height: 1) {
        	state "level", label: 'Brightness ${currentValue}%'
        }
        
        controlTile("levelSliderControl", "device.level", "slider", width: 4, height: 1) {
        	state "level", action:"switch level.setLevel"
        }

        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        valueTile("colorName", "device.colorName", height: 2, width: 4, inactiveLabel: false, decoration: "flat") {
            state "colorName", label: '${currentValue}'
        }
        
        valueTile("colorTemp", "device.colorTemperature", inactiveLabel: false, decoration: "flat", height: 1, width: 2) {
			state "colorTemp", label: '${currentValue}K'
		}
        
		controlTile("colorTempSliderControl", "device.colorTemperature", "slider", height: 1, width: 4, inactiveLabel: false, range:"(2700..9000)") {
			state "colorTemp", action:"color temperature.setColorTemperature"
		}
        
        controlTile("rgbSelector", "device.color", "color", height: 6, width: 6, inactiveLabel: false) {
            state "color", action:"setColor"
        }
        
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "default", label:"Refresh", action:"refresh.refresh", icon: "st.secondary.refresh"
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

        main(["switch"])
        details(["switch", "Brightness", "levelSliderControl", "colorTemp", "colorTempSliderControl", "rgbSelector", "poll", "sceneOne", "sceneTwo", "sceneThree", "refresh"])
    }
}

def groupList =  ""

def updated() {
	log("UPDATED", "DEBUG")
    buildGroupList()
}

def buildGroupList() {
	log("Begin method buildGroupList().", "DEBUG")

    try {
        state.groupsList = "group:" + group01 + ","
        
        if(group02 != null) {
        	state.groupsList = state.groupsList + "group:" + group02 + ","
        }
        
        if(group03 != null) {
            state.groupsList = state.groupsList + "group:" + group03 + ","
        }
        
        if(group04 != null) {
			state.groupsList = state.groupsList + "group:" + group04 + ","
        }
        
        if(group05 != null) {
            state.groupsList = state.groupsList + "group:" + group05 + ","
        }
                
        if(group06 != null) {
            state.groupsList = state.groupsList + "group:" + group06 + ","
        }
        
        if(group07 != null) {
            state.groupsList = state.groupsList + "group:" + group07 + ","
        }
        
        if(group08 != null) {
            state.groupsList = state.groupsList + "group:" + group08 + ","
        }
        
        if(group09 != null) {
            state.groupsList = state.groupsList + "group:" + group09 + ","
        }
        
        if(group10 != null) {
            state.groupsList = state.groupsList + "group:" + group10 + ","
        }
    } catch(e) {
    	log(e, "ERROR")
    }
    
    log("GroupsList = <<<${state.groupsList}>>>", "DEBUG")
    
    log("End method buildGroupList().", "DEBUG")
}

def determineLogLevel(data) {
	if(data.toUpperCase() == "TRACE") {
    	return 0
    } else if(data.toUpperCase() == "DEBUG") {
    	return 1
    } else if(data.toUpperCase() == "INFO") {
    	return 2
    } else if(data.toUpperCase() == "WARN") {
    	return 3
    } else {
    	return 4
    }
}

def log(data, type) {
    
    data = "LIFXGoG -- " + data
    
    try {
        if(determineLogLevel(type) >= determineLogLevel(logging)) {
            if(type.toUpperCase() == "TRACE") {
                log.trace "${data}"
            } else if(type.toUpperCase() == "DEBUG") {
                log.debug "${data}"
            } else if(type.toUpperCase() == "INFO") {
                log.info "${data}"
            } else if(type.toUpperCase() == "WARN") {
                log.warn "${data}"
            } else if(type.toUpperCase() == "ERROR") {
                log.error "${data}"
            } else {
                log.error "LFIXGs -- Invalid Log Setting"
            }
        }
    } catch(e) {
    	log.error ${e}
    }
}

def refresh() {
	log("Begin referesh.", "DEBUG")
    poll()
    log("End refresh.", "DEBUG")
}

def on() {
	log("Begin turning groups on", "DEBUG")
    buildGroupList()
    sendMessageToLIFX("lights/" + state.groupsList + ",/state", "PUT", "power=on&duration=1")
    sendEvent(name: "switch", value: "on")
    log("End turning grouops on", "DEBUG")
}

def off() {
	log("Begin turning groups off", "DEBUG")
    buildGroupList()
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", "power=off&duration=1")
    sendEvent(name: "switch", value: "off")
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
	}
	if (data.level == 0) {
		sendEvent(name: "level", value: 0)
		return off()
	}
    
    def brightness = data.level / 100
    
    buildGroupList()
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", ["brightness": brightness, "power": "on"])
    log("Response = ${resp}.", "DEBUG")

    sendEvent(name: 'level', value: value)
    sendEvent(name: 'switch', value: "on")
    
    log("End setting groups level to ${value}.", "DEBUG")
}

def setColor(value) {
	log("Begin setting groups color to ${value}.", "DEBUG")
    
    def data = [:]
    data.hue = value.hue
    data.saturation = value.saturation
    data.level = device.currentValue("level")
    
    buildGroupList()
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", [color: "saturation:${data.saturation / 100}+hue:${data.hue * 3.6}", "power": "on"])
    
    sendEvent(name: "hue", value: value.hue)
    sendEvent(name: "saturation", value: value.saturation)
    sendEvent(name: "color", value: value.hex)
    sendEvent(name: "switch", value: "on")
    
    log("End setting groups color to ${value}.", "DEBUG")
}

def setColorTemperature(value) {
	log("Begin setting groups color temperature to ${value}.", "DEBUG")
    
    buildGroupList()
    sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", [color: "kelvin:${value}", power: "on"])
            
	sendEvent(name: "colorTemperature", value: value)
	sendEvent(name: "color", value: "#ffffff")
	sendEvent(name: "saturation", value: 0)
    //sendEvent(name: "colorName", value: genericName)
    
    log("End setting groups color temperature to ${value}.", "DEBUG")
}

private sendMessageToLIFX(path, method="GET", body=null) {
    def pollParams = [
        uri: "https://api.lifx.com",
		path: "/v1/"+path+".json",
		headers: ["Content-Type": "application/x-www-form-urlencoded", "Authorization": "Bearer ${token}"],
        body: body
    ]
    
    try {
        if(method=="GET") {
            httpGet(pollParams) { resp ->            
                parseResponse(resp)
            }
        }else if(method=="PUT") {
            httpPut(pollParams) { resp ->            
                parseResponse(resp)
            }
        }
    } catch(Exception e) {
        log(e, "ERROR")
    }
}

private sendMessageToLIFXWithResponse(path, method="GET", body=null) {
    def pollParams = [
        uri: "https://api.lifx.com",
		path: "/v1/"+path+".json",
        headers: ["Content-Type": "application/x-www-form-urlencoded", "Authorization": "Bearer ${token}"],
        body: body
    ]
    
    try {
        if(method=="GET") {
            httpGet(pollParams) { resp ->            
                parseResponsePoll(resp)
            }
        }else if(method=="PUT") {
            httpPut(pollParams) { resp ->            
                parseResponsePoll(resp)
            }
        }
        
        return resp
    } catch(Exception e) {
        log(e, "ERROR")
    }
}

private parseResponse(resp) {
    if (resp.status == 404) {
		sendEvent(name: "switch", value: "unreachable")
        log("LIFX Service Unreachable!", "INFO")
		return []
	}
    
    else if (resp.data.results[0] != null) {
    	log("Results: "+resp.data.results[0], "DEBUG")
    } else {
        def data = resp.data[0]
        log.debug("Data: ${data}")

        sendEvent(name: "label", value: data.label)
        log("Label: ${data.label}", "DEBUG")
        sendEvent(name: "level", value: Math.round((data.brightness ?: 1) * 100))
        log("Label: ${Math.round((data.brightness ?: 1) * 100)}", "DEBUG")
        sendEvent(name: "switch.setLevel", value: Math.round((data.brightness ?: 1) * 100))
        log("Label: ${Math.round((data.brightness ?: 1) * 100)}", "DEBUG")
        sendEvent(name: "switch", value: data.connected ? data.power : "unreachable")
        sendEvent(name: "color", value: colorUtil.hslToHex((data.color.hue / 3.6) as int, (data.color.saturation * 100) as int))
        sendEvent(name: "hue", value: data.color.hue / 3.6)
        sendEvent(name: "saturation", value: data.color.saturation * 100)
        sendEvent(name: "colorTemperature", value: data.color.kelvin)
        sendEvent(name: "group", value: "${data.group.name}")

        return []
	}
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
    } else if(anyOn && !anyOff) {
    	log("All lights on.", "DEBUG")
		sendEvent(name: "switch", value: "on")
    } else {
    	log("All lights off.", "DEBUG")
        sendEvent(name: "switch", value: "off")
    }
}

def poll() {
	log("Begin poll.", "DEBUG")
    
    	buildGroupList()
    	sendMessageToLIFXWithResponse("lights/" + state.groupsList, "GET")
    
    log("End poll.", "DEBUG")
}

def parse() {
	log("Begin parse()", "DEBUG")
    poll()
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

def setScene(brightness, temp) {
	log("Begin setScene(...)", "DEBUG")
    
    if(brightness != null && temp != null) {
    	def brightnessValue = brightness / 100
    	buildGroupList()
   	 	sendMessageToLIFX("lights/" + state.groupsList + "/state", "PUT", ["color" : "${temp.toLowerCase()}", "brightness" : "${brightnessValue}" ,"power" : "on"])
       
        sendEvent(name: "level", value: brightness)
	    sendEvent(name: "switch", value: "on")
       
       if(temp.toLowerCase().startsWith("kelvin:")) {
	        sendEvent(name: "colorTemperature", value: value)
			sendEvent(name: "color", value: "#ffffff")
			sendEvent(name: "saturation", value: 0)
        } else {
        	sendEvent(name: "color", value: getHex(temp))
        }
    }
    
    log("End setScene(...)", "DEBUG")
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