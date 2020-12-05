<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdInterface")}
/* (c) https://github.com/MontiCore/monticore */
${tc.include("core.Package")}

${tc.include("core.Imports")}

${tc.include("core.Annotations")}
${cdInterface.printModifier()} interface ${cdInterface.getName()} <#rt><#lt>
<#if !cdInterface.isEmptyInterface()>extends ${cdInterface.printInterfaces()} </#if>{


<#list cdInterface.getCDAttributeList() as attribute>
    ${tc.include("core.Attribute", attribute)}
</#list>

<#list cdInterface.getCDMethodList() as method>
  <#if !method.getModifier().isAbstract()>default </#if>${tc.include("core.Method", method)}
</#list>
}
