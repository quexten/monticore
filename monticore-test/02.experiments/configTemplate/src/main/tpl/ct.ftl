<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "hpService", "cd")}

<#-- Override the general empty method body with a warning -->
<#assign hpWarn = hpService.templateHP("WarnOnEmptyBody")>
${glex.replaceTemplate("core.EmptyBody", hpWarn)}


<#-- Override the specific sizeStates method of the automaton to always return 10 -->
<#assign sizeStateMethod = cd.getCDElementList()?first.getCDMemberList()[17]>
<#assign hpSize = hpService.templateHP("SizeStateTen")>
${glex.replaceTemplate("core.EmptyBody", sizeStateMethod, hpSize)}