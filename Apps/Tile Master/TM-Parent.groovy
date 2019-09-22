/**
 *  ****************  Tile Master Parent ****************
 *
 *  Design Usage:
 *  Create a tile with multiple devices and customization options.
 *
 *  Copyright 2019 Bryan Turcotte (@bptworld)
 *
 *  This App is free.  If you like and use this app, please be sure to give a shout out on the Hubitat forums to let
 *  people know that it exists!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  V2.0.3 - 09/22/19 - Added color options for Temperature and Battery Levels
 *  V2.0.2 - 09/21/19 - Added device value color options
 *  V2.0.1 - 09/20/19 - Initial release.
 *  V2.0.0 - 08/18/19 - Now App Watchdog compliant
 *  V1.0.0 - 02/16/19 - Initially started working on this concept but never released.
 *
 */

def setVersion(){
	if(logEnable) log.debug "In setVersion - App Watchdog Parent app code"
    // Must match the exact name used in the json file. ie. AppWatchdogParentVersion, AppWatchdogChildVersion
    state.appName = "TileMasterParentVersion"
	state.version = "v2.0.3"
    
    try {
        if(sendToAWSwitch && awDevice) {
            awInfo = "${state.appName}:${state.version}"
		    awDevice.sendAWinfoMap(awInfo)
            if(logEnable) log.debug "In setVersion - Info was sent to App Watchdog"
	    }
    } catch (e) { log.error "In setVersion - ${e}" }
}

definition(
    name:"Tile Master",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "Create a tile with multiple devices and customization options.",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
	importUrl: "https://raw.githubusercontent.com/bptworld/Hubitat/master/Apps/Tile%20Master/TM-Parent.groovy",
)

preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
} 

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childApps.size()} child apps"
    childApps.each {child ->
    	log.info "Child app: ${child.label}"
    }
    if(awDevice) schedule("0 0 3 ? * * *", setVersion)
}

