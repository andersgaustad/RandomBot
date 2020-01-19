# How to document new commands

## When adding new commands to this bot, remember to do the following:

1. Make sure the new command inherits from Command superclass
    1. Override the "name" field with an unique ID
2. Add the command to the JsonDetailedHelpObject under kotlin/json
3. In the Command class, insert the command and its ID in the getDetailedHelp method
4. Under resources/ do the following:
    1. Add the command to Help.txt under documentation/
    2. Add the name and description of the command to DetailedHelp.json under json/

