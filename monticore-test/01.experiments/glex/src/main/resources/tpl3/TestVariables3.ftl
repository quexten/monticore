<#-- (c) https://github.com/MontiCore/monticore -->
<#if glex.hasGlobalVar("v1")>
  A:ERROR
<#else>
  A:OK
</#if>
${glex.defineGlobalVar("v1",16)}
<#if glex.hasGlobalVar("v1")>
  B:OK
<#else>
  B:ERROR
</#if>
<#if glex.hasGlobalVar("ast")>
  C:OK
<#else>
  C:ERROR
</#if>