def mainPage() {
    dynamicPage(name: "mainPage") {
    	installCheck()
		if(state.appInstalled == 'COMPLETE'){
			section(getFormat("title", "${app.label}")) {
				paragraph "<div style='color:#1A77C9'>Create a tile with multiple devices and customization options.</div>"
				paragraph getFormat("line")
			}
			section("Instructions:", hideable: true, hidden: true) {
				paragraph "<b>Information</b>"
				paragraph "Create a tile with multiple devices and customization options."
			}
			section(getFormat("header-green", "${getImage("Blank")}"+" Child Apps")) {
				app(name: "anyOpenApp", appName: "Tile Master Child", namespace: "BPTWorld", title: "<b>Add a new 'Tile Master' child</b>", multiple: true)
			}
            // ** App Watchdog Code **
            section("This app supports App Watchdog 2! Click here for more Information", hideable: true, hidden: true) {
				paragraph "<b>Information</b><br>See if any compatible app needs an update, all in one place!"
                paragraph "<b>Requirements</b><br> - Must install the app 'App Watchdog'. Please visit <a href='https://community.hubitat.com/t/release-app-watchdog/9952' target='_blank'>this page</a> for more information.<br> - When you are ready to go, turn on the switch below<br> - Then select 'App Watchdog Data' from the dropdown.<br> - That's it, you will now be notified automaticaly of updates."
                input(name: "sendToAWSwitch", type: "bool", defaultValue: "false", title: "Use App Watchdog to track this apps version info?", description: "Update App Watchdog", submitOnChange: "true")
			}
            if(sendToAWSwitch) {
                section(getFormat("header-green", "${getImage("Blank")}"+" App Watchdog 2")) {    
                    if(sendToAWSwitch) input(name: "awDevice", type: "capability.actuator", title: "Please select 'App Watchdog Data' from the dropdown", submitOnChange: true, required: true, multiple: false)
			        if(sendToAWSwitch && awDevice) setVersion()
                }
            }
            // ** End App Watchdog Code **
			section(getFormat("header-green", "${getImage("Blank")}"+" General")) {
       			label title: "Enter a name for parent app (optional)", required: false
 			}
            section(getFormat("header-green", "${getImage("Blank")}"+" Device Value Color Config")) {}
			section("Color Options:", hideable: true, hidden: false) {
                paragraph "Color is optional and is selectable within each child app. All child apps will get the values from here."
				paragraph "Enter in the colors you would like assigned to each value.<br>ie. Black, Blue, Brown, Green, Orange, Red, Yellow, White"
				input "colorOn", "text", title: "<span style='color: ${colorOn};font-size: 25px'>on</span>", submitOnChange: true, width: 6
                input "colorOff", "text", title: "<span style='color: ${colorOff};font-size: 25px'>off</span>", submitOnChange: true, width: 6
                
                input "colorOpen", "text", title: "<span style='color: ${colorOpen};font-size: 25px'>open</span>", submitOnChange: true, width: 6
                input "colorClosed", "text", title: "<span style='color: ${colorClosed};font-size: 25px'>closed</span>", submitOnChange: true, width: 6
                
                input "colorLocked", "text", title: "<span style='color: ${colorLock};font-size: 25px'>locked</span>", submitOnChange: true, width: 6
                input "colorUnlocked", "text", title: "<span style='color: ${colorUnlock};font-size: 25px'>unlocked</span>", submitOnChange: true, width: 6
                
                input "colorWet", "text", title: "<span style='color: ${colorWet};font-size: 25px'>wet</span>", submitOnChange: true, width: 6
                input "colorDry", "text", title: "<span style='color: ${colorDry};font-size: 25px'>dry</span>", submitOnChange: true, width: 6
                
                input "colorPresent", "text", title: "<span style='color: ${colorPresent};font-size: 25px'>present</span>", submitOnChange: true, width: 6
                input "colorNotPresent", "text", title: "<span style='color: ${colorNotPresent};font-size: 25px'>not present</span>", submitOnChange: true, width: 6
                
                paragraph "<b>Temperature Options</b>"
                input "colorT0to40", "text", title: "<span style='color: ${colorT0to40};font-size: 25px'>Temp <= 40</span>", submitOnChange: true, width: 4
                input "colorT41to70", "text", title: "<span style='color: ${colorT41to70};font-size: 25px'>Temp 41 to 70</span>", submitOnChange: true, width: 4
                input "colorT71to100", "text", title: "<span style='color: ${colorT71to100};font-size: 25px'>Temp >= 71</span>", submitOnChange: true, width: 4
                
                paragraph "<b>Battery Level Options</b>"
                input "colorB0to40", "text", title: "<span style='color: ${colorB0to40};font-size: 25px'>Batt Lvl <= 40</span>", submitOnChange: true, width: 4
                input "colorB41to70", "text", title: "<span style='color: ${colorB41to70};font-size: 25px'>Batt Lvl 41 to 70</span>", submitOnChange: true, width: 4
                input "colorB71to100", "text", title: "<span style='color: ${colorB71to100};font-size: 25px'>Batt Lvl >= 71</span>", submitOnChange: true, width: 4
                
                
            }
			display()
		}
	}
}

def installCheck(){
	state.appInstalled = app.getInstallationState() 
	if(state.appInstalled != 'COMPLETE'){
		section{paragraph "Please hit 'Done' to install '${app.label}' parent app "}
  	}
  	else{
    	log.info "Parent Installed OK"
  	}
}

def getImage(type) {				// Modified from @Stephack Code
    def loc = "<img src=https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    if(type == "Blank") return "${loc}blank.png height=40 width=5}>"
}

def getFormat(type, myText=""){		// Modified from @Stephack Code
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "\n<hr style='background-color:#1A77C9; height: 1px; border: 0;'></hr>"
	if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}

def display(){
    setVersion()
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center'>Tile Master - @BPTWorld<br><a href='https://github.com/bptworld/Hubitat' target='_blank'>Find more apps on my Github, just click here!</a><br>${state.version}</div>"
	}       
}         
