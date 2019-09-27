<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "deSerName")}
Log.error("Unable to deserialize symbol attribute children of type ${attribute.printType()}. " +
"Please override the method ${deSerName}#deserialize${attribute.getName()?cap_first}(JsonObject) using the TOP mechanism!");
return null;